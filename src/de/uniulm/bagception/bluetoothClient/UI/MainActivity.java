package de.uniulm.bagception.bluetoothClient.UI;

import java.util.ArrayList;
import java.util.Set;

import com.example.addnewbag.R;

import de.uniulm.bagception.bluetoothClient.handleConnection.BluetoothDeviceArrayAdapter;
import de.uniulm.bagception.bluetoothClient.handleConnection.ManageConnection;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static final int REQUEST_ENABLE_BT = 1;
	private final String TAG = getClass().getName();
	public BluetoothAdapter bluetoothAdapter;
	public static final String BLUETOOTH_DEVICE = "de.uniulm.bagception.extras.bluetoothdevice";
	public static final String BT_UUID = "1bcc9340-2c29-11e3-8224-0800200c9a66";
	BluetoothManager bluetoothManager;
	BluetoothDevice device;
	ListView listviewDiscoveredDevices;
	ListView listviewPairedDevices;
	TextView noPairedDevices;
	ArrayAdapter<BluetoothDevice> arrayAdapterDiscoveredDevices;
	ArrayAdapter<BluetoothDevice> arrayAdapterPairedDevices;
	private final ArrayList<BluetoothDevice> discoveredDevices = new ArrayList<BluetoothDevice>();
	int checkListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		listviewPairedDevices = (ListView) findViewById(R.id.pairedDevices);
		arrayAdapterPairedDevices = new BluetoothDeviceArrayAdapter(this);
		listviewPairedDevices.setAdapter(arrayAdapterPairedDevices);

		listviewDiscoveredDevices = (ListView) findViewById(R.id.foundDevices);
		arrayAdapterDiscoveredDevices = new BluetoothDeviceArrayAdapter(this);
		listviewDiscoveredDevices.setAdapter(arrayAdapterDiscoveredDevices);
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		if (!bluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}

		updatePairedDevices();

		listviewDiscoveredDevices.setClickable(true);
		listviewDiscoveredDevices
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					public void onItemClick(android.widget.AdapterView<?> arg0,
							View arg1, int arg2, long arg3) {
						checkListView = 0;
						handleListViewDiscoveredDevicesClick();
					};
				});
		
		listviewPairedDevices.setClickable(true);
		listviewPairedDevices
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					public void onItemClick(android.widget.AdapterView<?> arg0,
							View arg1, int arg2, long arg3) {
						checkListView = 1;
						handleListViewPairedDevicesClick(arrayAdapterPairedDevices.getItem(arg2));
					};
				});
	}

	public void updatePairedDevices() {
		arrayAdapterPairedDevices.clear();
		Set<BluetoothDevice> pairedDevices = bluetoothAdapter
				.getBondedDevices();
		// If there are paired devices
		if (pairedDevices.size() > 0) {
			// Loop through paired devices
			for (BluetoothDevice device : pairedDevices) {
				// Add the name and address to an array adapter to show in a
				// ListView
				noPairedDevices = (TextView) findViewById(R.id.noPairedDevices);
				noPairedDevices.setVisibility(View.INVISIBLE);
				arrayAdapterPairedDevices.add(device);
				Log.d("Bereits da", "device: " + device);
			}
		} else {
			noPairedDevices = (TextView) findViewById(R.id.noPairedDevices);
			noPairedDevices.setVisibility(View.VISIBLE);
		}
	}

	public void handleListViewDiscoveredDevicesClick() {
		Log.d(TAG, "bla test");
		if (arrayAdapterDiscoveredDevices.getCount() > 0) {
			listviewDiscoveredDevices.setClickable(true);
			ManageConnection mg = new ManageConnection(MainActivity.this);
			mg.execute(device);
		} else {
			listviewDiscoveredDevices.setClickable(false);
		}
	}
	
	public void handleListViewPairedDevicesClick(BluetoothDevice device) {
		if (arrayAdapterPairedDevices.getCount() > 0) {
			listviewDiscoveredDevices.setClickable(true);
			ManageConnection mg = new ManageConnection(MainActivity.this);
			Log.d(TAG, device.getName() + "ist eigtl da");
			mg.execute(device);
		} else {
			listviewPairedDevices.setClickable(false);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// Register the BroadcastReceiver
		IntentFilter filter = new IntentFilter();
		filter.addAction(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothDevice.ACTION_UUID);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(mReceiver, filter);
		updatePairedDevices();
		arrayAdapterDiscoveredDevices.notifyDataSetChanged();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		unregisterReceiver(mReceiver);
	}

	public void onScanClick(View view) {
		discoveredDevices.clear();
		bluetoothAdapter.startDiscovery();
	}

	final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public synchronized void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			// When discovery finds a device
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// Get the BluetoothDevice object from the Intent
				Log.d(TAG, "Get Device");
				device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				Log.d(TAG, device + " the extra device");
				discoveredDevices.add(device);
				
			} else if (intent.getAction().equals(
					BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
				Log.d(TAG, "Nach UUIDs suchen");
				Log.d(TAG, intent.getAction() + " intent");
				Log.d(TAG, BluetoothDevice.ACTION_UUID + " uuid");
				// device discovery finished
				for (BluetoothDevice d : discoveredDevices) {
					Log.d(TAG, d + " device");
					d.fetchUuidsWithSdp();
				}
			} else if (intent.getAction().equals(BluetoothDevice.ACTION_UUID)) {
				Parcelable[] extras = intent
						.getParcelableArrayExtra(BluetoothDevice.EXTRA_UUID);
				ParcelUuid[] uuid = new ParcelUuid[extras.length];
				Log.d(TAG, device.getName());

				for (int i = 0; i < extras.length; i++) {
					uuid[i] = (ParcelUuid) extras[i];
					Log.d(TAG, " " + uuid[i].toString());

					if (uuid[i].getUuid().toString().equalsIgnoreCase(BT_UUID)) {
						Log.d(TAG, "add the device now");

						arrayAdapterDiscoveredDevices.add(device);
					}

				}

			}
		}
	};

}
