package teeu.android.autolocationrecorderpublic.view.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import teeu.android.autolocationrecorderpublic.Repository
import teeu.android.autolocationrecorderpublic.model.Record

private const val TAG = "MYTAG"

class HomeViewModel : ViewModel() {
    var isSaved = false
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "HomeViewModel cleared")
    }

    private val repository = Repository.getInstance()
    val recordListLiveData by lazy {
        Log.d(TAG, "recordListLiveData lazy")
        repository.getRecordList()
    }

    var recordListFromFirestoreLiveData = Repository.recordListFromFirestore

//    fun getRecordList() = repository.getRecordList()

    fun insertRecord(record: Record) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertRecord(record)
        }
    }

    fun deleteRecord(record: Record) {
        Log.d(TAG, "deleteRecord")
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteRecord(record)
        }
    }

    fun deleteAllRecord() {
        Log.d(TAG, "deleteAllRecord")
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllRecord()
        }
    }
}