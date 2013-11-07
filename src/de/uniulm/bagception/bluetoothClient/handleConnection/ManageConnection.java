package de.uniulm.bagception.bluetoothClient.handleConnection;

import java.io.IOException;
import java.util.UUID;

import de.uniulm.bagception.bluetoothClient.UI.MainActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class ManageConnection extends AsyncTask<BluetoothDevice, Integer, Void> {

	private final String TAG = getClass().getName();
	private BluetoothSocket bluetoothSocket;
	private BluetoothDevice device;
	public BluetoothAdapter bluetoothAdapter;
	public static final String BT_UUID = "1bcc9340-2c29-11e3-8224-0800200c9a66";
	public String new_BLUETOOTH_DEVICE;

	public MainActivity context;

	public ManageConnection(MainActivity c) {
		context = c;
	}

	@Override
	protected Void doInBackground(BluetoothDevice... devices) {
		// TODO Auto-generated method stub
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		new_BLUETOOTH_DEVICE = devices[0].getName();
		this.device = devices[0];
		Log.d(TAG, new_BLUETOOTH_DEVICE + " is the device");
		bluetoothAdapter.cancelDiscovery();

		try {
			Log.d("So ein dreck", "bla " + devices[0]);
			bluetoothSocket = devices[0].createRfcommSocketToServiceRecord(UUID
					.fromString(BT_UUID));
		} catch (IOException e) {

			e.printStackTrace();
			cancel(true);
			return null;
		}

		try {
			// Connect the device through the socket
			bluetoothSocket.connect();
			Log.d("BT SOCKET OUTPUT", bluetoothSocket.toString());
		} catch (IOException connectException) {

			Log.d("Socket", "is not connected");
			connectException.printStackTrace();
			onCancelled();

			try {
				bluetoothSocket.close();
			} catch (IOException closeException) {
			}
			cancel(true);
			return null;
		}
		return null;
	}

	@Override
	protected void onCancelled() {
		Toast.makeText(context, "Error connecting socket", Toast.LENGTH_SHORT)
				.show();
		super.onCancelled();
	}

	public void sendString(String string) {
		try {
			Log.d("neuer BT SOCKET", bluetoothSocket + "");
			bluetoothSocket.getOutputStream().write(string.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// Handle disconnection
			e.printStackTrace();
		}
	}

	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		try {
			bluetoothSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		context.updatePairedDevices();
	}

}
