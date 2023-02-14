package teeu.android.autolocationrecorderpublic.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import teeu.android.autolocationrecorderpublic.model.Record

@Dao
interface RecordDao {
    
    @Query("select * from record")
    fun getRecordList() : LiveData<List<Record>>

    @Query("select * from record where id =(:id)")
    fun getRecord(id : Int) : LiveData<Record?>

    @Insert
    fun insertRecord(record: Record)

    @Update
    fun updateRecord(record: Record)

    @Delete
    fun deleteRecord(record: Record)

    @Query("delete from record")
    fun deleteAllRecord()
}
