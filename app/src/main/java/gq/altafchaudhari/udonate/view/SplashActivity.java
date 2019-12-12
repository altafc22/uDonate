package gq.altafchaudhari.udonate.view;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import es.dmoral.toasty.Toasty;
import gq.altafchaudhari.udonate.MyApplication;
import gq.altafchaudhari.udonate.R;

public class SplashActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 0;
    Handler btInitialHandler = null;
    boolean firstDataSend = false;
    MyApplication myApplication;
    ImageView iv_logo;
    public boolean isBtStatusRegistered =  false;
    LottieAnimationView anim_view;
    static final String LOG_SPLASH_ACTIVITY = "SplashActivity";
    View decorView;
    TextView tv_donate_count;
    LinearLayout donate_count_layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        myApplication = (MyApplication)getApplication();
        iv_logo = findViewById(R.id.logo);
        anim_view = findViewById(R.id.anim_view);
        decorView = getWindow().getDecorView();
        tv_donate_count = findViewById(R.id.tv_donate_count);
        donate_count_layout = findViewById(R.id.donate_count_layout);

        if (myApplication.sharedPreferences.getString("device_name", null) != null) {
            myApplication.startBtSearch();
        }else
        {
            //myApplication.updateDonateCount(0);
            showToast("Please connect app with paired bluetooth device.");
        }

        tv_donate_count.setText(String.valueOf(myApplication.getDonationCount()));

        iv_logo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                Intent intent = new Intent(SplashActivity.this, Settings.class);
                startActivity(intent);
                if (isBtStatusRegistered = true) {
                    unregisterReceiver(btStatusReceiver);
                    isBtStatusRegistered = false;
                    Log.d(LOG_SPLASH_ACTIVITY, "BT_STATUS_RECEIVER UN-REGISTERED");
                }
                return false;
            }
        });

        anim_view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                Intent intent = new Intent(SplashActivity.this, UsersActivity.class);
                startActivity(intent);
                if (isBtStatusRegistered = true) {
                    unregisterReceiver(btStatusReceiver);
                    isBtStatusRegistered = false;
                    Log.d(LOG_SPLASH_ACTIVITY, "BT_STATUS_RECEIVER UN-REGISTERED");
                }
                return false;
            }
        });

        donate_count_layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                myApplication.updateDonateCount(0);
                tv_donate_count.setText(String.valueOf(myApplication.getDonationCount()));
                myApplication.sendData("*B#");
                return false;
            }
        });

        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                hideSystemUI();
            }
        });
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus) {
            hideSystemUI();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void hideSystemUI() {
      /*  View overlay = findViewById(R.id.main);

        overlay.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN);
        *//*requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/

        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    private final BroadcastReceiver btStatusReceiver = new BroadcastReceiver()  {
        @Override
        public void onReceive(Context context, Intent intent) throws NullPointerException{
            String action = intent.getAction();
            try {

                switch (action) {
                    case BluetoothDevice.ACTION_ACL_CONNECTED:
                        myApplication.bt_device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        Toasty.success(SplashActivity.this,myApplication.bt_device.getName()+" Connected"
                                ,Toasty.LENGTH_SHORT,true).show();

                        myApplication.stopBtSearch();
                        // new add
                        if(btInitialHandler!=null)
                            btInitialHandler = null;

                        btInitialHandler = new Handler();
                        btInitialHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Do something after 5s = 5000ms
                                Log.d("MY APPLICATION", "trying to connect Bluetooth after 10 seconds");
                                //allButtons();
                                firstDataSend = true;
                                myApplication.sendData("*C#");
                            }
                        }, 5000);
                        /*if(firstDataSend==true)
                        {
                            if(btInitialHandler!=null)
                            {
                                btInitialHandler.removeCallbacksAndMessages(null);
                            }
                            firstDataSend = false;
                        }*/
                        //end new
                        break;
                    case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                        Toasty.error(SplashActivity.this,myApplication.bt_device.getName()+" Disconnected"
                                ,Toasty.LENGTH_SHORT,true).show();
                        myApplication.disconnectBt();
                        if(myApplication.sharedPreferences.getString("device_address",null)!=null)
                            myApplication.startBtSearch();
                        else
                            showToast("Please connect app with paired bluetooth device.");
                        break;
                }

            }catch (Exception e)
            {
                e.printStackTrace();
            }
            finally {
                isBtStatusRegistered= true;
                Log.d(LOG_SPLASH_ACTIVITY,"BT_STATUS_RECEIVER REGISTERED");
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        if(isBtStatusRegistered)
        {
            unregisterReceiver(btStatusReceiver);
            isBtStatusRegistered= false;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();
        changeStatusBarColor();
        tv_donate_count.setText(String.valueOf(myApplication.getDonationCount()));
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        changeStatusBarColor();
        hideSystemUI();

        if (myApplication.mBluetoothAdapter!=null && !myApplication.mBluetoothAdapter.isEnabled()){
            showToast("Turning On Bluetooth...");
            //intent to on bluetooth
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_ENABLE_BT);
        }
        else {
            //myApplication.connectToBt();
            if(isBtStatusRegistered== false)
            {
                Log.d(LOG_SPLASH_ACTIVITY, "TRYING BT_STATUS_RECEIVER REGISTERED");
                IntentFilter btStatusFilter = new IntentFilter();
                btStatusFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
                btStatusFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
                registerReceiver(btStatusReceiver, btStatusFilter);
                isBtStatusRegistered = true;
            }
        }
        Log.d("App Status","Resume");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("App Status","Destroyed");

    }

    public void gotoNextActivity(View v)
    {
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
    }

    //show toast message
    private void showToast(String s) {
        Toasty.info(getApplicationContext(),s,Toasty.LENGTH_SHORT,true).show();
    }


    private void changeStatusBarColor() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }
}
