package whz.de.sportapp.db.repository.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Update;

import whz.de.sportapp.db.model.UserPreferences;

@Dao
public interface UserPreferencesDao {
    @Query("SELECT * FROM user_preferences WHERE prefId = 1")
    LiveData<UserPreferences> getPreferences();


    @Update
    void updatePreferences(UserPreferences preferences);


}