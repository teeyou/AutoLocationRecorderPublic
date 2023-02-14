package teeu.android.autolocationrecorderpublic

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import teeu.android.autolocationrecorderpublic.database.RecorderDatabase
import teeu.android.autolocationrecorderpublic.model.Record
import teeu.android.autolocationrecorderpublic.model.Switch

private const val RECORD_DATABASE_NAME = "YOUR_DATABASE_NAME"
class Repository private constructor(context : Context) {
    private val database : RecorderDatabase = Room.databaseBuilder(
        context.applicationContext,
        RecorderDatabase::class.java,
        RECORD_DATABASE_NAME
    ).build()

    private val recordDao = database.recordDao()
    private val switchDao = database.switchDao()

    fun getRecordList() : LiveData<List<Record>> = recordDao.getRecordList()
    fun getRecord(id : Int) : LiveData<Record?> = recordDao.getRecord(id)

    fun insertRecord(record: Record) {
        recentRecord = record
        recordDao.insertRecord(record)
    }

    fun deleteRecord(record: Record) {
        recordDao.deleteRecord(record)
    }

    fun deleteAllRecord() {
        recordDao.deleteAllRecord()
    }

    fun deleteSwitch(switch: Switch) {
        switchDao.deleteSwitch(switch.id)
    }

    fun getSwitch() : LiveData<Switch> = switchDao.getSwitch()
    fun insertSwitch(switch: Switch) {
        switchDao.insertSwitch(switch)
    }

    fun updateSwitch(switch: Switch) {
        switchDao.updateSwitch(switch)
    }

    companion object {
        var recentRecord : Record? = null
        var switch : Boolean = false
        var recordListFromFirestore = MutableLiveData<MutableList<Record>>()
        var coin = 0L
        var account: GoogleSignInAccount? = null
        var interstitialAd: MutableLiveData<InterstitialAd?> = MutableLiveData<InterstitialAd?>()

        private var instance : Repository? = null

        fun init(context : Context) {
            if(instance == null)
                instance = Repository(context)

        }

        fun getInstance() : Repository = instance ?: throw IllegalStateException("Repository must be initialized")
    }
}
