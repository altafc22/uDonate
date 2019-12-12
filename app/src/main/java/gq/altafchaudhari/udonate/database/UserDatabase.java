package gq.altafchaudhari.udonate.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import gq.altafchaudhari.udonate.database.dao.UserDao;
import gq.altafchaudhari.udonate.model.User;
import io.reactivex.annotations.NonNull;


@Database(entities = {User.class}, version = 1,exportSchema = false)
public abstract class UserDatabase extends RoomDatabase {

    private static UserDatabase instance;

    public abstract UserDao userDao();

    public static synchronized UserDatabase getInstance(Context context){
        if(instance==null){
            //If instance is null that's mean database is not created and create new database
            instance = Room.databaseBuilder(context.getApplicationContext(),UserDatabase.class,"movie_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallBack)
                    .build();
        }

        return instance;
    }

    private static RoomDatabase.Callback roomCallBack = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
        }
    };
}