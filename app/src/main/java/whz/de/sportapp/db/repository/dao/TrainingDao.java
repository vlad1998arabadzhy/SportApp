package whz.de.sportapp.db.repository.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import whz.de.sportapp.db.model.Training;

@Dao
public interface TrainingDao {
    @Query("SELECT * FROM trainings ORDER BY created_at DESC")
    LiveData<List<Training>> getAllTrainings();


    @Query("SELECT * FROM trainings WHERE trainingId = :trainingId")
    LiveData<Training> getTrainingById(int trainingId);
    @Insert
    long insertTraining(Training training);

    @Query("SELECT SUM(met_points) FROM trainings WHERE(created_at >= :oneWeekAgo)")
    LiveData<Integer> getAllForTheLastWeek(long oneWeekAgo);


    @Query("UPDATE trainings SET times_completed = times_completed + 1, last_used_at = :lastUsedAt WHERE trainingId = :trainingId")
    void incrementTrainingUsage(int trainingId, long lastUsedAt);




}