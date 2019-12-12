package gq.altafchaudhari.udonate.view;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import java.util.List;

import gq.altafchaudhari.udonate.R;
import gq.altafchaudhari.udonate.adapter.UserAdapter;
import gq.altafchaudhari.udonate.model.User;
import gq.altafchaudhari.udonate.viewmodel.UserViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.Nullable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class UsersActivity extends AppCompatActivity {

    View decorView;
    private static final String TAG = "MoviesActivity_Tag";
    private static final int START_DELAY = 2;

    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private UserAdapter userAdapter;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private UserViewModel userActivityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        bindViews();


        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                hideSystemUI();
            }
        });


        //Check Loading State
        userActivityViewModel.getIsLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                Log.d(TAG, "onChanged: "+aBoolean);
                if (aBoolean!=null){
                    if (aBoolean){
                        mProgressBar.setVisibility(View.VISIBLE);
                    }
                    else {
                        mProgressBar.setVisibility(View.GONE);
                    }
                }
            }
        });

        Disposable disposable = userActivityViewModel.getAllUsers().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<User>>() {
                    @Override
                    public void accept(List<User> users) throws Exception {
                        Log.d(TAG, "accept: getAllMovies");
                        userAdapter = new UserAdapter(users);
                        mRecyclerView.setAdapter(userAdapter);
                        for (User user: users){
                            Log.d(TAG, "accept: "+user.getName());
                        }
                    }
                });
        compositeDisposable.add(disposable);
    }

    private void bindViews()
    {
        decorView = getWindow().getDecorView();
        mProgressBar = findViewById(R.id.progress);
        mRecyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false );
        //GridLayoutManager gridLayoutManager = new GridLayoutManager(this,3);
        mRecyclerView.setLayoutManager(layoutManager);/*
        mRecyclerView.setHasFixedSize(true);*/
        userActivityViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        hideSystemUI();
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

        changeStatusBarColor();
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

    public void gotoPreviousActivity(View v)
    {
        finish();
    }
}
