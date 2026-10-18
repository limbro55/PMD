package com.example.pmd.bd;
import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
@Database(entities = {PlayerEntity.class, RecordEntity.class}, version = 1, exportSchema = false)
public abstract class GameDatabase extends RoomDatabase {
    public abstract GameDao gameDao();
    private static volatile GameDatabase instance;
    public static GameDatabase get(Context context) {
        if (instance == null) synchronized (GameDatabase.class) {
            if (instance == null) instance = Room.databaseBuilder(context.getApplicationContext(), GameDatabase.class, "bugs.db").build();
        }
        return instance;
    }
}
