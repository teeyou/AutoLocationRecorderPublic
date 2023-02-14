package teeu.android.autolocationrecorderpublic.worker

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

private const val WORK_NAME = "YOUR_WORK_NAME"
class RecorderWorkerRequest {
    companion object {
        fun startPeriodicWork(context: Context, repeatInterval : Long) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED).build() //네트워크 연결되어 있어야함

            val periodicRequest = PeriodicWorkRequest
                .Builder(RecorderWorker::class.java, repeatInterval, TimeUnit.MINUTES) //최소 15분 간격부터 가능
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP, //동일한 이름의 새로운 요청이 들어와도 무시하고 하던거 진행
                periodicRequest
            )
        }

        fun stopPeriodicWork(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
}
