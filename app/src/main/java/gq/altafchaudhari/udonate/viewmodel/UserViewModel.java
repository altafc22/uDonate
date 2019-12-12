package gq.altafchaudhari.udonate.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import gq.altafchaudhari.udonate.database.repository.UserRepository;
import gq.altafchaudhari.udonate.model.User;
import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;

public class UserViewModel extends AndroidViewModel {

    private UserRepository userRepository;


    public UserViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
    }

    //Get users by id
    public Flowable<User> getUser(int id){
        return userRepository.getUserById(id);
    }
    //Get all Users
    public Flowable<List<User>> getAllUsers(){
        return userRepository.getAllUsers();
    }

    //Get Loading State
    public MutableLiveData<Boolean> getIsLoading(){
        return userRepository.getIsLoading();
    }
    //Insert User
    public void insert(User user){
        userRepository.insertUser(user);
    }

    //Update User
    public void update(User user){
        userRepository.updateUser(user);
    }

    //Delete User
    public void delete(User User){
        userRepository.deleteUser(User);
    }

    //Delete All User
    public void deleteAllUsersById( int id){
        userRepository.deleteUser(id);
    }
}