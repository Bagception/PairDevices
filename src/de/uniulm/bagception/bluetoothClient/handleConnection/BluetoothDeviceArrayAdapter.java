package de.uniulm.bagception.bluetoothClient.handleConnection;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class BluetoothDeviceArrayAdapter extends ArrayAdapter<BluetoothDevice> {

	public BluetoothDeviceArrayAdapter(Context context) {
		super(context, android.R.layout.simple_list_item_1);
	}
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(android.R.layout.simple_list_item_1, null);
        }

        BluetoothDevice item = getItem(position);
        if (item!= null) {
            TextView itemView = (TextView) view.findViewById(android.R.id.text1);
            
            if (itemView != null) {
                itemView.setText(item.getName());
            }
         }

        return view;
    }

}
