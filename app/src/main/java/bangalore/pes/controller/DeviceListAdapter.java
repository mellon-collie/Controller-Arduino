package bangalore.pes.controller;

/**
 * Created by nisha on 19-07-2017.
 */

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import android.view.LayoutInflater;
import android.view.ViewGroup;


// This class basically defines how each list item should look like in the ListAvailableDevices class.

public class DeviceListAdapter extends ArrayAdapter<BluetoothDevice> {

    private LayoutInflater mLayoutInflater;
    private ArrayList<BluetoothDevice> mDevices;
    private int  mViewResourceId;

    public DeviceListAdapter(Context context, int tvResourceId, ArrayList<BluetoothDevice> devices){
        super(context, tvResourceId,devices);
        this.mDevices = devices;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mViewResourceId = tvResourceId;
    }

    public android.view.View getView(int position, android.view.View convertView, ViewGroup parent) {
        convertView = mLayoutInflater.inflate(mViewResourceId, null);

        BluetoothDevice device = mDevices.get(position);

        if (device != null) {
            TextView deviceName = (TextView) convertView.findViewById(R.id.tvDeviceName); // for device name
            TextView deviceAdress = (TextView) convertView.findViewById(R.id.tvDeviceAddress); // for device address

            if (deviceName != null) {
                deviceName.setText(device.getName());
            }
            if (deviceAdress != null) {
                deviceAdress.setText(device.getAddress());
            }
        }

        return convertView;
    }

}
