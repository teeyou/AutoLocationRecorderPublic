package teeu.android.autolocationrecorderpublic

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder

private const val TAG = "MYTAG"
class RecorderService : Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            "stop" -> {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                } else {
                    stopForeground(true)
                    stopSelf()
                }
            }
            "start" -> start()
        }
        return START_REDELIVER_INTENT //프로세스가 죽었을 때 이전에 전달된 intent 그대로 전달
    }

    private fun start() {
        //Android 12 부터 FLAG_IMMUTABLE를 권장함
        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent,
                    PendingIntent.FLAG_IMMUTABLE)
            }

        val notification: Notification = Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(getString(R.string.noti_title))
            .setContentText(getString(R.string.noti_text))
            .setLargeIcon(BitmapFactory.decodeResource(resources,R.drawable.icon_gps))
            .setSmallIcon(R.drawable.icon_gps)
            .setContentIntent(pendingIntent)
            .setTicker("ticker")
            .build()

        // Notification ID cannot be 0.
        startForeground(1,notification)
    }
}