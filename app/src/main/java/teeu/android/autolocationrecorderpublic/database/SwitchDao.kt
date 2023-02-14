package teeu.android.autolocationrecorderpublic.database

import androidx.lifecycle.LiveData
import androidx.room.*
import teeu.android.autolocationrecorderpublic.model.Switch
import java.util.UUID

@Dao
interface SwitchDao {
    @Query("select * from switch")
    fun getSwitch() : LiveData<Switch>

    @Insert(onConflict = OnConflictStrategy.REPLACE) //PrimaryKey가 고유하지 않으면 소용없음
    fun insertSwitch(switch : Switch)

    @Update
    fun updateSwitch(switch: Switch)

    @Query("delete from switch where id =(:id)")
    fun deleteSwitch(id : UUID)
}