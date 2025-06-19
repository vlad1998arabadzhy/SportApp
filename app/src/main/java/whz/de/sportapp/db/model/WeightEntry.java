package whz.de.sportapp.db.model;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity(tableName = "weight_history")
public class WeightEntry {
    @PrimaryKey(autoGenerate = true)
    public int entryId;

    @ColumnInfo(name = "weight")
    public double weight;

    @ColumnInfo(name = "date")
    public long date;

    @ColumnInfo(name = "goal_id")
    public Integer goalId;

    @ColumnInfo(name = "note")
    public String note;

    public WeightEntry() {
        this.date = System.currentTimeMillis();
    }

    public WeightEntry(double weight, String note) {
        this();
        this.weight = weight;
        this.note = note;
    }

    public WeightEntry(double weight, int goalId, String note) {
        this(weight, note);
        this.goalId = goalId;
    }
}