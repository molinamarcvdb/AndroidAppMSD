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
    private long idCounter = 0; // Add this line
    private Long experimentId; // Add this line

    public SensorHandler(Long experimentId) { // Modify the constructor
        this.experimentId = experimentId;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        // called when new data available

        DataObject dObj = new DataObject();

        dObj.setId(idCounter++); // Set the id and increment the counter
        dObj.setExperimentId(experimentId); // Use the experimentId from the constructor
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

    public void setExperimentId(Long experimentId) {
        this.experimentId = experimentId;
    }

    public List<DataObject> getData() {
        return this.data;
    }
}
