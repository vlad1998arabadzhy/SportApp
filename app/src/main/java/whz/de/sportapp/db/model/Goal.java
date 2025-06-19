package whz.de.sportapp.db.model;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "goals")
public class Goal {
    @PrimaryKey(autoGenerate = true)
    public int goalId;

    @ColumnInfo(name = "goal_text")
    public String goalText;

    @ColumnInfo(name = "target_weight")
    public double targetWeight;

    @ColumnInfo(name = "current_weight")
    public double currentWeight;

    @ColumnInfo(name = "start_weight")
    public double startWeight;

    @ColumnInfo(name = "goal_type")
    public String goalType; // "LOSE_WEIGHT" or "GAIN_WEIGHT"

    @ColumnInfo(name = "weeks")
    public int weeks;

    @ColumnInfo(name = "kilos")//Kilos to lose or to gain
    public int kilos;

    @ColumnInfo(name = "is_completed")
    public boolean isCompleted;

    @ColumnInfo(name = "is_active")
    public boolean isActive;

    @ColumnInfo(name = "created_at")
    public long createdAt;

    @ColumnInfo(name = "completed_at")
    public Long completedAt;

    public Goal() {
        this.createdAt = System.currentTimeMillis();
        this.isActive = true;
        this.isCompleted = false;
    }
}