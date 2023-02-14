package teeu.android.autolocationrecorderpublic

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import teeu.android.autolocationrecorderpublic.databinding.ActivityMainBinding
import teeu.android.autolocationrecorderpublic.model.FirestoreData
import teeu.android.autolocationrecorderpublic.view.friend.FriendFragment
import teeu.android.autolocationrecorderpublic.view.friend.FriendViewModel


private const val TAG = "MYTAG"
class MainActivity : AppCompatActivity(), FriendFragment.ButtonTouchedListener, FriendViewModel.SeeFriendListener, FriendViewModel.BackButtonListener {
    lateinit var binding : ActivityMainBinding
    lateinit var navController : NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)

    }

    override fun changeFragment() {
        binding.bottomNavigation.selectedItemId = R.id.settingFragment
    }

    fun hideBottomNavigation(flag : Boolean) {
        if(flag)
            binding.bottomNavigation.visibility = BottomNavigationView.GONE
        else
            binding.bottomNavigation.visibility = BottomNavigationView.VISIBLE
    }

    override fun onTouch(data: FirestoreData, yourEmail : String) {
        hideBottomNavigation(true)
        navController.navigate(R.id.action_friendFragment_to_friendHistoryFragment, bundleOf("data" to data, "email" to yourEmail))
    }

    override fun onBack() {
        hideBottomNavigation(false)
    }
}
