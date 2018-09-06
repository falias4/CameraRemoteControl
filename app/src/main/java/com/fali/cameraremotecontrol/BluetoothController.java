package com.fali.cameraremotecontrol;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Toby on 07.06.2016.
 */
public class BluetoothController {
    String address = null;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    String deviceName = "CameraRemoteControl";
    //SPP UUID. Look for it
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private MainActivity myActivity;

    public BluetoothController(MainActivity activity) {
        myActivity = activity;

        address = getAddressByDeviceName(deviceName);

        if(!address.isEmpty())
        {
            new ConnectBT().execute(); //Call the class to connect
        }
        else
        {
            Toast.makeText(myActivity, "Bluetooth device missing: \"CameraRemoteControl\"", Toast.LENGTH_LONG).show();
        }
    }

    public void reconnectBluetooth()
    {
        isBtConnected = false;
        if(!address.isEmpty())
        {
            new ConnectBT().execute(); //Call the class to connect
        }
        else
        {
            Toast.makeText(myActivity, "Bluetooth device missing: \"CameraRemoteControl\"", Toast.LENGTH_LONG).show();
        }
    }

    private String getAddressByDeviceName(String deviceName) {
        BluetoothAdapter myBluetooth = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices;
        String searchedAddress = "";

        if(myBluetooth == null)
        {
            //Show a mensag. that the device has no bluetooth adapter
            myActivity.msg("Bluetooth Device Not Available");

            //finish apk
            //finish();
        }
        else if(!myBluetooth.isEnabled())
        {
            //Ask to the user turn the bluetooth on
            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            myActivity.startActivityForResult(turnBTon,1);
        }
        pairedDevices = myBluetooth.getBondedDevices();
        if (pairedDevices.size()>0)
        {
            for(BluetoothDevice bt : pairedDevices)
            {
                if(bt.getName().equals(deviceName))
                    searchedAddress = bt.getAddress();
            }
        }

        return searchedAddress;
    }

    public void sendString(String value)
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write(value.getBytes());
                myActivity.msg("Sent: " + value);
            }
            catch (IOException e)
            {
                myActivity.msg("Error");
            }
        }
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute() {
            //progress = ProgressDialog.show(MainActivity.class, "Connecting...", "Please wait!!!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try {
                if (btSocket == null || !isBtConnected) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
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
                myActivity.msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                //finish();
            } else {
                myActivity.msg("Connected.");
                isBtConnected = true;
            }
        }
    }
}