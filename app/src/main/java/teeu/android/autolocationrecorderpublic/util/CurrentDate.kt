package teeu.android.autolocationrecorderpublic.util

import java.text.SimpleDateFormat
import java.util.*

class CurrentDate {
    companion object {
        fun getCurrentDate() : String {
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val now = System.currentTimeMillis();
            val date = Date(now);
            return format.format(date)
        }
    }
}