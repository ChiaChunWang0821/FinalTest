package com.example.jolin.afinal;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class BluetoothActivity extends AppCompatActivity {
    /*----GUI Components----*/
    //private TextView mBluetoothStatus;
    private TextView mReadBuffer;
    //private Button mScanBtn;
    //private Button mOffBtn;
    private Button mListPairedDevicesBtn;
    //private Button mDiscoverBtn;
    private BluetoothAdapter mBTAdapter;
    private Set<BluetoothDevice> mPairedDevices;
    private ArrayAdapter<String> mBTArrayAdapter;
    private ListView mDevicesListView ;
    //private CheckBox mLED1;
    //private EditText inputdata;
    //private Button sendDevice;

    public Handler mHandler;
    // Our main handler that will receive callback notifications
    private ConnectedThread mConnectedThread;
    // bluetooth background worker thread to send and receive data
    private BluetoothSocket mBTSocket = null;
    // bi-directional client-to-client data path

    private static final UUID BTMODULEUUID = UUID.fromString
            ("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier

    // #defines for identifying shared types between calling functions
    private final static int REQUEST_ENABLE_BT = 1;
    // used to identify adding bluetooth names
    private final static int MESSAGE_READ = 2;
    // used in bluetooth handler to identify message update
    private final static int CONNECTING_STATUS = 3;
    // used in bluetooth handler to identify message status
    private  String _recieveData = "";
    // public ArrayList mynumber;

    public ArrayList<Integer> mynumber = new ArrayList();
    private Long startTime;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        //上方返回鍵
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        //初始化元件
        mListPairedDevicesBtn = (Button)findViewById(R.id.PairedBtn);
        mBTArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        mDevicesListView = (ListView)findViewById(R.id.devicesListView);
        mDevicesListView.setAdapter(mBTArrayAdapter); // assign model to view
        mDevicesListView.setOnItemClickListener(mDeviceClickListener);

        // 詢問藍芽裝置權限
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        mHandler = new Handler(){
            String readMessage = null;
            int data;
            public void handleMessage(android.os.Message msg){
                if(msg.what == MESSAGE_READ){ //收到MESSAGE_READ 開始接收資料
                    try {
                        readMessage = new String((byte[]) msg.obj, "UTF-8");
                        //過濾字串中的所有非數字字元
                        readMessage = readMessage.replaceAll("[^(a-zA-Z0-9\\u4e00-\\u9fa5)]", "");


                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    Session session = Session.getSession();
                    session.put("data", readMessage);
                }

                if(msg.what == CONNECTING_STATUS){
                    //收到CONNECTING_STATUS 顯示以下訊息
                    if(msg.arg1 == 1){
                        Toast.makeText(getApplicationContext(),"Connected to Device: "+ (String)(msg.obj),Toast.LENGTH_SHORT).show();
                        mDevicesListView.setVisibility(View.GONE);
                        mListPairedDevicesBtn.setVisibility(View.GONE);
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Connection Failed ", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };

        if (mBTArrayAdapter == null) {
            // Device does not support Bluetooth
            //mBluetoothStatus.setText("Status: ");
            Toast.makeText(getApplicationContext(),"Bluetooth device not found!",Toast.LENGTH_SHORT).show();
        }
        else {
            mListPairedDevicesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    listPairedDevices(v);
                }
            });
        }
    }

    //下面都是藍芽
    private void bluetoothOn(View view){
        if (!mBTAdapter.isEnabled()) {//如果藍芽沒開啟
            Intent enableBtIntent = new
                    Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);//跳出視窗
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            //開啟設定藍芽畫面
            // mBluetoothStatus.setText("Bluetooth enabled");
            Toast.makeText(getApplicationContext(),"Bluetooth turned on",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getApplicationContext(),"Bluetooth is already on",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent Data){
        // Check which request we're responding to
        if (requestCode == REQUEST_ENABLE_BT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(),"Enabled ",Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(getApplicationContext(),"Disabled ",Toast.LENGTH_SHORT).show();
        }
    }

    private void bluetoothOff(View view){
        mBTAdapter.disable(); // turn off bluetooth
        Toast.makeText(getApplicationContext(),"Bluetooth turned Off",
                Toast.LENGTH_SHORT).show();
    }

    private void discover(View view){
        // Check if the device is already discovering
        if(mBTAdapter.isDiscovering()){ //如果已經找到裝置
            mBTAdapter.cancelDiscovery(); //取消尋找
            Toast.makeText(getApplicationContext(),"Discovery  stopped",Toast.LENGTH_SHORT).show();
        }
        else{
            if(mBTAdapter.isEnabled()) { //如果沒找到裝置且已按下尋找
                mBTArrayAdapter.clear(); // clear items
                mBTAdapter.startDiscovery(); //開始尋找
                Toast.makeText(getApplicationContext(), "Discovery started",
                        Toast.LENGTH_SHORT).show();
                registerReceiver(blReceiver, new
                        IntentFilter(BluetoothDevice.ACTION_FOUND));
            }
            else{
                Toast.makeText(getApplicationContext(), "Bluetooth not on",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    final BroadcastReceiver blReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // add the name to the list
                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                mBTArrayAdapter.notifyDataSetChanged();
            }
        }
    };

    private void listPairedDevices(View view) {
        mPairedDevices = mBTAdapter.getBondedDevices();
        if (mBTAdapter.isEnabled()) {
            // put it's one to the adapter
            for (BluetoothDevice device : mPairedDevices) {
                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
            Toast.makeText(getApplicationContext(), "Show Paired Devices",
                    Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getApplicationContext(), "Bluetooth not on",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private AdapterView.OnItemClickListener mDeviceClickListener = new
            AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
                    if(!mBTAdapter.isEnabled()) {
                        Toast.makeText(getBaseContext(), "Bluetooth not on",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Toast.makeText(getBaseContext(), "Connecting...",Toast.LENGTH_SHORT).show();

                    String info = ((TextView) v).getText().toString();
                    final String address = info.substring(info.length() - 17);
                    final String name = info.substring(0,info.length() - 17);

                    new Thread() {
                        public void run() {
                            boolean fail = false;
                            //取得裝置MAC找到連接的藍芽裝置
                            BluetoothDevice device = mBTAdapter.getRemoteDevice(address);
                            try {
                                mBTSocket = createBluetoothSocket(device);
                                //建立藍芽socket
                            } catch (IOException e) {
                                fail = true;
                                Toast.makeText(getBaseContext(), "Socket creation failed",
                                        Toast.LENGTH_SHORT).show();
                            }
                            try {
                                mBTSocket.connect(); //建立藍芽連線
                            } catch (IOException e) {
                                try {
                                    fail = true;
                                    mBTSocket.close(); //關閉socket
                                    //開啟執行緒 顯示訊息
                                    mHandler.obtainMessage(CONNECTING_STATUS, -1, -1)
                                            .sendToTarget();
                                } catch (IOException e2) {
                                    //insert code to deal with this
                                    Toast.makeText(getBaseContext(), "Socket creation failed",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                            if(fail == false) {
                                //開啟執行緒用於傳輸及接收資料
                                mConnectedThread = new ConnectedThread(mBTSocket);
                                mConnectedThread.start();
                                //開啟新執行緒顯示連接裝置名稱
                                mHandler.obtainMessage(CONNECTING_STATUS, 1, -1, name)
                                        .sendToTarget();
                            }
                        }
                    }.start();

                }
            };

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws
            IOException {
        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connection with BT device using UUID
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.available();
                    if(bytes != 0) {
                        SystemClock.sleep(100);
                        //pause and wait for rest of data
                        bytes = mmInStream.available();
                        // how many bytes are ready to be read?
                        bytes = mmInStream.read(buffer, 0, bytes);
                        // record how many bytes we actually read
                        mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                                .sendToTarget(); // Send the obtained bytes to the UI activity
                    }
                } catch (IOException e) {
                    e.printStackTrace();

                    break;
                }
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }
}
