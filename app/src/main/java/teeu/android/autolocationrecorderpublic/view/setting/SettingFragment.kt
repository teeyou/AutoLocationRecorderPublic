package teeu.android.autolocationrecorderpublic.view.setting

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.PermissionChecker

import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import teeu.android.autolocationrecorderpublic.R
import teeu.android.autolocationrecorderpublic.RecorderService
import teeu.android.autolocationrecorderpublic.Repository
import teeu.android.autolocationrecorderpublic.databinding.FragmentSettingBinding
import teeu.android.autolocationrecorderpublic.firebase.Firestore
import teeu.android.autolocationrecorderpublic.g_signin.Gsignin
import teeu.android.autolocationrecorderpublic.model.Switch
import teeu.android.autolocationrecorderpublic.worker.RecorderWorkerRequest

private const val REPEAT_TIME_MINUTES: Long = 15
private const val TAG = "MYTAG"

class SettingFragment : Fragment() {
//    lateinit var signInLauncher : ActivityResultLauncher<Intent> //FirebaseAuth로 SignIn 할 때 쓰는 코드
    private lateinit var permissionRequest: ActivityResultLauncher<Array<String>>

    lateinit var launcher: ActivityResultLauncher<Intent>

    private val viewModel: SettingViewModel by viewModels()
    private lateinit var binding: FragmentSettingBinding


    //데이터 가져옴
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionRequest = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                val flag = permissions.getOrDefault(Manifest.permission.POST_NOTIFICATIONS,false)
                if(!flag) {
                    Toast.makeText(requireContext(),getString(R.string.permissions_required),Toast.LENGTH_SHORT).show()
                    val intent = Intent("android.settings.APPLICATION_DETAILS_SETTINGS",
                        Uri.parse("package:${requireContext().packageName}"))
                    startActivity(intent)
                }
            }
        }

        viewModel.switch = viewModel.getSwitchFromDatabase()

        if (Repository.account == null) {
            viewModel.signInOut.value = getString(R.string.sign_in)
            viewModel.state.value = "Guest"
        } else {
            viewModel.signInOut.value = getString(R.string.sign_out)
            viewModel.state.value = ""
        }
        viewModel.coin.value = Repository.coin

        //FirebaseAuth로 SignIn 할 때 쓰는 코드
//        signInLauncher = registerForActivityResult(
//            FirebaseAuthUIActivityResultContract()
//        ) { res ->
//            Log.d(TAG, "res - ${res}")
//            onSignInResult(res)
//        }

        launcher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult() as GoogleSignInAccount
                    Repository.account = account
                    viewModel.signInOut.value = getString(R.string.sign_out)
                    viewModel.state.value = ""

                    Firestore.getInstance()?.getSignInUserDocument("") { flag,data ->
                        when(flag) {
                            true -> {
                                if(data.recordList.size > 0 )
                                    Repository.recentRecord = data.recordList.sortedByDescending { it.date }.first()

                                Repository.coin = data.coin
                                viewModel.coin.value = data.coin
                                binding.switchOnoff.isChecked = data.switch
                            }
                            false -> {
                                Firestore.getInstance()?.addSignInUserData("") {
                                    Log.d(TAG, "addSignInUserData flag : ${it}")
                                } //최초 로그인일 때만 호출
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.d(TAG, e.toString())
                }
            }
    }

    //뷰 세팅
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false)
        binding.viewModel = viewModel
        return binding.root
    }

    //뷰 로직 처리
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonLoginout.setOnClickListener {
            when (viewModel.signInOut.value) {
                getString(R.string.sign_in) -> {
                    Log.d(TAG, "sign_in")
                    val intent = Gsignin.getInstance()?.getSignInIntent(requireContext())
                    launcher.launch(intent)
                }
                getString(R.string.sign_out) -> {
                    //유저데이터 저장 후 로그아웃, 게스트모드로 초기화
                    Log.d(TAG, "sign_out")
                    Firestore.getInstance()?.updateField("")
                    Gsignin.getInstance()?.signOut(requireContext())
                    viewModel.signInOut.value = getString(R.string.sign_in)
                    viewModel.state.value = "Guest"
                    viewModel.coin.value = 0L
                    Repository.coin = 0L
                    Repository.account = null
//                    Repository.recentRecord = null
                    binding.switchOnoff.isChecked = false
                }
            }
        }

        viewModel.coin.observe(viewLifecycleOwner) {
            binding.textViewDeleteCoinCount.text = it.toString()
        }

        viewModel.state.observe(viewLifecycleOwner) {
            binding.textViewLoginState.text = it
        }

        viewModel.signInOut.observe(viewLifecycleOwner) {
            binding.buttonLoginout.text = it
        }

        viewModel.switch.observe(viewLifecycleOwner) { switch: Switch? ->
            Log.d(TAG, "viewModel.switch observe - ${switch?.checked}")
            binding.switchOnoff.isChecked = switch?.checked ?: false
            Repository.switch = switch?.checked ?: false
        }

        binding.switchOnoff.setOnCheckedChangeListener { button, checked ->
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                PermissionChecker.checkSelfPermission(requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS) != PermissionChecker.PERMISSION_GRANTED && checked ) {

                binding.switchOnoff.isChecked = false
                permissionRequest.launch(arrayOf(
                    Manifest.permission.POST_NOTIFICATIONS
                ))
            }
            else
                startForeground(checked)
        }
    }

    override fun onStop() {
        super.onStop()
        if(Repository.account != null)
            Firestore.getInstance()?.updateField("")

        when (viewModel.switch.value) {
            null -> {
                Log.d(TAG, "onStop - insert switch")
                viewModel.insertSwitch(Switch(checked = binding.switchOnoff.isChecked))
            }
            else -> {
                Log.d(TAG, "onStop - update switch")
                viewModel.switch.value!!.checked = binding.switchOnoff.isChecked
                viewModel.updateSwitch(viewModel.switch.value!!)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }

    fun startForeground(checked: Boolean) {
        when (checked) {
            true -> {
                if (Repository.switch) {
                    //이미 포그라운드 실행중인 상태
                    Log.d(TAG, "포그라운드 실행중인 상태에서 또 실행")
                } else {
                    Repository.switch = true
                    Log.d(TAG, "startWork")
                    RecorderWorkerRequest.startPeriodicWork(requireContext(), REPEAT_TIME_MINUTES)
                    val intent = Intent(context, RecorderService::class.java)
                    intent.action = "start"
                    requireActivity().startForegroundService(intent)
                }
            }
            false -> {
                if(!Repository.switch) {
                    //이미 포그라운드 꺼져있는 상태
                    Log.d(TAG, "포그라운드 꺼져있는 상태에서 또 끄기")
                } else {
                    Repository.switch = false
                    Log.d(TAG, "stopWork")
                    RecorderWorkerRequest.stopPeriodicWork(requireContext())
                    val intent = Intent(context, RecorderService::class.java)
                    intent.action = "stop"
                    requireActivity().startForegroundService(intent)
                }
            }
        }
    }
}
