package teeu.android.autolocationrecorderpublic.firebase

import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.json.JSONArray
import org.json.JSONObject
import teeu.android.autolocationrecorderpublic.model.FirestoreData
import teeu.android.autolocationrecorderpublic.model.Friend
import teeu.android.autolocationrecorderpublic.model.Record
import java.util.*

private const val TAG = "MYTAG"

class Firestore private constructor(context: Context) {
    private val db = Firebase.firestore

    companion object {
        private var instance: Firestore? = null

        fun init(context: Context) {
            if (instance == null)
                instance = Firestore(context)
        }

        fun getInstance() = instance
    }

    fun addFCMToken(token : String) {
        db.collection("").document(token)
            .set(mapOf("" to token))
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
    }
    fun getRecordList(doc_id: String, callback: ((Boolean,FirestoreData) -> MutableList<Record>)) {
        db.collection("").document(doc_id)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val map = documentSnapshot.data
                when (map) {
                    null -> {
                        Log.d(TAG, "signInUser get document null")
                        callback.invoke(false, FirestoreData())
                    }
                    else -> {
//
                        val coin = map.get("") as Long
                        val coin_used = map.get("") as Long

                        val friendrequestList = map.get("") as MutableList<String>
                        val recordDeleteList = mutableListOf<String>()
//                        val recordDeleteList = map.get("") as MutableList<String> //굳이 읽어올 필요없음. 사용하게 된다면 Record타입으로 바꿔서 사용
                        val switch = map.get("") as Boolean

                        val friendList = mutableListOf<Friend>()
                        val recordList = mutableListOf<Record>()

                        val list = map.get("") as MutableList<*>
                        val list2 = map.get("") as MutableList<*>

                        //Firestore에서 friendList에 있는 Friend 객체 변환
                        for (item in list) {
                            val json = JSONObject(item.toString())
                            val friend = json.getString("").toBoolean()
                            val id = json.getString("")
                            friendList.add(Friend(id = id, friend = friend))
                        }

                        val jsonArray = JSONArray(list2)

                        //Firestore에서 recordList에 있는 Record 객체 변환
                        for(i in 0 until jsonArray.length()) {
                            val addressLine = jsonArray.getJSONObject(i).getString("")
                            val date = jsonArray.getJSONObject(i).getString("")
                            val leastSignificantBits = jsonArray.getJSONObject(i).getJSONObject("").getLong("")
                            val mostSignificantBits = jsonArray.getJSONObject(i).getJSONObject("").getLong("")
                            val lat = jsonArray.getJSONObject(i).getDouble("")
                            val lng = jsonArray.getJSONObject(i).getDouble("")
                            recordList.add()
                        }

                        val firestoreData = FirestoreData()
                        Log.d(TAG, "signInUser successfully get document")
                        callback.invoke(true,firestoreData)
                    }
                }

            }
            .addOnFailureListener { e ->
                Log.d(TAG, "Error getting documents: ", e)
                callback.invoke(false, FirestoreData())
            }
    }

    fun getSignInUserDocument(doc_id: String, callback: ((Boolean,FirestoreData) -> Unit)) {
        db.collection("").document(doc_id)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val map = documentSnapshot.data
                when (map) {
                    null -> {
                        Log.d(TAG, "signInUser get document null")
                        callback.invoke(false, FirestoreData())
                    }
                    else -> {
                        val coin = map.get("") as Long
                        val coin_used = map.get("") as Long

                        val friendrequestList = map.get("") as MutableList<String>
                        val recordDeleteList = mutableListOf<String>()
//                        val recordDeleteList = map.get("") as MutableList<String> //굳이 읽어올 필요없음. 사용하게 된다면 Record타입으로 바꿔서 사용
                        val switch = map.get("") as Boolean

                        val friendList = mutableListOf<Friend>()
                        val recordList = mutableListOf<Record>()

                        val list = map.get("") as MutableList<*>
                        val list2 = map.get("") as MutableList<*>

                        //Firestore에서 friendList에 있는 Friend 객체 변환
                        for (item in list) {
                            val json = JSONObject(item.toString())
                            val friend = json.getString("").toBoolean()
                            val id = json.getString("")
                            friendList.add(Friend(id = id, friend = friend))
                        }

                        val jsonArray = JSONArray(list2)

                        //Firestore에서 recordList에 있는 Record 객체 변환
                        for(i in 0 until jsonArray.length()) {
                            val addressLine = jsonArray.getJSONObject(i).getString("")
                            val date = jsonArray.getJSONObject(i).getString("")
                            val leastSignificantBits = jsonArray.getJSONObject(i).getJSONObject("id").getLong("")
                            val mostSignificantBits = jsonArray.getJSONObject(i).getJSONObject("id").getLong("")
                            val lat = jsonArray.getJSONObject(i).getDouble("")
                            val lng = jsonArray.getJSONObject(i).getDouble("")
                            recordList.add()
                        }

                        val firestoreData = ""
                        Log.d(TAG, "signInUser successfully get document")
                        callback.invoke(true,firestoreData)
                    }
                }

            }
            .addOnFailureListener { e ->
                Log.d(TAG, "Error getting documents: ", e)
                callback.invoke(false, FirestoreData())
            }
    }

    //List에 Record 객체를 넣기 위해서 생성된 List에 element를 추가하는 방식으로 해결
    fun updateRecordList(collection: String, doc_id: String, record: Record) {
        db.collection(collection).document(doc_id)
            .update("", FieldValue.arrayUnion(record))
            .addOnSuccessListener { Log.d(TAG, "successfully update!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error update document", e) }
    }

    fun updateRecordDeleteList(collection: String, doc_id: String, record: Record) {
        db.collection(collection).document(doc_id)
            .update("", FieldValue.arrayUnion(record))
            .addOnSuccessListener { Log.d(TAG, "successfully update!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error update document", e) }
    }

    fun updateFriendList(collection: String, doc_id: String, friend: Friend, callback: (Boolean) -> Unit) {
        db.collection(collection).document(doc_id)
            .update("", FieldValue.arrayUnion(friend))
            .addOnSuccessListener {
                Log.d(TAG, "successfully update!")
                callback.invoke(true)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error update document", e)
                callback.invoke(false)
            }
    }

    fun updateFriendrequestList(collection: String, doc_id: String, email: String) {
        db.collection(collection).document(doc_id)
            .update("", FieldValue.arrayUnion(email))
            .addOnSuccessListener { Log.d(TAG, "successfully update!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error update document", e) }
    }
    fun deleteStringListField(collection: String, doc_id: String, field: String, element: String) {
        db.collection(collection).document(doc_id)
            .update(field, FieldValue.arrayRemove(element))
            .addOnSuccessListener { Log.d(TAG, "successfully delete!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error delete document", e) }
    }

    fun deleteRecordListField(collection: String, doc_id: String, field: String, element: Record) {
        db.collection(collection).document(doc_id)
            .update(field, FieldValue.arrayRemove(element))
            .addOnSuccessListener { Log.d(TAG, "successfully delete!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error delete document", e) }
    }

    fun deleteFriendListField(collection: String, doc_id: String, field: String, element: Friend) {
        db.collection(collection).document(doc_id)
            .update(field, FieldValue.arrayRemove(element))
    }

    fun updateField(doc_id: String, field: String, value: Any) {
        db.collection("")
            .document(doc_id)
            .update(field, value)
            .addOnSuccessListener { Log.d(TAG, "Field successfully written!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
    }

    fun updateValueDown(collection: String, doc_id: String, field: String, number : Long) {
        db.collection(collection).document(doc_id).update(field, FieldValue.increment(-number))
    }

    fun updateValueUp(collection: String, doc_id: String, field: String, number : Long) {
        db.collection(collection).document(doc_id).update(field, FieldValue.increment(number))
    }

    fun addSignInUserData(account: GoogleSignInAccount, callback: (Boolean) -> Unit) {
        val g_email: String = account.email!!
        val g_photoUrl = account.photoUrl
        val g_id = account.id
        val g_displayName = account.displayName
        val g_idToken = account.idToken

        val data = hashMapOf(
         
        )


        db.collection("")
            .document("")
            .set(data)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully written!")
                callback.invoke(true)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error writing document", e)
                callback.invoke(false)
            }
    }

    fun addUnregisteredUserData(doc_id: String, callback: (Boolean) -> Unit) {
        val data = hashMapOf(
            
        )

        db.collection("")
            .document(doc_id)
            .set(data)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully written!")
                callback.invoke(true)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error writing document", e)
                callback.invoke(false)
            }
    }
}


//    fun 배열요소업데이트() {
//        // Atomically add a new region to the "regions" array field.
//        db.collection("cities").document("DC").update("regions", FieldValue.arrayUnion("greater_virginia"))
//
//        // Atomically remove a region from the "regions" array field.
//        db.collection("cities").document("DC").update("regions", FieldValue.arrayRemove("east_coast"))
//    }
//

//    fun 사용자지정객체가져오기() {
//        val docRef = db.collection("cities").document("BJ")
//        docRef.get().addOnSuccessListener { documentSnapshot ->
//            val city = documentSnapshot.toObject<Record>()
//        }
//    }
//
//    fun 컬렉션에서여러문서가져오기() {
//        db.collection("cities")
//            .whereEqualTo("capital", true)
//            .get()
//            .addOnSuccessListener { documents ->
//                for (document in documents) {
//                    Log.d(TAG, "${document.id} => ${document.data}")
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.w(TAG, "Error getting documents: ", exception)
//            }
//    }
