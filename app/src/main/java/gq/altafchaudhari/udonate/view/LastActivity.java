package gq.altafchaudhari.udonate.view;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import gq.altafchaudhari.udonate.MyApplication;
import gq.altafchaudhari.udonate.R;

public class LastActivity extends AppCompatActivity {
    View decorView;
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last);
        decorView = getWindow().getDecorView();
        startCountDownTimer();

        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                hideSystemUI();
            }
        });
    }

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


    public void gotoPreviousActivity(View v)
    {
        finish();
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
    }

        @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus) {
            hideSystemUI();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(countDownTimer!=null)
            countDownTimer.cancel();
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
