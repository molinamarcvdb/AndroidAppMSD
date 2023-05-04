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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

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
            addButton.setEnabled(true);
            resetButton.setEnabled(true);
            startButton.setText("Start");
            stopCollectingData();
        }
    }

    private void startCollectingData() {
        for (Sensor sensor : selectedSensors) {
            sensorManager.registerListener(sensorHandler, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }


    }

    private void stopCollectingData() {
        sensorManager.unregisterListener(sensorHandler);
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