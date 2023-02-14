package teeu.android.autolocationrecorderpublic.firebase

import android.content.Context
import android.util.Log
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth

private const val TAG = "MYTAG"

// Firebase를 통한 구글 로그인, provider, email, uid, token 정도만 받아올 수 있음
// 기본적으로 로그인 상태가 유지되지 않음
class Auth private constructor(context: Context) {
    private val auth = FirebaseAuth.getInstance()

    companion object {
        private var instance : Auth? = null
        fun init(context: Context) {
            if(instance == null)
                instance = Auth(context)
        }

        fun getInstance() = instance
    }

    fun logout(context: Context) {
//        auth.signOut() // return Unit

        AuthUI.getInstance()
            .signOut(context)
            .addOnCompleteListener {
                Log.d(TAG,"logout completed")
            }
    }

    fun getCurrentUser() = auth.currentUser

    fun getUserInfo() {
        val user = auth.currentUser
        user?.let {
            // Name, email address, and profile photo Url
            val name = user.displayName
            val email = user.email
            val photoUrl = user.photoUrl

            // Check if user's email is verified
            val emailVerified = user.isEmailVerified
            val phone = user.phoneNumber
            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            val uid = user.uid
        }
    }

    fun deleteUser(context: Context) {
        AuthUI.getInstance()
            .delete(context)
            .addOnCompleteListener {
                Log.d(TAG,"delete user completed")
            }
    }


//    fun createAccount(email : String, password : String) {
//        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
//            .addOnCompleteListener(requireActivity()) { task ->
//                if (task.isSuccessful) {
//                    // Sign in success, update UI with the signed-in user's information
//                    Log.d(TAG, "createUserWithEmail:success")
//                    val user = auth.currentUser
//                    updateUI(user)
//                } else {
//                    // If sign in fails, display a message to the user.
//                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
//                    Toast.makeText(baseContext, "Authentication failed.",
//                        Toast.LENGTH_SHORT).show()
//                    updateUI(null)
//                }
//            }
//    }

//    fun signinWithEmailAndPassword(email : String, password : String) {
//        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
//            .addOnCompleteListener(requireActivity()) { task ->
//                if (task.isSuccessful) {
//                    // Sign in success, update UI with the signed-in user's information
//                    Log.d(TAG, "signInWithEmail:success")
//                    val user = auth.currentUser
//                    updateUI(user)
//                } else {
//                    // If sign in fails, display a message to the user.
//                    Log.w(TAG, "signInWithEmail:failure", task.exception)
//                    Toast.makeText(baseContext, "Authentication failed.",
//                        Toast.LENGTH_SHORT).show()
//                    updateUI(null)
//                }
//            }
//    }

}