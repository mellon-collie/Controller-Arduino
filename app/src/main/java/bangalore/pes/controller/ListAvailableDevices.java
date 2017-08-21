package bangalore.pes.controller;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class ListAvailableDevices extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final String TAG ="ListAvailableDevices"; // TAG for the class

    // Buttons
    Button List;
    Button Go;

    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>(); // Initialization of arrayList to hold device information
    public DeviceListAdapter mDeviceListAdapter;
    // Object of class DeviceListAdapter. Consists of two text fields, one for name of device and the other for address of device.

    ListView lvNewDevices; // object which displays devices.

    String deviceAddress; //address of selected device.
    String deviceName; // name of selected device.
    public static String EXTRA_ADDRESS = "device_address"; // Key to carry address of selected device to next activity.
    public static String EXTRA_NAME = "device_name"; // Key to carry name of selected device to next activity.

    BluetoothAdapter mBluetoothAdapter; // object to enable bluetooth, discover devices etc.

    // Broadcast Receiver to get information about the nearby devices which have been discovered.
    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction(); //  discoverDevicesIntent received.

            if (action.equals(BluetoothDevice.ACTION_FOUND)){

                BluetoothDevice device = intent.getParcelableExtra (BluetoothDevice.EXTRA_DEVICE); // object holds the discovered device.
                mBTDevices.add(device); // Adds device to the list.

                // Initializing DeviceListAdapter object with context, the layout file path and the list to the displayed.
                mDeviceListAdapter = new DeviceListAdapter(context, R.layout.activity_discover, mBTDevices);

                lvNewDevices.setAdapter(mDeviceListAdapter); // Sets the object to the ListView object. Now, the list will be displayed in the activity.
            }
        }
    };

    // The app crashes when the device is rotated. This override function takes care of the activity state when the configuration is changed (screen rotation)
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toast.makeText(getApplicationContext(),"Select a device to connect",Toast.LENGTH_SHORT).show();
        setContentView(R.layout.activity_list_available_devices);

        //Initializing buttons
        List=(Button)findViewById(R.id.button5);
        Go=(Button)findViewById(R.id.button15);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); // initializing BluetoothAdapter object

        lvNewDevices = (ListView) findViewById(R.id.listView2); //Initializing ListView object
        lvNewDevices.setOnItemClickListener(ListAvailableDevices.this); //Making list items clickable

        // On clicking List Button
        List.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableBT(mBluetoothAdapter); // call to enable Bluetooth
                BTdiscoverable(mBluetoothAdapter); //call to make android discoverable


            }
        });

        // On clicking Go Button
        Go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent connection=new Intent(ListAvailableDevices.this,MainActivity.class);
                connection.putExtra(EXTRA_ADDRESS, deviceAddress);
                connection.putExtra(EXTRA_NAME,deviceName);
                startActivity(connection);
            }
        });









    }

    //function to enable Bluetooth
    public void enableBT(BluetoothAdapter mBluetoothAdapter)
    {

        if(mBluetoothAdapter == null){ // True when device does not support Bluetooth capabilities.
            Log.d(TAG, "enableDisableBT: Does not have BT capabilities.");
            Toast.makeText(getApplicationContext(),"Device does not support Bluetooth",Toast.LENGTH_SHORT).show();
        }
        if(!mBluetoothAdapter.isEnabled()){ // When Bluetooth hasn't been enabled.

            Log.d(TAG, "enableDisableBT: enabling BT.");

            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE); //intent to enable Bluetooth.
            startActivity(enableBTIntent);

            Toast.makeText(getApplicationContext(),"Bluetooth Enabled",Toast.LENGTH_SHORT).show();


        }

    }
    // function on make android device discoverable
    public void BTdiscoverable(BluetoothAdapter mBluetoothAdapter)
    {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE); //intent to make device discoverable.
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300); //set time for disocerability (300s)
        Toast.makeText(getApplicationContext(),"Device is discoverable for 300s",Toast.LENGTH_SHORT).show();


        if(mBluetoothAdapter.isDiscovering()){ // When the android device is already disocvering devices (happens when app has already run once before)
            mBluetoothAdapter.cancelDiscovery(); // Cancel discovering.
            Log.d(TAG, "btnDiscover: Canceling discovery.");

            ActivityCompat.requestPermissions(ListAvailableDevices.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            ActivityCompat.requestPermissions(ListAvailableDevices.this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1);
            //check BT permissions in manifest

            mBluetoothAdapter.startDiscovery(); //starts discovery for nearby devices.

        }
        if(!mBluetoothAdapter.isDiscovering()){
            ActivityCompat.requestPermissions(ListAvailableDevices.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            ActivityCompat.requestPermissions(ListAvailableDevices.this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1);
            //check BT permissions in manifest

            mBluetoothAdapter.startDiscovery();  //starts discovery for nearby devices.

        }
        IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND); // On finding device, this intent is created.
        registerReceiver(mBroadcastReceiver3, discoverDevicesIntent); // this intent is sent to Broadcast Receiver.
    }



    // Override function which is called when a list item is clicked. This function basically pairs with the selected device.

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        mBluetoothAdapter.cancelDiscovery(); // Cancel discovering so that pairing happens faster.

        deviceName = mBTDevices.get(i).getName(); // name of the selected device.
        deviceAddress = mBTDevices.get(i).getAddress(); // address of the selected device.
        mBTDevices.get(i).createBond(); //pair with the selected device.
        Toast.makeText(getApplicationContext(),"You have selected "+deviceName ,Toast.LENGTH_SHORT).show();

        // change selected item color to blue when clicked
        ((TextView)view.findViewById(R.id.tvDeviceName)).setTextColor(Color.BLUE);
        ((TextView)view.findViewById(R.id.tvDeviceAddress)).setTextColor(Color.BLUE);


        Intent connection=new Intent(ListAvailableDevices.this,MainActivity.class); //intent to go to MainActivity class
        connection.putExtra(EXTRA_ADDRESS, deviceAddress); // carries device address along with intent.
        connection.putExtra(EXTRA_NAME,deviceName); // carries device name along with intent.

    }

    // Override function called when activity is destroyed.
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver3);
    }
    // Override function called when activity resumes from previous state.
    @Override
    protected void onResume() {
        super.onResume();
    }
}
