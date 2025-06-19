package whz.de.sportapp.db.repository.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import whz.de.sportapp.db.model.User;

@Dao
public interface UserDao {
    @Query("SELECT * FROM user_profile WHERE uid = 1")
    LiveData<User> getUser();

    @Query("SELECT * FROM user_profile WHERE uid = 1")
    User getUserSync();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(User user);

    @Update
    void updateUser(User user);

    @Query("UPDATE user_profile SET weight = :weight WHERE uid = 1")
    void updateWeight(double weight);


}