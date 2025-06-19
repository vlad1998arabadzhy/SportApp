package whz.de.sportapp.db.repository.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import whz.de.sportapp.db.model.TrainingSession;
@Dao
public interface TrainingSessionDao {

    @Insert
    long insertSession(TrainingSession session);

    @Query("UPDATE training_sessions SET is_completed = 1, end_time = :endTime WHERE sessionId = :sessionId")
    void completeSession(int sessionId, long endTime);

    @Query("SELECT * FROM training_sessions ORDER BY date DESC")
    LiveData<List<TrainingSession>> getAllSessions();

    @Query("SELECT * FROM training_sessions WHERE(end_time>=:oneWeekAgo)")
    LiveData<List<TrainingSession>> getAllSessionsOfLastWeek(long oneWeekAgo);

    @Query("SELECT * FROM training_sessions ORDER BY date DESC LIMIT 10")
    LiveData<List<TrainingSession>> getRecentSessions();

    @Query("SELECT COUNT(*) FROM training_sessions WHERE is_completed = 1")
    LiveData<Integer> getCompletedSessionsCount();




}
