package teeu.android.autolocationrecorderpublic.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Entity
@Parcelize
data class Record(@PrimaryKey val id : UUID = UUID.randomUUID(), val date : String,
                  val lat : Double, val lng : Double,
                  val addressLine : String) : java.io.Serializable , Parcelable