package gq.altafchaudhari.udonate.view;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Set;

import es.dmoral.toasty.Toasty;
import gq.altafchaudhari.udonate.MyApplication;
import gq.altafchaudhari.udonate.R;
import gq.altafchaudhari.udonate.adapter.DeviceListAdapter;


public class Settings extends AppCompatActivity {


    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_DISCOVER_BT = 1;

    TextView mStatusBlueTv, tv_device_name;
    ImageView mBlueIv;
    TextView mOnBtn, mOffBtn, mDiscoverBtn, mPairedBtn,btnAllOnOff;
    MyApplication myApplication;
    BluetoothAdapter mBluetoothAdapter;
    ArrayList<BluetoothDevice> bluetoothDevices;
    public DeviceListAdapter deviceListAdapter;
    ListView listView;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    boolean resetButtons = false;

    String ip_address;
    int in_port,out_port;


    // Handler for writing messages to the Bluetooth connection
    Handler writeHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        changeStatusBarColor();

        mStatusBlueTv = findViewById(R.id.statusBluetoothTv);
        mBlueIv       = findViewById(R.id.bluetoothIv);
        mOnBtn        = findViewById(R.id.onBtn);
        mOffBtn       = findViewById(R.id.offBtn);
        mDiscoverBtn  = findViewById(R.id.discoverableBtn);
        mPairedBtn    = findViewById(R.id.pairedBtn);

        //adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothDevices = new ArrayList<>();
        myApplication = (MyApplication)getApplication();
        sharedPreferences = getSharedPreferences(myApplication.SP_NAME, Context.MODE_PRIVATE);
        myApplication.showToast = true;


        ip_address = myApplication.sharedPreferences.getString("ip_address", null);
        in_port = myApplication.sharedPreferences.getInt("in_port", 0);
        out_port = myApplication.sharedPreferences.getInt("out_port", 0);

        //check if bluetooth is available or not
        if (mBluetoothAdapter == null){
            mStatusBlueTv.setText("Bluetooth is not available");
        }
        else {
            mStatusBlueTv.setText("Bluetooth is available");
        }

        ///set image according to bluetooth status(on/off)
        if(mBluetoothAdapter!=null)
        {
            if (mBluetoothAdapter.isEnabled()){
                mBlueIv.setImageResource(R.drawable.ic_bluetooth_on);
            }
            else {
                mBlueIv.setImageResource(R.drawable.ic_bluetooth_off);
            }

            IntentFilter btStatusFilter = new IntentFilter();
            btStatusFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
            btStatusFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);

            Log.e("SETTINGS LOG","BT_STATUS RECEIVER REGISTERED");
            registerReceiver(btStatusReceiver, btStatusFilter);

            //discover bluetooth btn click
            mDiscoverBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mBluetoothAdapter.isDiscovering()){
                        showToast("Making Your Device Discoverable");
                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                        startActivityForResult(intent, REQUEST_DISCOVER_BT);
                    }
                }
            });

            //on btn click
            mOnBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mBluetoothAdapter.isEnabled()){
                        showToast("Turning On Bluetooth...");
                        //intent to on bluetooth
                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(intent, REQUEST_ENABLE_BT);
                    }
                    else {
                        showToast("Bluetooth is already on");
                    }
                }
            });

            //off btn click
            mOffBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mBluetoothAdapter.isEnabled()){
                        mBluetoothAdapter.disable();
                        showToast("Turning Bluetooth Off");
                        mBlueIv.setImageResource(R.drawable.ic_bluetooth_off);
                    }
                    else {
                        showToast("Bluetooth is already off");
                    }
                }
            });

            //get paired devices btn click
            mPairedBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadPairedDevices();
                    try{
                        showDialog(Settings.this);
                    }
                    catch (NullPointerException e)
                    {
                        showToast("No Paired Device Found");
                    }
                }
            });
        }
        else
        {
            //showToast("Bluetooth not supported");
        }

    }

    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        if(resetButtons==true) {
            Intent intent=new Intent();
            intent.putExtra("reset",true);
            setResult(1,intent);
            resetButtons = false;
        }
        else
        {
            Intent intent=new Intent();
            intent.putExtra("reset",false);
            setResult(1,intent);
            finish();
        }
    }

    private void loadPairedDevices() {
        if (mBluetoothAdapter.isEnabled()){
            bluetoothDevices.clear();
            Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
            for (BluetoothDevice device: devices){
                bluetoothDevices.add(device);
                System.out.println("\nDevice: " + device.getName()+ ", " + device);
            }
        }
        else {
            showToast("Turn on bluetooth to get paired devices");
        }
    }

    public void showDialog(Activity activity){

        final Dialog dialog = new Dialog(activity);
        // dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_listview);

        Button btndialog = (Button) dialog.findViewById(R.id.btndialog);

        btndialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        listView = (ListView) dialog.findViewById(R.id.listview);
        deviceListAdapter = new DeviceListAdapter(Settings.this,R.layout.list_item,bluetoothDevices);
        deviceListAdapter.notifyDataSetChanged();
        listView.setAdapter(deviceListAdapter);

        Handler btInitialHandler = null;

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //textView.setText("You have clicked : "+myImageNameList[position]);
                myApplication.showToast  = true;
                if(myApplication.btt!=null) {
                    myApplication.disconnectBt();
                }
                else
                {

                    editor = sharedPreferences.edit();
                    editor.putString("device_address",bluetoothDevices.get(position).getAddress());
                    editor.putString("device_name",bluetoothDevices.get(position).getName());
                    editor.commit();

                    System.out.println("Device Address: "+sharedPreferences.getString("device_address",null));
                    if(myApplication.btt==null)
                        myApplication.connectToBt();
                    myApplication.sendData("*C#");
                }
                dialog.dismiss();
                myApplication.showToast  = false;
            }
        });

        dialog.show();
    }

    //toast message function
    private void showToast(String msg){
        Toasty.info(this, msg, Toasty.LENGTH_SHORT,true).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK){
                    //bluetooth is on
                    mBlueIv.setImageResource(R.drawable.ic_bluetooth_on);
                    showToast("Bluetooth is on");
                }
                else {
                    //user denied to turn bluetooth on
                    showToast("could't on bluetooth");
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        try
        {
            unregisterReceiver(btStatusReceiver);
        }
        catch(IllegalArgumentException ex)
        {
            System.out.println("No Bluetooth Device");
        }
        Log.e("SETTINGS LOG","BT_STATUS RECEIVER UN-REGISTERED");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myApplication.showToast = false;
    }
    Handler btInitialHandler = null;
    boolean firstDataSend = false;
    private final BroadcastReceiver btStatusReceiver = new BroadcastReceiver()  {

        @Override
        public void onReceive(Context context, Intent intent) throws NullPointerException{
            String action = intent.getAction();
            try {

                switch (action) {
                    case BluetoothDevice.ACTION_ACL_CONNECTED:
                        myApplication.bt_device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        Toasty.success(Settings.this,myApplication.bt_device.getName()+" Connected"
                                ,Toasty.LENGTH_SHORT,true).show();
                        btInitialHandler = new Handler();
                        btInitialHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Do something after 5s = 5000ms
                                Log.d("MY APPLICATION", "trying to connect send Data From Setting");
                                myApplication.sendData("*C#");
                                firstDataSend = true;
                            }
                        }, 1000);

                        if(firstDataSend==true)
                        {
                            if(btInitialHandler!=null)
                            {
                                btInitialHandler.removeCallbacksAndMessages(null);
                            }
                            firstDataSend = false;
                        }
                        resetButtons = true;
                        break;
                    case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                        Toasty.error(Settings.this,myApplication.bt_device.getName()+" Disconnected"
                                ,Toasty.LENGTH_SHORT,true).show();
                        myApplication.bt_device  = null;
                        resetButtons = false;
                        firstDataSend = false;
                        break;
                }
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    };

    //Making notification bar transparent
    private void changeStatusBarColor() {
        View yourView = findViewById(R.id.settings_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (yourView != null) {
                yourView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.WHITE);
            //window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public void gotoPreviousActivity(View v)
    {
        if(resetButtons==true) {
            Intent intent=new Intent();
            intent.putExtra("reset",true);
            setResult(1,intent);
            resetButtons = false;
            finish();
        }
        else {
            Intent intent=new Intent();
            intent.putExtra("reset",false);
            setResult(1,intent);
            finish();
        }
    }

}