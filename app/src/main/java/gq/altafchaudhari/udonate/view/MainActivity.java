package gq.altafchaudhari.udonate.view;

import
androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import es.dmoral.toasty.Toasty;
import gq.altafchaudhari.udonate.MyApplication;
import gq.altafchaudhari.udonate.R;
import gq.altafchaudhari.udonate.model.User;
import gq.altafchaudhari.udonate.viewmodel.UserViewModel;
import io.reactivex.disposables.CompositeDisposable;


public class MainActivity extends AppCompatActivity {
    EditText et_name,et_phone,et_email;
    ImageView iv_logo;

    public boolean isBtStatusRegistered =  false;
    MyApplication myApplication;
    static final String LOG_MAIN_ACTIVITY = "MainActivity";
    private static final int REQUEST_ENABLE_BT = 0;
    Handler btInitialHandler = null;
    boolean firstDataSend = false;
    View decorView;
    CountDownTimer countDownTimer;
    private int user_id;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private UserViewModel userActivityViewModel;

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        int x = (int)event.getX();
        int y = (int)event.getY();
        startCountDownTimer();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                System.out.println("DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                System.out.println("MOVE");
                break;
            case MotionEvent.ACTION_UP:
                System.out.println("UP");
                break;
        }
        return false;
    }

    //countdown
    private void startCountDownTimer()
    {
        if(countDownTimer!=null)
            countDownTimer.cancel();
        //180000
        countDownTimer = new CountDownTimer(MyApplication.timer_value, 1000) {

            public void onTick(long millisUntilFinished) {
                //mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
                //here you can have your logic to set text to edittext
                System.out.println("Remaining: "+millisUntilFinished);
            }
            public void onFinish() {
                //mTextField.setText("done!");

                finish();
            }
        }.start();
    }
    // countdown

    private void bindViews()
    {
        decorView = getWindow().getDecorView();
        myApplication = (MyApplication)getApplication();
        et_name = findViewById(R.id.et_name);
        et_phone = findViewById(R.id.et_phone);
        et_email = findViewById(R.id.et_email);
        iv_logo = findViewById(R.id.logo);
        userActivityViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
    }

    private final BroadcastReceiver btStatusReceiver = new BroadcastReceiver()  {
        @Override
        public void onReceive(Context context, Intent intent) throws NullPointerException{
            String action = intent.getAction();
            try {

                switch (action) {
                    case BluetoothDevice.ACTION_ACL_CONNECTED:
                        myApplication.bt_device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        Toasty.success(MainActivity.this,myApplication.bt_device.getName()+" Connected"
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
                                //myApplication.sendData("*A#");
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
                        Toasty.error(MainActivity.this,myApplication.bt_device.getName()+" Disconnected"
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
                Log.d(LOG_MAIN_ACTIVITY,"BT_STATUS_RECEIVER REGISTERED");
            }
        }
    };


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();
        startCountDownTimer();

        if (myApplication.sharedPreferences.getString("device_name", null) != null) {
            myApplication.startBtSearch();
        }else
        {
            showToast("Please connect app with paired bluetooth device.");
        }

        iv_logo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                Intent intent = new Intent(MainActivity.this, Settings.class);
                startActivity(intent);
                if (isBtStatusRegistered = true) {
                    unregisterReceiver(btStatusReceiver);
                    isBtStatusRegistered = false;
                    Log.d(LOG_MAIN_ACTIVITY, "BT_STATUS_RECEIVER UN-REGISTERED");
                }
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
    protected void onResume() {
        super.onResume();
        hideSystemUI();
        changeStatusBarColor();
        startCountDownTimer();
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
                Log.d(LOG_MAIN_ACTIVITY, "TRYING BT_STATUS_RECEIVER REGISTERED");
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
    protected void onPause() {
        super.onPause();
        if(isBtStatusRegistered)
        {
            unregisterReceiver(btStatusReceiver);
            isBtStatusRegistered= false;
        }
        if(countDownTimer!=null)
            countDownTimer.cancel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("App Status","Destroyed");

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(LOG_MAIN_ACTIVITY, "ACTIVITY RESULT");
        if (resultCode != RESULT_CANCELED) {
            System.out.println("Request Code: " + requestCode);
        }
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

    //show toast message
    private void showToast(String s) {
        Toasty.info(getApplicationContext(),s,Toasty.LENGTH_SHORT,true).show();
    }

    public void gotoNextActivity(View v)
    {
        if(et_name.length()<1)
            et_name.setError("Enter Name");
        else if(et_phone.length()<1)
            et_phone.setError("Enter Phone Number");
        else if(et_email.length()<1)
            et_email.setError("Enter Email ID");
        else if(!myApplication.isValidEmail(et_email.getText().toString()))
            et_email.setError("Enter Valid Email ID");
        else
        {
            myApplication.sendData("*A#");
            String name = et_name.getText().toString();
            String email = et_email.getText().toString();
            String phone = et_phone.getText().toString();

            User user = new User(name,phone,email);
            saveUser(user);
            myApplication.updateDonateCount(myApplication.getDonationCount()+1);
            startActivity(new Intent(MainActivity.this,LastActivity.class));
            finish();
        }
    }

    private void saveUser(User user)
    {
        User currentUser = user;
        //currentUser.setId(genre_id);
        userActivityViewModel.insert(currentUser);
    }
}
