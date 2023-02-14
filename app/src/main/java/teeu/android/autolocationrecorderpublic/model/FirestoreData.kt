package teeu.android.autolocationrecorderpublic.model

data class FirestoreData(
    var coin: Long = 0, var coin_used: Long = 0,
    var friendList: MutableList<Friend> = mutableListOf(), var friendrequestList: MutableList<String> = mutableListOf(),
    var recordList: MutableList<Record> = mutableListOf(), var recordDeleteList: MutableList<String> = mutableListOf(),
    var switch: Boolean = false
) : java.io.Serializable