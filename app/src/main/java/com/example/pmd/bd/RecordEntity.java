package com.example.pmd.bd;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
@Entity(tableName = "records", foreignKeys = @ForeignKey(entity = PlayerEntity.class, parentColumns = "id", childColumns = "playerId", onDelete = ForeignKey.CASCADE), indices = {@Index("playerId")})
public class RecordEntity {
    @PrimaryKey(autoGenerate = true) public long id;
    public long playerId;
    public int score, hits, misses, difficulty, durationSeconds;
    public long playedAt;
    public RecordEntity() {}
}
