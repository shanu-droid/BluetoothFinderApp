package example.bluetoothfinderapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    TextView statusTextView;
    Button searchButton;
    ListView listView;
    ArrayList<String> deviceList = new ArrayList<>();
    ArrayList<String> addresses = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter ;

    BluetoothAdapter bluetoothAdapter;

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i("Action",action);
            if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                statusTextView.setText("Finished....");
                searchButton.setEnabled(true);
            }else if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String name = device.getName();
                String address = device.getAddress();
               String rssi = Integer.toString(intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE));
               Log.i("Device Found","Name: " + name + " Address: " + address + " RSSI: " + rssi );

               if(!addresses.contains(address)){
                   addresses.add(address);
                   String deviceString = "";
                   if( name == null || name.isEmpty()){
                       deviceString =  address + " - RSSI " + rssi + " dBm";
                   }else{
                       deviceString = name + " - RSSI " + rssi + " dBm";
                   }
                   deviceList.add(deviceString);
                   arrayAdapter.notifyDataSetChanged();
               }
            }
        }
    };
    public void searchClicked(View view) {
        deviceList.clear();
        addresses.clear();
        statusTextView.setText("Searching....");
        searchButton.setEnabled(false);
        bluetoothAdapter.startDiscovery();
        int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
    }


        @Override
       protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listview);
        searchButton = findViewById(R.id.searchButton);
        statusTextView = findViewById(R.id.statusTextView);

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, deviceList){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

               TextView textView=(TextView) super.getView(position, convertView, parent);

                /*YOUR CHOICE OF COLOR*/
                textView.setTextColor(Color.GRAY);
                return textView;
            }
        };


        listView.setAdapter(arrayAdapter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(broadcastReceiver,intentFilter);



    }
}
