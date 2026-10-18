package com.example.pmd.bd;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;
@Dao public interface GameDao {
    @Insert long insertPlayer(PlayerEntity player);
    @Query("SELECT * FROM players ORDER BY id DESC") List<PlayerEntity> getPlayers();
    @Query("SELECT * FROM players WHERE id = :id LIMIT 1") PlayerEntity getPlayer(long id);
    @Insert long insertRecord(RecordEntity record);
    @Query("SELECT r.id, r.playedAt, p.fullName, r.score, r.hits, r.misses, r.difficulty, r.durationSeconds FROM records r JOIN players p ON p.id = r.playerId ORDER BY r.score DESC, r.playedAt DESC") List<ScoreRow> getRecords();
}
