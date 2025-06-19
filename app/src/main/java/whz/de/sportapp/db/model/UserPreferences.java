package whz.de.sportapp.db.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_preferences")
public class UserPreferences {
    @PrimaryKey
    public int prefId = 1; // Always 1, since we have only one user

    @ColumnInfo(name = "goal_reminders")
    public boolean goalReminders;

    @ColumnInfo(name = "training_reminders")
    public boolean trainingReminders;

    @ColumnInfo(name = "progress_updates")
    public boolean progressUpdates;

    @ColumnInfo(name = "reminder_time")
    public String reminderTime;

    @ColumnInfo(name = "weight_unit")
    public String weightUnit;

    @ColumnInfo(name = "theme")
    public String theme;

    @ColumnInfo(name = "language")
    public String language;

    @ColumnInfo(name = "auto_backup")
    public boolean autoBackup;

    @ColumnInfo(name = "updated_at")
    public long updatedAt;

    public UserPreferences() {
        // Default values
        this.goalReminders = true;
        this.trainingReminders = true;
        this.progressUpdates = true;
        this.reminderTime = "09:00";
        this.weightUnit = "KG";
        this.theme = "LIGHT";
        this.language = "EN";
        this.autoBackup = true;
        this.updatedAt = System.currentTimeMillis();
    }
}