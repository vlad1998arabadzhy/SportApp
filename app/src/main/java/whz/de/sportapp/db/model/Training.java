package whz.de.sportapp.db.model;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity(tableName = "trainings")
public class Training {
    @PrimaryKey(autoGenerate = true)
    public int trainingId;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "days")
    public String days; // JSON string of days array


    @ColumnInfo(name = "is_custom")
    public boolean isCustom;

    @ColumnInfo(name = "is_predefined")
    public boolean isPredefined;

    @ColumnInfo(name = "created_at")
    public long createdAt;

    @ColumnInfo(name = "last_used_at")
    public Long lastUsedAt;

    @ColumnInfo(name = "times_completed")
    public int timesCompleted;

    @ColumnInfo(name="met_points")
    public int metPoints;//Points for  the whole training according to WHO

    public Training() {
        this.createdAt = System.currentTimeMillis();
        this.timesCompleted = 0;
    }

    public Training(String name, String description, String days, boolean isCustom) {
        this();
        this.name = name;
        this.description = description;
        this.days = days;
        this.isCustom = isCustom;
        this.isPredefined = !isCustom;
    }
}