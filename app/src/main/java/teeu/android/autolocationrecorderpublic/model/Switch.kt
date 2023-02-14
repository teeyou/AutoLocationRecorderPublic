package teeu.android.autolocationrecorderpublic.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Switch(@PrimaryKey val id : UUID = UUID.randomUUID(), var checked : Boolean) {
}