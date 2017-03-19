package com.example.pc.accelerometer;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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


public class MainActivity extends Activity implements SensorEventListener, View.OnClickListener{

    private TextView xText, yText, zText, directionText, exceptionText;
    private Button enableButton;
    private Sensor mySensor;
    private SensorManager SM;
    private double x, y, z;
    private boolean enableOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Create our Sensor Manager
        SM = (SensorManager)getSystemService(SENSOR_SERVICE);
        // Accelerometer Sensor
        mySensor = SM.getDefaultSensor(Sensor.TYPE_GRAVITY);
        // Register sensor Listener
        SM.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);
        // Assign TextView
        xText = (TextView)findViewById(R.id.xText);
        yText = (TextView)findViewById(R.id.yText);
        zText = (TextView)findViewById(R.id.zText);
        directionText = (TextView)findViewById(R.id.directionText);
        exceptionText = (TextView)findViewById(R.id.exceptionText);

        enableButton = (Button)findViewById(R.id.enableButton);
        enableButton.setOnClickListener(this);
        enableOn = false;

        x = 0; y = 0; z = 0;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not in use
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        double newX = event.values[0];
        double newY = event.values[1];
        double newZ = event.values[2];

        double threshold = 3.0;
        double referenceForward = 0.0;
        double referenceRight = 0.0;
        String direction = "";

        x = newX; y = newY; z = newZ;

        int moveForward, moveRight;

        if (x < referenceRight - threshold) {
            direction += "Right ";
            moveRight = 1;
        }
        else moveRight = 0;

        if (x > referenceRight + threshold) {
            direction += "Left ";
            moveRight = -1;
        }

        if (y > referenceForward + threshold) {
            direction += "Forward ";
            moveForward = 1;
        }
        else moveForward = 0;

        if (y < referenceForward - threshold) {
            direction += "Back ";
            moveForward = -1;
        }

        if (moveForward == 0 && moveRight == 0)
            direction = "No movement";

        xText.setText("X: " + String.format("%.2f", x));
        yText.setText("Y: " + String.format("%.2f", y));
        zText.setText("Z: " + String.format("%.2f", z));
        directionText.setText("Direction: " + direction);
        if (enableOn)
            exceptionText.setText("Control is activated");
        else
            exceptionText.setText("Control is off");

        try {
            makeRequest(moveForward, moveRight);
        }
        catch (Exception exception) {
            //exceptionText.setText("Here:" + exception.getMessage());
        }
    }

    public void onClick(View v)
    {
        if (v == enableButton) {
            enableOn = !enableOn;
        }
    }

    public String makeRequest(int moveForward, int moveRight) throws Exception {

        RestAdapter retrofit = new RestAdapter.Builder()
                .setEndpoint("https://api.particle.io")
                .build();

        Microcontroller micro = retrofit.create(Microcontroller.class);

        if (moveForward == 0 && moveRight == 0 || !enableOn)
            micro.stop(
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
                            Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
            );

        if (enableOn) {
            if (moveForward == 1)
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
                                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                );


            if (moveForward == -1)
                micro.back(
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
                                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                );


            if (moveRight == 1)
                micro.right(
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
                                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                );


            if (moveRight == -1)
                micro.left(
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
                                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                );
        }
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