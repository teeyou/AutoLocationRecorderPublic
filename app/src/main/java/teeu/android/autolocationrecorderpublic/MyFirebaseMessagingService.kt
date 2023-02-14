package teeu.android.autolocationrecorderpublic

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import teeu.android.autolocationrecorderpublic.firebase.Firestore

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Firestore.getInstance()?.addFCMToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val id = message.messageId
        val from = message.from
        val to = message.to
        val type = message.messageType
        val data = message.data
        val title = message.notification?.title ?: "title"
        val text = message.notification?.body ?: "text"
        val sender_id = message.senderId
        val sent_time = message.sentTime
        val priority = message.priority

        makeNotification(title, text)
    }

    private fun makeNotification(title : String, text : String) {
        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent,
                    PendingIntent.FLAG_IMMUTABLE)
            }

        val notification = NotificationCompat
            .Builder(this, FCM_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.icon_gps)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setContentTitle(title)
            .setContentText(text)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .build()

        NotificationManagerCompat.from(this).notify(0,notification)
    }
}