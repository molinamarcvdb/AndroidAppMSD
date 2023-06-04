package ch.fhnw.sensordatacollector;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SensorHandler implements SensorEventListener {

    private List<DataObject> data = new ArrayList<>();

    public SensorHandler() {
        // setting up copnnection
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        // called when new data available

        DataObject dObj = new DataObject();

        dObj.setExperimentId(1l); // TODO: do not use fix value
        dObj.setSensorType(sensorEvent.sensor.getType());
        dObj.setSensorId(sensorEvent.sensor.getName());
        List<Float> data = new ArrayList<>();

        if (sensorEvent.values != null) {
            for (float x : sensorEvent.values) {
                data.add(x);
            }
        }

        dObj.setData(data);
        dObj.setTimestamp(sensorEvent.timestamp);
        dObj.setAccuracy(sensorEvent.accuracy);

        // send data to background service
        this.data.add(dObj);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public List<DataObject> getData() {
        return this.data;
    }
}
