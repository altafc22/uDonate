package gq.altafchaudhari.udonate.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import gq.altafchaudhari.udonate.model.User;
import io.reactivex.Flowable;

@Dao
public interface UserDao {

    //Insert User
    @Insert
    void insert(User user);

    //Update existing user
    @Update
    void update(User user);

    //Delete existing user
    @Delete
    void delete(User user);

    //Delete all user from table;
    @Query("DELETE FROM user_table")
    void deleteAllUsers();

    //Get User under specific user
    @Query("SELECT * FROM user_table WHERE id LIKE :id")
    Flowable<User> getUserById(int id);
    //WHERE id==:id int genre_id

    @Query("SELECT * FROM user_table")
    Flowable<List<User>> getAllUsers();
    //WHERE id==:id int genre_id

    //Delete movies by genre
    @Query("DELETE FROM user_table WHERE id==:id")
    void deleteUserById(int id);

}