package whz.de.sportapp.db.repository.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import whz.de.sportapp.db.model.Exercise;
@Dao
public interface ExerciseDao {
    @Query("SELECT * FROM exercises WHERE training_id = :trainingId ORDER BY order_index ASC")
    LiveData<List<Exercise>> getExercisesForTraining(int trainingId);

    @Query("SELECT * FROM exercises WHERE training_id = :trainingId ORDER BY order_index ASC")
    List<Exercise> getExercisesForTrainingSync(int trainingId);




    @Insert
    void insertExercise(Exercise exercise);


}