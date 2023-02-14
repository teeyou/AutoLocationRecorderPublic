package teeu.android.autolocationrecorderpublic.util

import android.content.Context
import android.util.Log
import java.util.Locale

private const val TAG = "MYTAG"
class DeviceLocale {
    companion object {
        fun getLocale(context: Context) : Locale {
            val locale = context.resources.configuration.locales.get(0)
            Log.d(TAG,"getLocale - ${locale}")
            return locale
        }
    }
}