package gq.altafchaudhari.udonate;

import android.annotation.SuppressLint;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import es.dmoral.toasty.Toasty;


public class MyApplication extends Application {


    public static final UUID MY_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    public static String SP_NAME  = "udonate_sp";
    public SharedPreferences sharedPreferences;
    public SharedPreferences.Editor configEditor;
    public static final long timer_value = 180000;

    public BluetoothAdapter mBluetoothAdapter = null;
    public ArrayList<BluetoothDevice> bluetoothDevices;
    public BluetoothThread btt = null;
    public Handler writeHandler;
    public boolean showToast =false;
    public BluetoothDevice bt_device = null;
    Handler btSearchHandler = null;
    int donation_count = 0;

    @Override
    public void onCreate()
    {
        super.onCreate();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothDevices = new ArrayList<>();
        sharedPreferences = getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);

    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public int getDonationCount()
    {
        donation_count = sharedPreferences.getInt("donate_count",0);
        if(donation_count>30)
        {
            updateDonateCount(0);
            donation_count = 0;
        }
       return donation_count;
    }

    public void updateDonateCount(int count)
    {
        configEditor = sharedPreferences.edit();
        configEditor.putInt("donate_count",count);
        configEditor.commit();
    }


    @SuppressLint("HandlerLeak")
    public void connectToBt() {
        // Only one thread at a time
        if (btt != null) {
            Log.w("BLUETOOTH CONNECTION", "Already connected!");
            //Toasty.success(getApplicationContext(),"Connected",Toasty.LENGTH_SHORT,true).show();
            return;
        }

        // Initialize the Bluetooth thread, passing in a MAC address
        // and a Handler that will receive incoming messages
        btt = new BluetoothThread(sharedPreferences.getString("device_address",null), new Handler() {

            @Override
            public void handleMessage(Message message) {

                String s = (String) message.obj;

                // Do something with the message
                if (s.equals("CONNECTED")) {
                    System.out.println("Connected.");
                    //resetButtons = true;
                    //sendData("*00000000#");
                    Log.d("Main Activity",sharedPreferences.getString("device_name",null));
                } else if (s.equals("DISCONNECTED")) {
                    System.out.println("Disconnected.");
                    disconnectBt();
                    //if(showToast==true)
                    //   Toasty.error(getApplicationContext(),sharedPreferences.getString("device_name",null)+ " dddDisconnected",Toasty.LENGTH_SHORT,true).show();
                } else if (s.equals("CONNECTION FAILED")) {
                    System.out.println("Connection Failed.");
                    disconnectBt();
                    startBtSearch();
                    //resetButtons = true;
                    if(showToast==true)
                        Toasty.warning(getApplicationContext(),"Connection Failed.",Toasty.LENGTH_SHORT,true).show();
                } else {
                    System.out.println(s);
                }
            }
        });

        // Get the handler that is used to send messages
        writeHandler = btt.getWriteHandler();
        // Run the thread
        btt.start();

    }

    public void disconnectBt() {
        Log.v("Disconnect BT", "Disconnect button pressed.");
        if(btt != null) {
            btt.interrupt();
            btt = null;
        }
    }

    public void sendData(String command)
    {
        Log.d("Sending Command to bt", command);
        //TextView tv = (TextView)findViewById(R.id.writeField);
        String data = command;
        Message msg = Message.obtain();
        msg.obj = data;
        try{
            writeHandler.sendMessage(msg);
        }
        catch (Exception e)
        {
            Log.e("MY APPLICATION","EXCEPTION - "+e.toString());
        }
    }

    public void startBtSearch()
    {
        if(btSearchHandler!=null)
            btSearchHandler = null;

        btSearchHandler = new Handler();
        btSearchHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                Log.d("MY APPLICATION", "trying to connect Bluetooth after 10 seconds");
                connectToBt();
            }
        }, 1000);
    }
    public void stopBtSearch()
    {
        Log.d("MY APPLICATION", "BT SEARCH STOPPED");
        if(btSearchHandler!=null)
        {
            btSearchHandler.removeCallbacksAndMessages(null);
        }
    }

}