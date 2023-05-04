package ch.fhnw.sensordatacollector;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class SensorHandler implements SensorEventListener {

    private String serviceBaseUrl = "http://localhost:8080";

    public SensorHandler() {
        // setting up copnnection
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        // called when new data available

        // send data to background service
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
