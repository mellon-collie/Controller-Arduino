package bangalore.pes.controller;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.IntegerRes;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class DisplayActivity extends AppCompatActivity {
    private static final String TAG = "DisplayActivity"; // TAG for the class

    // Buttons
    Button GetData;
    Button Receive;
    Button Off;
    String number = "";
    String number2 = "";


    private BluetoothSocket btSocket = null; // for connecting android device with HC-05
    private boolean isBtConnected = false;
    BluetoothAdapter myBluetooth;

    // Used while receiving data from arduino
    List<String> list = new ArrayList<>();
    List<String> list2 = new ArrayList<>();

    float final_answer; // final value calculated using received data and inputted data.

    // to display the incoming data
    TextView incomingData1;
    TextView incomingData2;

    InputStream mmInStream = null; // to receive the data
    String incomingMessage;// holds the data received and converted to String from byte array

    TextView timerKeeper; // to display the timer in seconds

    AsyncTask DataReceive; // object to execute AsyncTask for receiving data.

    private boolean receive = true;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // UUID for connection

    String p1, p2, toolingFactor,name,address; // variables to hold the received parameter values, and the device name and address.


    private RelativeLayout mainLayout1,mainLayout2,mainLayout3; // objects needed for displaying graphs.
    
    private LineChart mChart1;
    private LineChart mChart2,mChart3; // objects for displaying three line charts.

    ProgressDialog dialog;

    int time = 0; // variable that stores time in seconds.
    int interval = 1000;

    Runnable timeRunnable; // Runnable object to display time in seconds.

    // The app crashes when the device is rotated. This override function takes care of the activity state when the configuration is changed (screen rotation)
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // prevents the screen from going to screen saver mode.
        // WRITE_SETTINGS permission has to be added in the manifest file.

        btSocket = null;

        // To receive all the data from the previous activity - parameter values and device name, address.
        Bundle bundle = getIntent().getExtras();
        p1 = bundle.getString("p1");
        p2 = bundle.getString("p2");
        toolingFactor = bundle.getString("toolingFactor");
        address = bundle.getString("address");
        name = bundle.getString("name");

        // initializing buttons
        GetData = (Button) findViewById(R.id.button9);
        Receive = (Button) findViewById(R.id.button);
        Off = (Button) findViewById(R.id.button2);

        // initializing textViews
        incomingData1 = (TextView) findViewById(R.id.textView);
        incomingData2 = (TextView) findViewById(R.id.textView10);
        timerKeeper=(TextView)findViewById(R.id.textView6);


        dialog = new ProgressDialog(DisplayActivity.this); // initializing progress dialog, which is displayed during connection process.
        dialog.setMessage("Connecting to "+name);
        dialog.show();

        new ConnectBT().execute(); // executes the connection AsyncTask, and establishes connection between android device and HC-05.

        // On clicking REQUEST button,
        GetData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Requesting data", Toast.LENGTH_SHORT).show();
                try {
                    // 7 is written in the outputstream.
                    btSocket.getOutputStream().write("7".toString().getBytes());// request to switch on led in arduino
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // On clicking RECEIVE button,
        Receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Handler handler = new Handler();
                // runnable object to receive data and display it on the screen
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        receive = true;
                        DataReceive = new ReceiveData().execute(); // executes AsyncTask for receiving data.

                        // displays two sets of data received from the arduino
                        incomingData1.setText(number);
                        incomingData2.setText(number2);

                        handler.postDelayed(this, 10); // updates the UI every 10 ms.


                    }
                };
                runnable.run(); // executes the runnable object.




            }
        });


        final Handler timeHandler = new Handler();
        // Runnable object to display time in seconds.
        timeRunnable = new Runnable() {
            @Override
            public void run() {
                time = time +interval;
                int secTime = time/1000;
                Log.d("secTime", Integer.toString(secTime));
                timerKeeper.setText(String.valueOf(secTime));
                timeHandler.postAtTime(this,System.currentTimeMillis()+interval); //absolute time at which the callback timeRunnable should run
                timeHandler.postDelayed(this,interval); //The delay (in milliseconds) until the Runnable will be executed.\



            }
        };
        timeRunnable.run();



        // On clicking OFF button,
        Off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    btSocket.getOutputStream().write("0".toString().getBytes());// request to switch off led in arduino
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });






        // initializing layouts for the three line graphs.
        mainLayout1 = (RelativeLayout) findViewById(R.id.relativeLayout1);
        mainLayout2 = (RelativeLayout) findViewById(R.id.relativeLayout3);
        mainLayout3 = (RelativeLayout) findViewById(R.id.relativeLayout2);

        // initializing the objects for the three line graphs, by giving 'this' context.
        mChart1 = new LineChart(this);
        mChart2 = new LineChart(this);
        mChart3 = new LineChart(this);

        // adds the line graphs to each of the layouts
        mainLayout1.addView(mChart1, new AbsListView.LayoutParams
                (AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT));
        mainLayout2.addView(mChart2, new AbsListView.LayoutParams
                (AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT));
        mainLayout3.addView(mChart3, new AbsListView.LayoutParams
                (AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT));

        // attributes for the three graphs
        mChart1.setNoDataText("no data for the moment.");

        mChart1.setHighlightPerDragEnabled(true);
        mChart1.setHighlightPerTapEnabled(true);
        mChart1.setTouchEnabled(true);

        mChart1.setDragEnabled(true);
        mChart1.setScaleEnabled(true);
        mChart1.setDrawGridBackground(false);

        mChart1.setPinchZoom(true);

        mChart1.setBackgroundColor(Color.LTGRAY);

        LineData data1 = new LineData();
        data1.setValueTextColor(Color.WHITE);


        mChart1.setData(data1);

        Legend l1 = mChart1.getLegend();

        l1.setForm(Legend.LegendForm.LINE);
        l1.setTextColor(Color.WHITE);

        XAxis x21 = mChart1.getXAxis();
        x21.setTextColor(Color.WHITE);
        x21.setDrawGridLines(false);
        x21.setAvoidFirstLastClipping(true);

        YAxis y21 = mChart1.getAxisLeft();
        y21.setTextColor(Color.WHITE);
        y21.setAxisMaxValue(3000f);
        y21.setDrawGridLines(true);

        YAxis y121 = mChart1.getAxisRight();
        y121.setEnabled(false);


        //mChart2
        mChart2.setNoDataText("no data for the moment.");

        mChart2.setHighlightPerDragEnabled(true);
        mChart2.setHighlightPerTapEnabled(true);
        mChart2.setTouchEnabled(true);

        mChart2.setDragEnabled(true);
        mChart2.setScaleEnabled(true);
        mChart2.setDrawGridBackground(false);

        mChart2.setPinchZoom(true);

        mChart2.setBackgroundColor(Color.LTGRAY);

        LineData data2 = new LineData();
        data2.setValueTextColor(Color.WHITE);


        mChart2.setData(data2);

        Legend l2 = mChart2.getLegend();

        l2.setForm(Legend.LegendForm.LINE);
        l2.setTextColor(Color.WHITE);

        XAxis x22 = mChart1.getXAxis();
        x22.setTextColor(Color.WHITE);
        x22.setDrawGridLines(false);
        x22.setAvoidFirstLastClipping(true);

        YAxis y22 = mChart1.getAxisLeft();
        y22.setTextColor(Color.WHITE);
        y22.setAxisMaxValue(3000f);
        y22.setDrawGridLines(true);

        YAxis y122 = mChart1.getAxisRight();
        y122.setEnabled(false);


        //mChart3
        mChart3.setNoDataText("no data for the moment.");

        mChart3.setHighlightPerDragEnabled(true);
        mChart3.setHighlightPerTapEnabled(true);
        mChart3.setTouchEnabled(true);

        mChart3.setDragEnabled(true);
        mChart3.setScaleEnabled(true);
        mChart3.setDrawGridBackground(false);

        mChart3.setPinchZoom(true);

        mChart3.setBackgroundColor(Color.LTGRAY);

        LineData data3 = new LineData();
        data3.setValueTextColor(Color.WHITE);


        mChart3.setData(data3);

        Legend l3 = mChart3.getLegend();

        l3.setForm(Legend.LegendForm.LINE);
        l3.setTextColor(Color.WHITE);

        XAxis x23 = mChart3.getXAxis();
        x23.setTextColor(Color.WHITE);
        x23.setDrawGridLines(false);
        x23.setAvoidFirstLastClipping(true);

        YAxis y23 = mChart3.getAxisLeft();
        y23.setTextColor(Color.WHITE);
        y23.setAxisMaxValue(3000f);
        y23.setDrawGridLines(true);

        YAxis y123 = mChart3.getAxisRight();
        y123.setEnabled(false);




    }

    // this override function makes sure the graph adds data all the time
    @Override
    protected void onResume() {
        super.onResume();
        //new ConnectBT().execute();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addEntry1();
                            addEntry2();
                            addEntry3();
                        }
                    });

                    try {
                        Thread.sleep(600);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    // for adding values to graph 1
    private void addEntry1() {
        LineData data1 = mChart1.getData();


        if (data1 != null) {
            LineDataSet set = (LineDataSet) data1.getDataSetByIndex(0);
            if (set == null) {
                set = createSet();
                data1.addDataSet(set);
            }

            data1.addXValue("");
            data1.addEntry(new Entry(final_answer, set.getEntryCount()), 0); // the calculated final_answer is plotted on the graph

            mChart1.notifyDataSetChanged();

            mChart1.setVisibleXRange(0, 6);

            mChart1.moveViewToX(data1.getXValCount() - 7);
        }
    }
    // for adding values to graph 2
    private void addEntry2() {
        LineData data2 = mChart2.getData();


        if (data2 != null) {
            LineDataSet set = (LineDataSet) data2.getDataSetByIndex(0);
            if (set == null) {
                set = createSet();
                data2.addDataSet(set);
            }

            data2.addXValue("");
            data2.addEntry(new Entry(final_answer, set.getEntryCount()), 0); // the calculated final_answer is plotted on the graph

            mChart2.notifyDataSetChanged();

            mChart2.setVisibleXRange(0, 6);

            mChart2.moveViewToX(data2.getXValCount() - 7);
        }
    }
    // for adding values to graph 3
    private void addEntry3() {
        LineData data3 = mChart3.getData();


        if (data3 != null) {
            LineDataSet set = (LineDataSet) data3.getDataSetByIndex(0);
            if (set == null) {
                set = createSet();
                data3.addDataSet(set);
            }

            data3.addXValue("");
            data3.addEntry(new Entry(final_answer, set.getEntryCount()), 0); // the calculated final_answer is plotted on the graph

            mChart3.notifyDataSetChanged();

            mChart3.setVisibleXRange(0, 6);

            mChart3.moveViewToX(data3.getXValCount() - 7);
        }
    }
     // TODO create three such functions for each of the graphs
    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "SPL Db");
        set.setDrawFilled(true);
        set.setDrawCubic(true);
        set.setCubicIntensity(0.2f);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.getHoloBlue());
        set.setLineWidth(2f);
        set.setCircleColor(ColorTemplate.getHoloBlue());
        set.setFillAlpha(65);
        set.setCircleRadius(4f);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(244, 117, 177));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(10f);

        return set;

    }

    // AsyncTask for connecting with the selected device
    private class ConnectBT extends AsyncTask<Void, Void, Void>
    {
        private boolean ConnectSuccess = true;
        @Override
        protected void onPreExecute() {
            Toast.makeText(getApplicationContext(), "Connecting....", Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(Void... devices)
        {
            try {
                if (btSocket == null || !isBtConnected) {
                    ActivityCompat.requestPermissions(DisplayActivity.this, new String[]{Manifest.permission.BLUETOOTH}, 1);
                    ActivityCompat.requestPermissions(DisplayActivity.this, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, 1);
                    ActivityCompat.requestPermissions(DisplayActivity.this, new String[]{Manifest.permission.BLUETOOTH_PRIVILEGED}, 1);

                    myBluetooth = BluetoothAdapter.getDefaultAdapter();

                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);

                    //connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection

                }
            } catch (IOException e) {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                Toast.makeText(getApplicationContext(), "Connection Failed", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Connection Successful", Toast.LENGTH_SHORT).show();
                isBtConnected = true;
            }
            dialog.dismiss();

            try {
                btSocket.getOutputStream().write("5".toString().getBytes()); // request to switch on led in arduino
            } catch (IOException e) {
                e.printStackTrace();
            }



        }
    }

    // AsyncTask for receiving data from the arduino
    protected class ReceiveData extends AsyncTask<Object, Object, Void> {


        @Override
        protected Void doInBackground(Object... voids) {
            if (receive == false)
                return null;
            else {
                try {
                    mmInStream = btSocket.getInputStream();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                byte[] buffer = new byte[1];
                int bytes = 0;
                int flag = 0;
                while (true) {
                    // Read from the InputStream
                    try {
                        if (mmInStream == null) {
                            Log.d("", "InputStream is null");
                        }
                        try {
                            bytes = mmInStream.read(buffer); // gets the data from the buffer in the form of bytes
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        incomingMessage = new String(buffer, 0, bytes);//converts bytes to string
                        // splits the data between 1 and B as data 1 and between B and C as data 2
                        if(incomingMessage.equals("A"))
                        {
                            try{
                            number2 = TextUtils.join("",list2).trim();}catch(NullPointerException e){number2 = "0";}
                           // Log.d("Number 2 :",number2);
                            flag = 1;
                            list2.clear();
                        }
                        if(!incomingMessage.equals("A")&& !incomingMessage.equals("B") && flag == 1)
                        {
                            list.add(incomingMessage);
                        }
                        if(incomingMessage.equals("B"))
                        {
                            flag = 2;
                            for(int i = 0;i<list.size();i++)
                            {
                                //Log.d("i =",""+i+" :"+list.get(i));
                            }
                            number = TextUtils.join("",list).trim();
                            //Log.d("Number 1 :",number);


                            list.clear();
                        }
                        if(!incomingMessage.equals("A")&& !incomingMessage.equals("B") && flag == 2)
                        {
                            list2.add(incomingMessage);


                        }






                    } catch (NoSuchElementException e) {
                        e.printStackTrace();
                    }


                    try {
                        // final value calculation
                        final_answer = Float.valueOf(p1)+Float.valueOf(p2)+Float.valueOf(toolingFactor)+Float.valueOf(number)+Float.valueOf(number2);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }


                }
            }

            //return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(getApplicationContext(), "Receiving Data", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onCancelled() {
            receive = false;
            try {
                mmInStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

    }
    private void dismissProgressDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        dismissProgressDialog();


    }
}

