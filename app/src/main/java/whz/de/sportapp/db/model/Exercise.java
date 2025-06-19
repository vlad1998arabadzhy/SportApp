package whz.de.sportapp.db.model;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "exercises")
public class Exercise {
    @PrimaryKey(autoGenerate = true)
    public int exerciseId;

    @ColumnInfo(name = "training_id")
    public int trainingId;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "default_reps")
    public int defaultReps;

    @ColumnInfo(name = "default_duration")
    public int defaultDuration; // in seconds

    @ColumnInfo(name = "unit")
    public String unit; // "REPS" or "SECONDS"

    @ColumnInfo(name = "category")
    public String category; //

    @ColumnInfo(name = "is_selected")
    public boolean isSelected;

    @ColumnInfo(name = "order_index")
    public int orderIndex;


    public Exercise() {
        this.isSelected = true;
    }

    public Exercise(int trainingId, String name, int defaultReps, int defaultDuration, String unit) {
        this();
        this.trainingId = trainingId;
        this.name = name;
        this.defaultReps = defaultReps;
        this.defaultDuration = defaultDuration;
        this.unit = unit;
    }
}