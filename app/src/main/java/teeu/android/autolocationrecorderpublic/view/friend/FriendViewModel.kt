package teeu.android.autolocationrecorderpublic.view.friend

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.snackbar.Snackbar
import teeu.android.autolocationrecorderpublic.R
import teeu.android.autolocationrecorderpublic.Repository
import teeu.android.autolocationrecorderpublic.firebase.Firestore
import teeu.android.autolocationrecorderpublic.model.FirestoreData
import teeu.android.autolocationrecorderpublic.model.Friend

private const val TAG = "MYTAG"
const val NO_DATA = "No Data"
class FriendViewModel(val context : Context) : ViewModel() {
    interface SeeFriendListener {
        fun onTouch(data : FirestoreData, yourEmail: String)
    }
    interface BackButtonListener {
        fun onBack()
    }

    val friendEmail = MutableLiveData<String>("")
    val result = MutableLiveData<String>("")
    val visible = MutableLiveData(LinearLayout.GONE)

    val friendListLiveData = MutableLiveData<MutableList<Friend>>(mutableListOf())
    val friendRequestListLiveData = MutableLiveData<MutableList<String>>(mutableListOf())
    val listener = context as SeeFriendListener

    fun searchFriend(view: View) {
        if(friendEmail.value!!.length >= 6) {   //구글아이디 최소길이 6
            Firestore.getInstance()?.getSignInUserDocument("") { flag,data ->
                visible.value = LinearLayout.VISIBLE
                when(flag) {
                    true -> {
                        result.value = friendEmail.value!!
                        Log.d(TAG,"true ${result.value.toString()}")
                        Log.d(TAG,"data ${data}")

                    }
                    false -> {
                        result.value = NO_DATA
                        Log.d(TAG,"false ${result.value.toString()}")
                    }
                }
            }
        } else {
            Snackbar.make(view, context.getString(R.string.msg_invalid),Snackbar.LENGTH_SHORT).show()
        }
    }

    fun addFriend(view : View) {
        val myEmail = Repository.account!!.email!!.substringBefore('@')
        val yourEmail = friendEmail.value!!
        val friend = Friend(yourEmail,false)
        var already = false
        for(item in friendListLiveData.value!!) {
            if(item.id == yourEmail)
                already = true
        }

        for(item in friendRequestListLiveData.value!!) {
            if(item == yourEmail)
                already = true
        }

        if(!already &&
            result.value != NO_DATA &&
            yourEmail != myEmail) { //중복요청이 아니고, 로그인 한 이력이 있고, 검색한 아이디가 자기자신이 아닌 경우

            //내 리스트 업데이트
         

            //친구신청 받은 유저 리스트 업데이트
           

        } else {
            Snackbar.make(view, context.getString(R.string.msg_fail), Snackbar.LENGTH_SHORT).show()
        }
    }

    fun acceptFriend(view: View, yourEmail : String) {
        val myEmail = Repository.account!!.email!!.substringBefore('@')

        //수락한 유저의 리스트
        
        //요청했던 유저의 리스트
     
        }
    }

    fun seeFriend(view: View, yourEmail: String) {

    }
}
