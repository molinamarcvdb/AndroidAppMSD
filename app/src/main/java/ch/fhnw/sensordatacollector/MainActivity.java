package ch.fhnw.sensordatacollector;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.util.Log;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.toolbox.JsonObjectRequest;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {

    private SensorManager sensorManager;

    private List<Sensor> selectedSensors = new ArrayList<>();

    private SensorHandler sensorHandler = new SensorHandler(1L);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);

        Spinner sensorSpinner = findViewById(R.id.sensorspinner);
        List<String> sensorArray =  new ArrayList<>();

        for (Sensor sensor : sensorList) {
            sensorArray.add(sensor.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sensorArray);
        sensorSpinner.setAdapter(adapter);
    }

    public void addButtonClicked(View view) {
        // get selected sensor
        Spinner sensorSpinner = findViewById(R.id.sensorspinner);
        String sensorName = sensorSpinner.getSelectedItem().toString();

        // check if sensor is already in the list
        for (Sensor sensor : selectedSensors) {
            if (sensor.getName().equals(sensorName)) {
                // do nothing
                return;
            }
        }

        Sensor sensor = getSensor(sensorName);
        selectedSensors.add(sensor);
        updateSelectedSensorList();
    }

    public void startButtonClicked(View view) {

        Button startButton = (Button) findViewById(R.id.startbutton);
        Button addButton = (Button) findViewById(R.id.addbutton);
        Button resetButton = (Button) findViewById(R.id.resetbutton);
        if (startButton.getText().equals("Start")) {
            addButton.setEnabled(false);
            resetButton.setEnabled(false);
            startButton.setText("Stop");

            // Get the experiment name from the EditText
            EditText experimentNameEditText = findViewById(R.id.experimentName);
            String experimentName = experimentNameEditText.getText().toString();

            // Create the experiment
            createExperiment(experimentName);

            startCollectingData();
        } else {
            stopCollectingData();
            addButton.setEnabled(true);
            resetButton.setEnabled(true);
            startButton.setText("Start");
        }
    }

    private void createExperiment(String name) {
        // Instantiate the RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // Define the API endpoint URL.
        String url = "http://10.0.2.2:8080/experiment?name=" + name;

        // Create a request using StringRequest.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle the response from the server.
                        // Get the experiment id
                        getExperimentId(name);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle the error.
                        handleUploadError(error);
                    }
                });

        // Add the request to the RequestQueue.
        requestQueue.add(stringRequest);
    }

    private void getExperimentId(String name) {
        // Instantiate the RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // Define the API endpoint URL.
        String url = "http://10.0.2.2:8080/experiment?name=" + name;

        // Create a request using StringRequest.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle the response from the server.
                        // The response is the experiment id
                        Long experimentId = Long.parseLong(response);

                        // Set the experiment id for the data objects
                        sensorHandler.setExperimentId(experimentId);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle the error.
                        handleUploadError(error);
                    }
                });

        // Add the request to the RequestQueue.
        requestQueue.add(stringRequest);
    }



    private void startCollectingData() {

        sensorHandler.getData().clear();
        for (Sensor sensor : selectedSensors) {
            sensorManager.registerListener(sensorHandler, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }


    }
    private void stopCollectingData() {

        sensorManager.unregisterListener(sensorHandler);

        // upload data
        List<DataObject> dataToUpload = sensorHandler.getData();

        for (DataObject dObj : dataToUpload) {
            uploadData(dObj);
        }
    }

    private void uploadData(DataObject dObj) {
        // Instantiate the RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // Define the API endpoint URL.
        String url = "http://10.0.2.2:8080/data?experimentid=" + dObj.getExperimentId(); // Replace with your actual API endpoint URL.

        Log.d("DataObject", "Sending DataObject with id: " + dObj.getDevice());

        // Create a JSONObject and set the properties from the DataObject.
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", dObj.getId());
            jsonObject.put("device", dObj.getDevice());
            jsonObject.put("experimentId", dObj.getExperimentId());
            jsonObject.put("experimentName", dObj.getExperimentName());
            jsonObject.put("sensorId", dObj.getSensorId());
            jsonObject.put("data", new JSONArray(dObj.getData()));
            jsonObject.put("timestamp", dObj.getTimestamp());
            jsonObject.put("accuracy", dObj.getAccuracy());
            jsonObject.put("sensorType", dObj.getSensorType());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create a request using JsonObjectRequest.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Handle the response from the server.
                        handleUploadSuccess(response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle the error.
                        handleUploadError(error);
                    }
                });

        // Add the request to the RequestQueue.
        requestQueue.add(jsonObjectRequest);
    }



    private void handleUploadSuccess(String response) {
        // Handle the successful upload response here
        // Update UI, display a success message, etc.
        TextView statusView = findViewById(R.id.statusLabel);
        statusView.setText("Upload successful");
    }

    private void handleUploadError(VolleyError error) {
        // Handle the upload error here
        // Update UI, display an error message, etc.
        TextView statusView = findViewById(R.id.statusLabel);
        statusView.setText("Upload failed: " + error.getMessage());
    }


    public void resetButtonClicked(View view) {
        selectedSensors.clear();
        updateSelectedSensorList();
    }

    private void updateSelectedSensorList() {
        StringBuilder sensorText = new StringBuilder();
        for (Sensor sensor : selectedSensors) {
            sensorText.append(sensor.getName()).append(System.getProperty("line.separator"));
        }
        TextView sensorTextView = (TextView) findViewById(R.id.selectedsensors);
        sensorTextView.setText(sensorText);
    }

    private Sensor getSensor(String sensorName) {
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor sensor : sensorList) {
            if (sensor.getName().equals(sensorName)) {
                return sensor;
            }
        }
        // sensor not found
        return null;
    }

}