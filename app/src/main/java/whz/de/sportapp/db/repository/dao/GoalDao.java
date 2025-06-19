package whz.de.sportapp.db.repository.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import whz.de.sportapp.db.model.Goal;

@Dao
public interface GoalDao {
    @Query("SELECT * FROM goals ORDER BY created_at DESC")
    LiveData<List<Goal>> getAllGoals();

    @Query("SELECT * FROM goals WHERE is_active = 1 LIMIT 1")
    LiveData<Goal> getActiveGoal();

    @Query("SELECT * FROM goals WHERE is_active = 1 LIMIT 1")
    Goal getActiveGoalSync();

    @Insert
    long insertGoal(Goal goal);

    @Query("UPDATE goals SET is_completed = 1, is_active = 0, completed_at = :completedAt WHERE goalId = :goalId")
    void completeGoal(int goalId, long completedAt);

    @Query("UPDATE goals SET is_active = 0 WHERE is_active = 1")
    void deactivateAllGoals();

    @Query("UPDATE goals SET current_weight = :weight WHERE goalId = :goalId")
    void updateGoalCurrentWeight(int goalId, double weight);

    @Query("SELECT COUNT(*) FROM goals WHERE is_completed = 1")
    LiveData<Integer> getCompletedGoalsCount();
}