package teeu.android.autolocationrecorderpublic.database

import androidx.room.Database
import androidx.room.RoomDatabase
import teeu.android.autolocationrecorderpublic.model.Record
import teeu.android.autolocationrecorderpublic.model.Switch

//@Database(entities = [Record::class], version = 1)
//abstract class RecordDatabase : RoomDatabase() {
//    abstract fun recordDao() : RecordDao
//}

@Database(entities = [Record::class, Switch::class], version = 1)
abstract class RecorderDatabase : RoomDatabase() {
    abstract fun recordDao() : RecordDao
    abstract fun switchDao() : SwitchDao
}