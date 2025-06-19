package whz.de.sportapp.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import whz.de.sportapp.db.model.Exercise;
import whz.de.sportapp.db.model.Goal;
import whz.de.sportapp.db.model.Training;
import whz.de.sportapp.db.model.TrainingSession;
import whz.de.sportapp.db.model.User;
import whz.de.sportapp.db.model.UserPreferences;
import whz.de.sportapp.db.model.WeightEntry;
import whz.de.sportapp.db.repository.dao.ExerciseDao;
import whz.de.sportapp.db.repository.dao.GoalDao;
import whz.de.sportapp.db.repository.dao.TrainingDao;
import whz.de.sportapp.db.repository.dao.TrainingSessionDao;
import whz.de.sportapp.db.repository.dao.UserDao;
import whz.de.sportapp.db.repository.dao.UserPreferencesDao;
import whz.de.sportapp.db.repository.dao.WeightEntryDao;

@Database(
        entities = {
                User.class,
                Goal.class,
                WeightEntry.class,
                Training.class,
                Exercise.class,
                TrainingSession.class,
                UserPreferences.class
        },
        version = 1,
        exportSchema = false
)
public abstract class SportAppDatabase extends RoomDatabase {

    // DAOs
    public abstract UserDao userDao();
    public abstract GoalDao goalDao();
    public abstract WeightEntryDao weightEntryDao();
    public abstract TrainingDao trainingDao();
    public abstract ExerciseDao exerciseDao();
    public abstract TrainingSessionDao trainingSessionDao();
    public abstract UserPreferencesDao userPreferencesDao();

    // Singleton instance
    private static volatile SportAppDatabase INSTANCE;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(4);

    public static SportAppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (SportAppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    SportAppDatabase.class,
                                    "sport_app_database_with_defaults_7.0"
                            )
                            .addCallback(new DatabaseCallback())
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}