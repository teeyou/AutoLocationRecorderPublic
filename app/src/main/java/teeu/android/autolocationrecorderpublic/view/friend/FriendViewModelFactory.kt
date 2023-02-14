package teeu.android.autolocationrecorderpublic.view.friend

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class FriendViewModelFactory(private val context : Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(FriendViewModel::class.java)) {
            FriendViewModel(context) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}