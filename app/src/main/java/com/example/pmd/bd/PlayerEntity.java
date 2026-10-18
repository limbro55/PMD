package com.example.pmd.bd;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;
@Entity(tableName = "players")
public class PlayerEntity {
    @PrimaryKey(autoGenerate = true) public long id;
    @NonNull public String fullName = "";
    @NonNull public String gender = "";
    public int course, difficulty, birthDay, birthMonth, birthYear;
    @NonNull public String zodiacSign = "";
    public PlayerEntity() {}
}
