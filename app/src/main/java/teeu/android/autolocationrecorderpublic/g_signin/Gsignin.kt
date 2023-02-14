package teeu.android.autolocationrecorderpublic.g_signin

import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import teeu.android.autolocationrecorderpublic.Repository

private const val TAG = "MYTAG"
// Google 계정으로 로그인, displayName, email, id, photoUrl등 받아올 수 있음
// 한 번 로그인하면 로그인 상태 유지됨
// 디바이스에 연결된 모든 구글계정이 디스플레이 되고 선택해서 로그인 가능

class Gsignin private constructor(context: Context){
    companion object {
        private var instance : Gsignin? = null
        fun init(context: Context) {
            if(instance == null)
                instance = Gsignin(context)
        }

        fun getInstance() = instance
    }

    private val gso : GoogleSignInOptions by lazy {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
    }

    fun getSignInIntent(context: Context) = GoogleSignIn.getClient(context,gso).signInIntent

    fun checkSignedIn(context: Context) : Boolean {
        val account = GoogleSignIn.getLastSignedInAccount(context)
        when(account) {
            null -> {
                Log.d(TAG, "checkSignedIn - 이전에 로그인한 이력 없음")
                return false
            }
            else -> {
                Log.d(TAG, "checkSignedIn - 로그인 되어있음")
                Repository.account = account
                return true
            }
        }
    }

    fun signOut(context: Context) {
        GoogleSignIn.getClient(context,gso).signOut().addOnCompleteListener{  task->
            if(task.isSuccessful) {
                Log.d(TAG, "sign-out success")
            }
            else
                Log.d(TAG,"sign-out failed")
        }
    }
}
