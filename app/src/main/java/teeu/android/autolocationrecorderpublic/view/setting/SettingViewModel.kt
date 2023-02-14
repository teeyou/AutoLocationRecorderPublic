package teeu.android.autolocationrecorderpublic.view.setting

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import teeu.android.autolocationrecorderpublic.Repository
import teeu.android.autolocationrecorderpublic.model.Switch
class SettingViewModel : ViewModel() {
    private val repository = Repository.getInstance()
    lateinit var switch : LiveData<Switch>
    var coin = MutableLiveData<Long>(0L)
    var signInOut = MutableLiveData<String>("Sign-in")
    var state = MutableLiveData<String>("Guest")

    override fun onCleared() {
        super.onCleared()
        Log.d("MYTAG", "SettingViewModel cleared")
    }

    fun getSwitchFromDatabase() = repository.getSwitch()

    fun insertSwitch(switch: Switch) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertSwitch(switch)
        }
    }

    fun updateSwitch(switch: Switch) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateSwitch(switch)
        }
    }

    fun deleteSwitch(switch: Switch) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteSwitch(switch)
        }
    }
}

