package whz.de.sportapp.db.model;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_profile")
public class User {
    @PrimaryKey
    public int uid = 1; // always =1 since we have only one user

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "age")
    public int age;

    @ColumnInfo(name = "weight")
    public double weight;

    @ColumnInfo(name = "height")
    public double height;

    @ColumnInfo(name = "gender")
    public String gender;

    @ColumnInfo(name = "created_at")
    public long createdAt;

    public User() {
        this.createdAt = System.currentTimeMillis();
    }

    public User(String name, int age, double weight, double height, String gender) {
        this();
        this.name = name;
        this.age = age;
        this.weight = weight;
        this.height = height;
        this.gender = gender;
    }
}