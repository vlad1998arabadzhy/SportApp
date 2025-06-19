package whz.de.sportapp.db;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DatabaseCallback extends RoomDatabase.Callback {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public void onCreate(@NonNull SupportSQLiteDatabase db) {
        super.onCreate(db);

        // Populate default trainings when database is created
        executor.execute(() -> {
            populateDefaultTrainings(db);
        });
    }

    private void populateDefaultTrainings(SupportSQLiteDatabase db) {
        try {
            // Insert Training 1: Cardio Endurance Training
            db.execSQL("INSERT INTO trainings (name, description, days, is_custom, is_predefined, created_at, times_completed, met_points) VALUES " +
                    "('Cardio Endurance Training', 'A comprehensive cardio workout focusing on endurance and cardiovascular health', " +
                    "'Monday, Wednesday, Friday', 0, 1, " + System.currentTimeMillis() + ", 0, 100)");

            // Insert Training 2: No-Equipment Strength Training
            db.execSQL("INSERT INTO trainings (name, description, days, is_custom, is_predefined, created_at, times_completed, met_points) VALUES " +
                    "('No-Equipment Strength Training', 'Bodyweight strength training focusing on major muscle groups: legs, back, core, chest, shoulders, and arms', " +
                    "'Tuesday, Thursday, Saturday', 0, 1, " + System.currentTimeMillis() + ", 0, 100)");

            // Insert exercises for Cardio Endurance Training (training_id = 1)
            insertCardioExercises(db);

            // Insert exercises for Strength Training (training_id = 2)
            insertStrengthExercises(db);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertCardioExercises(SupportSQLiteDatabase db) {
        // Cardio Endurance Training exercises
        String[] cardioExercises = {
                "INSERT INTO exercises (training_id, name, default_reps, default_duration, unit, category, is_selected, order_index) VALUES " +
                        "(1, 'Warm-up Jogging', 0, 300, 'SECONDS', 'Warm-up', 1, 0)",
                "INSERT INTO exercises (training_id, name, default_reps, default_duration, unit, category, is_selected, order_index) VALUES " +
                        "(1, 'Running in Place', 0, 180, 'SECONDS', 'Cardio', 1, 1)",
                "INSERT INTO exercises (training_id, name, default_reps, default_duration, unit, category, is_selected, order_index) VALUES " +
                        "(1, 'Jumping Jacks', 50, 0, 'REPS', 'Cardio', 1, 2)",
                "INSERT INTO exercises (training_id, name, default_reps, default_duration, unit, category, is_selected, order_index) VALUES " +
                        "(1, 'High Knees', 0, 120, 'SECONDS', 'Cardio', 1, 3)",
                "INSERT INTO exercises (training_id, name, default_reps, default_duration, unit, category, is_selected, order_index) VALUES " +
                        "(1, 'Butt Kicks', 0, 120, 'SECONDS', 'Cardio', 1, 4)",
                "INSERT INTO exercises (training_id, name, default_reps, default_duration, unit, category, is_selected, order_index) VALUES " +
                        "(1, 'Mountain Climbers', 30, 0, 'REPS', 'Cardio', 1, 5)",
                "INSERT INTO exercises (training_id, name, default_reps, default_duration, unit, category, is_selected, order_index) VALUES " +
                        "(1, 'Step-ups', 40, 0, 'REPS', 'Cardio', 1, 6)",
                "INSERT INTO exercises (training_id, name, default_reps, default_duration, unit, category, is_selected, order_index) VALUES " +
                        "(1, 'Cool-down Walking', 0, 300, 'SECONDS', 'Cool-down', 1, 7)"
        };

        for (String exercise : cardioExercises) {
            try {
                db.execSQL(exercise);
            } catch (Exception e){
                Log.println(Log.ERROR, "INSERTION ERROR", "");
                e.printStackTrace();
            }

        }
    }

    private void insertStrengthExercises(SupportSQLiteDatabase db) {
        // No-Equipment Strength Training exercises
        String[] strengthExercises = {
                "INSERT INTO exercises (training_id, name, default_reps, default_duration, unit, category, is_selected, order_index) VALUES " +
                        "(2, 'Dynamic Warm-up', 0, 180, 'SECONDS', 'Warm-up', 1, 0)",
                "INSERT INTO exercises (training_id, name, default_reps, default_duration, unit, category, is_selected, order_index) VALUES " +
                        "(2, 'Bodyweight Squats', 20, 0, 'REPS', 'Legs', 1, 1)",
                "INSERT INTO exercises (training_id, name, default_reps, default_duration, unit, category, is_selected, order_index) VALUES " +
                        "(2, 'Lunges (per leg)', 10, 0, 'REPS', 'Legs', 1, 2)",
                "INSERT INTO exercises (training_id, name, default_reps, default_duration, unit, category, is_selected, order_index) VALUES " +
                        "(2, 'Push-ups', 15, 0, 'REPS', 'Chest', 1, 3)",
                "INSERT INTO exercises (training_id, name, default_reps, default_duration, unit, category, is_selected, order_index) VALUES " +
                        "(2, 'Plank', 0, 60, 'SECONDS', 'Core', 1, 4)",
                "INSERT INTO exercises (training_id, name, default_reps, default_duration, unit, category, is_selected, order_index) VALUES " +
                        "(2, 'Supermans', 15, 0, 'REPS', 'Back', 1, 5)",
                "INSERT INTO exercises (training_id, name, default_reps, default_duration, unit, category, is_selected, order_index) VALUES " +
                        "(2, 'Chair Dips', 15, 0, 'REPS', 'Arms', 1, 6)",
                "INSERT INTO exercises (training_id, name, default_reps, default_duration, unit, category, is_selected, order_index) VALUES " +
                        "(2, 'Plank Shoulder Taps', 20, 0, 'REPS', 'Shoulders', 1, 7)",
                "INSERT INTO exercises (training_id, name, default_reps, default_duration, unit, category, is_selected, order_index) VALUES " +
                        "(2, 'Bicycle Crunches', 30, 0, 'REPS', 'Core', 1, 8)",
                "INSERT INTO exercises (training_id, name, default_reps, default_duration, unit, category, is_selected, order_index) VALUES " +
                        "(2, 'Cool-down Stretching', 0, 300, 'SECONDS', 'Cool-down', 1, 9)"
        };

        for (String exercise : strengthExercises) {
            try {
                db.execSQL(exercise);
            } catch (Exception e){
                Log.println(Log.ERROR, "INSERTION ERROR", "");
                e.printStackTrace();
            }

        }
    }
}
