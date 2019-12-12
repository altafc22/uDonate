package gq.altafchaudhari.udonate.database.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

import gq.altafchaudhari.udonate.database.UserDatabase;
import gq.altafchaudhari.udonate.database.dao.UserDao;
import gq.altafchaudhari.udonate.model.User;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class UserRepository {

    private static final String TAG = "UserRepository";

    private UserDao userDao;
    private Flowable<List<User>> allUser;
    MutableLiveData<Boolean> isLoading = new MutableLiveData<>();


    public UserRepository(Application application){
        UserDatabase userDatabase = UserDatabase.getInstance(application);
        userDao = userDatabase.userDao();
    }

    //Get all Genre
    public Flowable<List<User>> getAllUsers(){
        return userDao.getAllUsers();
    }

    //Get Loading State
    public MutableLiveData<Boolean> getIsLoading(){
        return isLoading;
    }

    //Get user of specific id
    public Flowable<User> getUserById(int id){
        return userDao.getUserById(id);
    }

    //Insert User
    public void insertUser(final User user){
        isLoading.setValue(true);
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                userDao.insert(user);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe: Called");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: Called");
                        isLoading.setValue(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: "+e.getMessage());
                    }
                });
    }

    //Update User
    public void updateUser(final User user){
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                userDao.update(user);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe: Called");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: Called");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: Called");
                    }
                });
    }

    //Delete Users By Id
    public void deleteUser(final int id){
        isLoading.setValue(true);
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                userDao.deleteUserById(id);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe: Called");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: Called");
                        isLoading.setValue(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: "+e.getMessage());
                    }
                });
    }

    //Delete User
    public void deleteUser(final User user){
        isLoading.setValue(true);
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                userDao.delete(user);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe: Called");
                    }

                    @Override
                    public void onComplete() {

                        Log.d(TAG, "onComplete: Called");
                        deleteUser(user.getId());
                        isLoading.setValue(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: "+ e.getMessage());
                    }
                });
    }

    //Delete all Users
    public void deleteAllUsers(){

        isLoading.setValue(true);
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                userDao.deleteAllUsers();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe: Called");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: Called");
                        //deleteAllMovies();
                        isLoading.setValue(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: Called"+e.getMessage());
                    }
                });
    }

}