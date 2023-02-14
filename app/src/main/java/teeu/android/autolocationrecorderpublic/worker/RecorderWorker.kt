package teeu.android.autolocationrecorderpublic.worker

import android.Manifest
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.core.content.PermissionChecker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.*
import teeu.android.autolocationrecorderpublic.Repository
import teeu.android.autolocationrecorderpublic.model.Record
import teeu.android.autolocationrecorderpublic.util.CurrentDate
import teeu.android.autolocationrecorderpublic.util.DeviceLocale
import teeu.android.autolocationrecorderpublic.util.DistanceCalculator

private const val MINIMUM_DISTANCE = 100    //단위 meter
private const val TAG = "MYTAG"

class RecorderWorker(val context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    override suspend fun doWork(): Result = coroutineScope {
        withContext(Dispatchers.IO) {
            if (PermissionChecker.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PermissionChecker.PERMISSION_GRANTED
            ) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    when {
                        location == null -> {
                            Log.d(TAG, "location null")
                        }
                        else -> {
                            Log.d(TAG, "lat : ${location.latitude} lng : ${location.longitude}")

                            val geocoder = Geocoder(context, DeviceLocale.getLocale(context))
//                            val geocoder = Geocoder(context, Locale.KOREA)
                            val lat = location.latitude
                            val lng = location.longitude

                            var record: Record?

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                geocoder.getFromLocation(
                                    location.latitude ?: lat,
                                    location.longitude ?: lng,
                                    4
                                ) {

                                    it.forEach {
                                        Log.d(TAG, it.getAddressLine(0))
                                    }

                                    record = Record(
                                        date = CurrentDate.getCurrentDate(),
                                        lat = lat,
                                        lng = lng,
                                        addressLine = it.first().getAddressLine(0)
                                    )

                                    saveRecord(record!!,location)

                                }
                            } else {
                                val addressList = geocoder.getFromLocation(
                                    location.latitude,
                                    location.longitude,
                                    4
                                )

                                addressList?.forEach {
                                    Log.d(TAG, it.getAddressLine(0))
                                }

                                record = Record(
                                    date = CurrentDate.getCurrentDate(),
                                    lat = lat,
                                    lng = lng,
                                    addressLine = addressList?.first()!!.getAddressLine(0)
                                )

                                saveRecord(record!!,location)
                            }
                        }
                    }
                }
            } else {
                Log.d(TAG,"GPS 권한 없음")
            }
            Result.success()
        }
    }

    fun saveRecord(record: Record, location: Location) {
        //현재 위치와 거리를 계산할 최근 레코드가 없을 때
        if (Repository.recentRecord == null) {
            Repository.recentRecord = record

            //room에 쓰기
            if(Repository.account == null) {
                CoroutineScope(Dispatchers.IO).launch {
                    Repository.getInstance().insertRecord(record)
                }
            }

            //firestore에 쓰기
            if(Repository.account != null)
               

            Log.d(TAG, "첫번째 기록 추가")

        } else {
            //현재 위치와 거리를 계산해서 최소거리차이 보다 클 경우에 저장
            val meter: Int = DistanceCalculator.getDistance(
                Repository.recentRecord!!.lat,
                Repository.recentRecord!!.lng,
                location.latitude,
                location.longitude
            )

            Log.d(TAG, "거리 차이 meter : ${meter}")
            if (meter >= MINIMUM_DISTANCE) {
                //room에 쓰기
                if(Repository.account == null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        Repository.getInstance().insertRecord(record)
                    }
                }
                //firestore에 쓰기
                if(Repository.account != null)
                 

                Repository.recentRecord = record
                Log.d(TAG, "이동한 거리 100m 이상. 기록 추가")
            } else {
                Log.d(TAG, "이동한 거리 100m 이내. 기록 추가 안함")
            }
        }
    }
}
