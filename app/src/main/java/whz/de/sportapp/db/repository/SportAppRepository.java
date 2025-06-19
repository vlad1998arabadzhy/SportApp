package whz.de.sportapp.db.repository;

import android.app.Application;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import whz.de.sportapp.db.SportAppDatabase;
import whz.de.sportapp.db.model.Exercise;
import whz.de.sportapp.db.model.Goal;
import whz.de.sportapp.db.model.Training;
import whz.de.sportapp.db.model.TrainingSession;
import whz.de.sportapp.db.model.User;
import whz.de.sportapp.db.model.WeightEntry;
import whz.de.sportapp.db.repository.dao.ExerciseDao;
import whz.de.sportapp.db.repository.dao.GoalDao;
import whz.de.sportapp.db.repository.dao.TrainingDao;
import whz.de.sportapp.db.repository.dao.TrainingSessionDao;
import whz.de.sportapp.db.repository.dao.UserDao;
import whz.de.sportapp.db.repository.dao.WeightEntryDao;

public class SportAppRepository {

    private UserDao userDao;
    private GoalDao goalDao;
    private WeightEntryDao weightEntryDao;
    private TrainingDao trainingDao;
    private ExerciseDao exerciseDao;
    private TrainingSessionDao trainingSessionDao;

    private ExecutorService executor;

    public SportAppRepository(Application application) {
        SportAppDatabase database = SportAppDatabase.getDatabase(application);
        exerciseDao = database.exerciseDao();
        userDao = database.userDao();
        goalDao = database.goalDao();
        weightEntryDao = database.weightEntryDao();
        trainingDao = database.trainingDao();
        trainingSessionDao = database.trainingSessionDao();
        executor = SportAppDatabase.databaseWriteExecutor;
    }

    // USER METHODS

    public LiveData<User> getUser() {
        return userDao.getUser();
    }

    public void updateUser(User user) {
        executor.execute(() -> {
            User existingUser = userDao.getUserSync();
            if (existingUser != null) {
                user.uid = existingUser.uid;

                // Check if weight changed
                boolean weightChanged = existingUser.weight != user.weight;

                userDao.updateUser(user);

                // If weight changed, update active goal and create weight entry
                if (weightChanged) {
                    updateActiveGoalCurrentWeight(user.weight);
                    WeightEntry entry = new WeightEntry(user.weight, "Profile weight update");
                    weightEntryDao.insertWeightEntry(entry);
                }
            } else {
                userDao.insertUser(user);
            }
        });
    }

    public void updateWeight(double weight) {
        executor.execute(() -> {
            userDao.updateWeight(weight);
            updateActiveGoalCurrentWeight(weight);
            WeightEntry entry = new WeightEntry(weight, "Weight update");
            weightEntryDao.insertWeightEntry(entry);
        });
    }

    // Helper method to update active goal's current weight
    private void updateActiveGoalCurrentWeight(double newWeight) {
        Goal activeGoal = goalDao.getActiveGoalSync();
        if (activeGoal != null && activeGoal.isActive && !activeGoal.isCompleted) {
            goalDao.updateGoalCurrentWeight(activeGoal.goalId, newWeight);

            // Check if goal is completed
            checkGoalCompletion(activeGoal, newWeight);
        }
    }

    // Check if goal should be marked as completed
    private void checkGoalCompletion(Goal goal, double currentWeight) {
        boolean goalReached = false;

        if ("Lose weight".equals(goal.goalType)) {
            // Goal is reached if current weight is at or below target weight
            goalReached = currentWeight <= goal.targetWeight;
        } else if ("Gain weight".equals(goal.goalType)) {
            // Goal is reached if current weight is at or above target weight
            goalReached = currentWeight >= goal.targetWeight;
        }

        if (goalReached) {
            goalDao.completeGoal(goal.goalId, System.currentTimeMillis());
        }
    }

    // ===== GOAL METHODS =====

    public LiveData<Goal> getActiveGoal() {
        return goalDao.getActiveGoal();
    }


    public void createGoalWithKilos(String goalText, String goalType, double targetWeight, int weeks, int kilos) {
        executor.execute(() -> {
            User currentUser = userDao.getUserSync();
            if (currentUser != null) {
                Goal newGoal = new Goal();
                newGoal.goalText = goalText;
                newGoal.goalType = goalType;
                newGoal.targetWeight = targetWeight;
                newGoal.currentWeight = currentUser.weight;
                newGoal.startWeight = currentUser.weight;
                newGoal.weeks = weeks;
                newGoal.kilos = kilos; // Set the amount of kilos to lose/gain

                goalDao.deactivateAllGoals();
                goalDao.insertGoal(newGoal);
            }
        });
    }


    // ===== TRAINING METHODS =====

    public LiveData<List<Training>> getAllTrainings() {
        return trainingDao.getAllTrainings();
    }

    public LiveData<Training> getTrainingById(int trainingId) {
        return trainingDao.getTrainingById(trainingId);
    }

    public LiveData<Integer> calculatePointsOfAllTrainingsForLastWeek(){
        long currentDate= System.currentTimeMillis();
        long oneWeekAgo= currentDate- TimeUnit.DAYS.toMillis(7);
        return trainingDao.getAllForTheLastWeek(oneWeekAgo);
    }

    public void createCustomTrainingWithExercises(String name, String description, String days, List<Exercise> exercises) {
        executor.execute(() -> {
            Training training = new Training(name, description, days, true);

            long trainingId = trainingDao.insertTraining(training);

            // Add exercises to the training
            if (exercises != null && !exercises.isEmpty()) {
                for (int i = 0; i < exercises.size(); i++) {
                    Exercise exercise = exercises.get(i);
                    exercise.trainingId = (int) trainingId;
                    exercise.orderIndex = i;
                    exerciseDao.insertExercise(exercise);
                }
            }
        });
    }




    // ===== EXERCISE METHODS =====

    public LiveData<List<Exercise>> getExercisesForTraining(int trainingId) {
        return exerciseDao.getExercisesForTraining(trainingId);
    }





    // ===== TRAINING SESSION METHODS =====



    public void saveCompletedTrainingSession(int trainingId, String trainingName, String exercisesData) {
        executor.execute(() -> {
            // Create new training session
            TrainingSession session = new TrainingSession(trainingId, trainingName);
            session.startTime = System.currentTimeMillis() - (30 * 60 * 1000); // 30 minutes ago (approximate duration)
            session.endTime = System.currentTimeMillis();
            session.isCompleted = true;
            session.exercisesData = exercisesData;
            session.date = System.currentTimeMillis();

            // Save to database
            trainingSessionDao.insertSession(session);

            // Update training usage statistics
            trainingDao.incrementTrainingUsage(trainingId, System.currentTimeMillis());
        });

    }


    public LiveData<Integer> getCompletedSessionsCount() {
        return trainingSessionDao.getCompletedSessionsCount();
    }

    public LiveData<List<TrainingSession>> getAllSessions() {
        return trainingSessionDao.getAllSessions();
    }
    public String calculatePointsForLastWeek(){

        return new String();
    }

}