package teeu.android.autolocationrecorderpublic

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import teeu.android.autolocationrecorderpublic.firebase.Auth
import teeu.android.autolocationrecorderpublic.firebase.Firestore
import teeu.android.autolocationrecorderpublic.g_signin.Gsignin

private const val TAG = "MYTAG"
private const val AD_ID = "YOUR_AD_ID";

const val NOTIFICATION_CHANNEL_ID = "YOUR_CHANNEL_ID"
const val NOTIFICATION_CHANNEL_NAME = "YOUR_CHANNEL_NAME"

const val FCM_NOTIFICATION_CHANNEL_ID = "YOUR_FCM_CHANNEL_ID"
const val FCM_NOTIFICATION_CHANNEL_NAME = "YOUR_FCM_CHANNEL_NAME"

class RecorderApplication : Application() {
    fun createNotiChannel() {
        val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID,NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH)
        channel.description = "Foreground Service Channel"
        
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun createFCMNotiChannel() {
        val channel = NotificationChannel(FCM_NOTIFICATION_CHANNEL_ID,FCM_NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH)
        channel.description = "FCM Channel"

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    override fun onCreate() {
        super.onCreate()
        Repository.init(this)
        createNotiChannel()
        createFCMNotiChannel()

        Firestore.init(this)
        Auth.init(this)
        Gsignin.init(this)

        MobileAds.initialize(this) {
            Log.d(TAG,"MobileAds initialize")
        }
        loadAds()
    }

    private fun loadAds() {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(applicationContext, AD_ID, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(TAG, adError.message)
                Repository.interstitialAd.value = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d(TAG, "Ad was loaded.")
                Repository.interstitialAd.value = interstitialAd

                Repository.interstitialAd.value?.fullScreenContentCallback = object: FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        Log.d(TAG, "Ad was dismissed.")
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        Log.d(TAG, "Ad failed to show.")
                    }
                    override fun onAdShowedFullScreenContent() {
                        Log.d(TAG, "Ad showed fullscreen content.")
                        Repository.interstitialAd.value = null;
                        Repository.coin += 1
                        if(Repository.account != null)
                            Firestore.getInstance()?.updateField("")
                    }
                }
            }
        })
    }
}
