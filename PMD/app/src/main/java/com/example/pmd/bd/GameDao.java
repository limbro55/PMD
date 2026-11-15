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
 @Query("SELECT MAX(score) FROM records WHERE playerId = :playerId") Integer getBestScore(long playerId);
 @Query("DELETE FROM records WHERE playerId = :playerId") void deleteRecordsForPlayer(long playerId);
 @Query("SELECT r.id, r.playedAt, p.fullName, r.score, r.hits, r.misses, r.difficulty, r.durationSeconds " +
         "FROM records r JOIN players p ON p.id = r.playerId " +
         "WHERE r.id = (SELECT r2.id FROM records r2 " +
         "WHERE r2.playerId = r.playerId ORDER BY r2.score DESC, r2.playedAt DESC, r2.id DESC LIMIT 1) ORDER BY r.score DESC, r.playedAt DESC") List<ScoreRow> getRecords();
}
