package whz.de.sportapp.db.repository.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import whz.de.sportapp.db.model.WeightEntry;


@Dao
public interface WeightEntryDao {


    @Query("SELECT * FROM weight_history ORDER BY date DESC LIMIT 10")
    LiveData<List<WeightEntry>> getRecentWeightEntries();



    @Insert
    void insertWeightEntry(WeightEntry weightEntry);


}