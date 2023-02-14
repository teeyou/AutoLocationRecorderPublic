package teeu.android.autolocationrecorderpublic.view.home

import android.Manifest
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import teeu.android.autolocationrecorderpublic.databinding.FragmentHomeBinding
import teeu.android.autolocationrecorderpublic.model.Record
import teeu.android.autolocationrecorderpublic.util.CurrentDate
import teeu.android.autolocationrecorderpublic.R
import teeu.android.autolocationrecorderpublic.Repository
import teeu.android.autolocationrecorderpublic.databinding.ListItemBinding
import teeu.android.autolocationrecorderpublic.firebase.Firestore
import teeu.android.autolocationrecorderpublic.g_signin.Gsignin
import teeu.android.autolocationrecorderpublic.util.DeviceLocale

//동국대학교 서울캠퍼스 위도 경도
private const val DEFAULT_LAT = 37.5582876
private const val DEFAULT_LNG = 127.0001671
private const val TAG = "MYTAG"

class HomeFragment : Fragment() {
    private lateinit var permissionRequest: ActivityResultLauncher<Array<String>>
    private lateinit var binding: FragmentHomeBinding
    private val myAdapter by lazy { MyAdapter() }
    private val viewModel: HomeViewModel by viewModels()
    val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    fun loadLocation() {
        if (checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PermissionChecker.PERMISSION_GRANTED ||
            checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PermissionChecker.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.apply {
                addOnFailureListener { exception ->
                    Toast.makeText(context, exception.toString(), Toast.LENGTH_SHORT).show()
                    throw exception
                }
                addOnSuccessListener { location: Location? ->
                    val geocoder =
                        Geocoder(requireContext(), DeviceLocale.getLocale(requireContext()))
                    val lat = location?.latitude ?: DEFAULT_LAT
                    val lng = location?.longitude ?: DEFAULT_LNG

                    //Worker에서도 똑같이 추가
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        geocoder.getFromLocation(
                            location?.latitude ?: lat,
                            location?.longitude ?: lng,
                            4
                        ) {

                            it.forEach {
                                Log.d(TAG, "TIRAMISU 이상 addressLine - ${it.getAddressLine(0)}")
                            }

                            val record = Record(
                                date = CurrentDate.getCurrentDate(),
                                lat = lat,
                                lng = lng,
                                addressLine = it.first().getAddressLine(0)
                            )
                            viewModel.insertRecord(record)
                            Repository.recentRecord = record
                        }
                    } else {
                        val addressList = geocoder.getFromLocation(
                            location?.latitude ?: lat,
                            location?.longitude ?: lng,
                            4
                        )

                        val record = Record(
                            date = CurrentDate.getCurrentDate(),
                            lat = lat,
                            lng = lng,
                            addressLine = addressList?.first()!!.getAddressLine(0)
                        )

                        addressList.forEach {
                            Log.d(TAG, "addressLine - ${it.getAddressLine(0)}")
                        }

                        viewModel.insertRecord(record)
                        Repository.recentRecord = record
                    }
                }
            }

        }
    }

    fun requestPermissionAndResult() {
        //권한 요청에 대한 결과 세팅
        permissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {

                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    Log.d(TAG, "ACCESS_FINE_LOCATION 권한 승인")
                    loadLocation()
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    Log.d(TAG, "ACCESS_COARSE_LOCATION 권한 승인")
                }

                else -> {
                    Log.d(TAG, "위치 권한 거부")
                    Toast.makeText(
                        context,
                        getString(R.string.permissions_required),
                        Toast.LENGTH_SHORT
                    ).show()

                    requireActivity().finish()

                    val intent = Intent("android.settings.APPLICATION_DETAILS_SETTINGS",
                        Uri.parse("package:${requireContext().packageName}"))
                    startActivity(intent)
                }
            }
        }

        //여기서 권한 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionRequest.launch(
                arrayOf(
                    Manifest.permission.POST_NOTIFICATIONS,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            permissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PermissionChecker.PERMISSION_GRANTED &&
            checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PermissionChecker.PERMISSION_GRANTED
        ) {
            Log.d(TAG, "권한 요청함")
            requestPermissionAndResult()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = myAdapter
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        if(viewModel.recordListLiveData.value?.size != 0)
//            viewModel.deleteAllRecord()

        Log.d(TAG, "id - ${Repository.unregisteredUserId}")
        Gsignin.getInstance()?.checkSignedIn(requireContext()) //로그인 되어있는 상태인지 확인

        binding.editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                Log.d(TAG, "currentList - ${myAdapter.currentList}")

                if (Repository.account == null) {
                    val list = viewModel.recordListLiveData.value?.filter {
                        it.toString().contains(p0 ?: "")
                    }

                    myAdapter.submitList(list?.sortedByDescending { it.date })
                } else {
                    val list = viewModel.recordListFromFirestoreLiveData.value?.filter {
                        it.toString().contains(p0 ?: "")
                    }
                    myAdapter.submitList(list?.sortedByDescending { it.date })
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        binding.imageViewDelete.setOnClickListener {
            binding.editText.setText("")
            if (Repository.account == null)
                myAdapter.submitList(viewModel.recordListLiveData.value?.sortedByDescending { it.date })
            else
                myAdapter.submitList(viewModel.recordListFromFirestoreLiveData.value?.sortedByDescending { it.date })
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "HomeFragment onStart")

        if (Repository.account != null) {
            Firestore.getInstance()
                ?.getSignInUserDocument(Repository.account!!.email!!.substringBefore('@')) { flag, data ->
                    viewModel.recordListFromFirestoreLiveData.value = data.recordList
                    Repository.coin = data.coin
                }
        }

        viewModel.recordListLiveData.observe(viewLifecycleOwner) { list ->
            if (Repository.account == null) {
                if (list != null && list.size > 0) {
                    val reverseList = list.sortedByDescending { it.date }
                    myAdapter.submitList(reverseList)
                    Repository.recentRecord = reverseList.first()
                    Log.d(TAG, "local recentRecord : ${Repository.recentRecord}")
                } else {
                    Log.d(TAG, "recordListLiveData null or size 0")
                    myAdapter.submitList(emptyList())
                    Repository.recentRecord = null
                }
            }
        }

        viewModel.recordListFromFirestoreLiveData.observe(viewLifecycleOwner) { list ->
            if (Repository.account != null) {
                if (list != null && list.size > 0) {
                    val reverseList = list.sortedByDescending { it.date }
                    myAdapter.submitList(reverseList)
                    Repository.recentRecord = reverseList.first()
                    Log.d(TAG, "recordListFromFirestore recentRecord : ${Repository.recentRecord}")
                } else {
                    Log.d(TAG, "recordListFromFirestore null or size 0")
                    myAdapter.submitList(emptyList())
                    Repository.recentRecord = null
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        //홈버튼, Task강제종료, 메뉴 이동시 호출, 백스택에 존재함
        Log.d(TAG, "HomeFragment onStop")

        //비로그인 기록과 로그인 기록을 별개로 처리
        //최근 레코드 1개만 저장

    }

    override fun onDestroy() {
        super.onDestroy()
        //Home버튼, Task목록 눌러도 호출되지 않음
        //Task목록에서 강제종료해도 호출되지 않음
        //정상적으로 종료할 때만 호출
        Log.d(TAG, "HomeFragment onDestroy")
    }

    inner class MyAdapter : ListAdapter<Record, RecyclerView.ViewHolder>(MyDiffCallback()) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return MyHolder(ListItemBinding.inflate(layoutInflater, parent, false))
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder is MyHolder) {
                val record = getItem(position)
                holder.bind(record)
            }
        }
    }

    inner class MyHolder(val binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(record: Record) {
            binding.record = record
            binding.listLayout.setOnClickListener {
//                viewModel.deleteRecord(record)
                val dialogFragment = RecorderDialogFragment.newInstance(record)
                dialogFragment.show(childFragmentManager, "dialog")
            }
        }
    }

    inner class MyDiffCallback : DiffUtil.ItemCallback<Record>() {
        override fun areItemsTheSame(oldItem: Record, newItem: Record): Boolean =
            oldItem === newItem

        override fun areContentsTheSame(oldItem: Record, newItem: Record): Boolean =
            oldItem == newItem
    }
}
