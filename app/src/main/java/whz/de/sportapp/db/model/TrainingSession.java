package whz.de.sportapp.db.model;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "training_sessions")
public class TrainingSession {
    @PrimaryKey(autoGenerate = true)
    public int sessionId;

    @ColumnInfo(name = "training_id")
    public int trainingId;

    @ColumnInfo(name = "training_name")
    public String trainingName;

    @ColumnInfo(name = "date")
    public long date;

    @ColumnInfo(name = "start_time")
    public Long startTime;

    @ColumnInfo(name = "end_time")
    public Long endTime;

    @ColumnInfo(name = "is_completed")
    public boolean isCompleted;

    @ColumnInfo(name = "notes")
    public String notes;

    @ColumnInfo(name = "rating")
    public int rating; // 1-5 stars

    @ColumnInfo(name = "exercises_data")
    public String exercisesData; // JSON

    public TrainingSession() {
        this.date = System.currentTimeMillis();
        this.isCompleted = false;
    }

    public TrainingSession(int trainingId, String trainingName) {
        this();
        this.trainingId = trainingId;
        this.trainingName = trainingName;
    }
}