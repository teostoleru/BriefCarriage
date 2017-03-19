package com.example.pc.accelerometer;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pc.accelerometer.Microcontroller.Microcontroller;
import com.squareup.okhttp.ResponseBody;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends Activity implements SensorEventListener{

    private TextView xText, yText, zText, directionText, exceptionText;
    private Sensor mySensor;
    private SensorManager SM;
    private double x, y, z;
    private long currentTime, timeThreshold;
    private double aveX, aveY, aveZ;
    private int noReads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create our Sensor Manager
        SM = (SensorManager)getSystemService(SENSOR_SERVICE);

        // Accelerometer Sensor
        mySensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Register sensor Listener
        SM.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);

        // Assign TextView
        xText = (TextView)findViewById(R.id.xText);
        yText = (TextView)findViewById(R.id.yText);
        zText = (TextView)findViewById(R.id.zText);
        directionText = (TextView)findViewById(R.id.directionText);
        exceptionText = (TextView)findViewById(R.id.exceptionText);

        x = 0; y = 0; z = 0;
        currentTime = System.currentTimeMillis();
        timeThreshold = 500;
        aveX = 0; aveY = 0; aveZ = 0;
        noReads = 0;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not in use
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        long newTime = System.currentTimeMillis();

        if (newTime - currentTime < timeThreshold){
            aveX += event.values[0];
            aveY += event.values[1];
            aveZ += event.values[2];
            noReads++;
        }

        else {
            double newX = aveX / noReads;
            double newY = aveY / noReads;
            double newZ = aveZ / noReads;
            double threshold = 0.5;
            String direction = "";

            double deltaX = x - newX;
            double deltaY = y - newY;
            double deltaZ = z - newZ;

            x = newX; y = newY; z = newZ;

            int moveForward, moveRight;

            if (deltaX > threshold) {
                direction += "Right ";
                moveRight = 1;
            }
            else moveRight = 0;

            if (deltaX < -threshold) {
                direction += "Left ";
                moveRight = -1;
            }

            if (deltaZ > threshold) {
                direction += "Forward ";
                moveForward = 1;
            }
            else moveForward = 0;

            if (deltaZ < -threshold) {
                direction += "Back ";
                moveForward = -1;
            }

            if (Math.abs(deltaX) < threshold && Math.abs(deltaY) < threshold)
                direction = "No movement";

            xText.setText("X: " + String.format("%.2f", x));
            yText.setText("Y: " + String.format("%.2f", y));
            zText.setText("Z: " + String.format("%.2f", z));
            directionText.setText("Direction: " + direction);
            exceptionText.setText(moveForward + " " + moveRight);

            aveX = 0; aveY = 0; aveZ = 0;
            noReads = 0;
            currentTime = newTime;

            try {
                String path = null;
                /*if (moveForward == 0 && moveRight == 0)
                    path = "/v1/devices/2d0047001047343339383037/Stop?access_token=d2e16d62d96dc77cde4e77ce31950e9a6650541d";

                if (moveForward == 1)
                    path = "/v1/devices/2d0047001047343339383037/Forward?access_token=d2e16d62d96dc77cde4e77ce31950e9a6650541d";

                if (moveForward == -1)
                    path = "https://api.particle.io/v1/devices/2d0047001047343339383037/Back?access_token=d2e16d62d96dc77cde4e77ce31950e9a6650541d";

                if (moveRight == 1)
                    path = "https://api.particle.io/v1/devices/2d0047001047343339383037/Right?access_token=d2e16d62d96dc77cde4e77ce31950e9a6650541d";

                if (moveRight == -1)
                    path = "https://api.particle.io/v1/devices/2d0047001047343339383037/Left?access_token=d2e16d62d96dc77cde4e77ce31950e9a6650541d";
                */
                makeRequest(path);
            }
            catch (Exception exception) {
                //exceptionText.setText("Here:" + exception.getMessage());
            }
        }
    }

    private void sendCommand() {

    }

    public String makeRequest(String path) throws Exception {

        RestAdapter retrofit = new RestAdapter.Builder()
                .setEndpoint("https://api.particle.io")
                .build();


        Microcontroller micro = retrofit.create(Microcontroller.class);
        directionText.setText("first");

        micro.forward(
                "1",
                new Callback<Response>() {
                    @Override
                    public void success(Response result, Response response) {
                        BufferedReader reader = null;
                        String output = "";

                        try {
                            reader = new BufferedReader(new InputStreamReader(result.getBody().in()));
                            output = reader.readLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        Toast.makeText(MainActivity.this, output, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(MainActivity.this, error.toString(),Toast.LENGTH_LONG).show();
                    }
                }
        );

        return "Here I got";
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}