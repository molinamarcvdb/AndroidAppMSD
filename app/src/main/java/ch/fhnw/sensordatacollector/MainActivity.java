package ch.fhnw.sensordatacollector;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {

    private SensorManager sensorManager;

    private List<Sensor> selectedSensors = new ArrayList<>();

    private SensorHandler sensorHandler = new SensorHandler();

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
            startCollectingData();
        } else {
            stopCollectingData();
            addButton.setEnabled(true);
            resetButton.setEnabled(true);
            startButton.setText("Start");
        }
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
        RequestQueue queue = Volley.newRequestQueue(this);

        TextView statusView = findViewById(R.id.statusLabel);
        String url = "http://192.168.251.215:8080/data";
        Gson gson = new Gson();
        String requestBody = gson.toJson(dObj);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        statusView.setText("call successful");
                        // TODO Upload data
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // do some error handling
                statusView.setText("call failed: " + error.getMessage());
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return requestBody.getBytes(StandardCharsets.UTF_8);
            }
        };

        queue.add(stringRequest);
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