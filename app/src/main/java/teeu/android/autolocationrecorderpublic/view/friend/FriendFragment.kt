package teeu.android.autolocationrecorderpublic.view.friend

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.*
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import teeu.android.autolocationrecorderpublic.R
import teeu.android.autolocationrecorderpublic.Repository
import teeu.android.autolocationrecorderpublic.databinding.FragmentFriendBinding
import teeu.android.autolocationrecorderpublic.databinding.FragmentFriendGuestBinding
import teeu.android.autolocationrecorderpublic.databinding.ListItemFriendBinding
import teeu.android.autolocationrecorderpublic.databinding.ListItemRequestBinding
import teeu.android.autolocationrecorderpublic.model.Friend

private const val TAG = "MYTAG"
class FriendFragment : Fragment() {
    interface ButtonTouchedListener {
        fun changeFragment()
    }
    private lateinit var binding : FragmentFriendBinding
    private var listener : ButtonTouchedListener? = null //MainActivity에서 BottomNav 변경
    private val viewModel : FriendViewModel by lazy {
        ViewModelProvider(this,FriendViewModelFactory(requireContext())).get(FriendViewModel::class.java)   //viewModel에 context 넣어서 생성
    }

    private val friendAdapter by lazy { FriendAdpater() }
    private val requestAdapter by lazy { RequestAdapter() }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as ButtonTouchedListener?
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        when(Repository.account) {
            null -> {
                val binding_guest : FragmentFriendGuestBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_friend_guest,container,false)
                binding_guest.buttonLogin.setOnClickListener {
                    listener?.changeFragment()
                }
                return binding_guest.root
            }
            else -> {
//                val list = listOf("oreo","pie","quincetart","redvelvetcake","snowcone","tiramisu",
//                    "oreo","pie","quincetart","redvelvetcake","snowcone","tiramisu")
//
//                val list2 = listOf("oreo","pie","quincetart","redvelvetcake","snowcone","tiramisu",
//                    "oreo","pie","quincetart","redvelvetcake","snowcone","tiramisu",
//                    "oreo","pie","quincetart","redvelvetcake","snowcone","tiramisu",
//                    "oreo","pie","quincetart","redvelvetcake","snowcone","tiramisu")


                binding = DataBindingUtil.inflate(inflater, R.layout.fragment_friend,container,false)
                binding.viewModel = viewModel

                binding.recyclerViewRequest.apply {
                    layoutManager = LinearLayoutManager(context,VERTICAL,false)
                    addItemDecoration(DividerItemDecoration(context,VERTICAL))
//                    adapter = RequestAdapter(list)
                    adapter = requestAdapter
                }

                binding.recyclerViewFriend.apply {
                    layoutManager = LinearLayoutManager(context,VERTICAL,false)
                    addItemDecoration(DividerItemDecoration(context,VERTICAL))
//                    adapter = FriendAdpater(list2)
                    adapter = friendAdapter
                }

                return binding.root
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(Repository.account != null) {
          
            }

            binding.editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    viewModel.friendEmail.value = p0.toString()
                }
                override fun afterTextChanged(p0: Editable?) {}
            })

            binding.editText.setOnEditorActionListener(object : OnEditorActionListener{
                override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
                    if(p1 == EditorInfo.IME_ACTION_SEARCH) {
                        viewModel.searchFriend(p0!!)
                        return true
                    }
                    return false
                }

            })
            binding.imageViewDelete.setOnClickListener {
                binding.editText.setText("")
                binding.linearLayoutSecond.visibility = LinearLayout.GONE
                viewModel.friendEmail.value = ""
            }

            viewModel.visible.observe(viewLifecycleOwner) {
                binding.linearLayoutSecond.visibility = it
            }

            viewModel.result.observe(viewLifecycleOwner) {
                binding.textViewSearchResult.text = it
                val alreadyFriend = viewModel.friendListLiveData.value?.toString()?.contains(it) ?: false
                binding.buttonAdd.isEnabled = !(alreadyFriend || it == NO_DATA)
            }

            viewModel.friendRequestListLiveData.observe(viewLifecycleOwner) {
                Log.d(TAG, "friendRequestListLiveData : ${it}")
                val list = mutableListOf<String>()
                list.addAll(it)
                requestAdapter.submitList(list.sorted())
            }

            viewModel.friendListLiveData.observe(viewLifecycleOwner) {
                Log.d(TAG,"friendListLiveData : ${it}")
                val list = mutableListOf<Friend>()
                list.addAll(it)
                friendAdapter.submitList(list.sortedBy { it.id })
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if(Repository.account != null) {
            viewModel.friendEmail.value = ""
            viewModel.visible.value = LinearLayout.GONE
            viewModel.result.value = ""
            binding.editText.setText("")
            viewModel.friendRequestListLiveData.value!!.clear()
            viewModel.friendListLiveData.value!!.clear()
        }
    }

    inner class RequestAdapter : ListAdapter<String, RecyclerView.ViewHolder>(RequestDiffCallback()) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestHolder {
            val binding = DataBindingUtil.inflate<ListItemRequestBinding>(layoutInflater,R.layout.list_item_request,parent,false)

            binding.lifecycleOwner = this@FriendFragment
            return RequestHolder(binding)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if(holder is RequestHolder) {
                val id = getItem(position)
                holder.bind(id)
            }
        }
    }

    inner class RequestHolder(val binding : ListItemRequestBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(id : String) {
            Log.d(TAG, "RequestHolder - ${id}")
            binding.id = id
            binding.viewModel = viewModel
        }
    }

    inner class FriendAdpater : ListAdapter<Friend, RecyclerView.ViewHolder>(FriendDiffCallback()){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val binding = DataBindingUtil.inflate<ListItemFriendBinding>(layoutInflater,R.layout.list_item_friend,parent,false)
            binding.lifecycleOwner = this@FriendFragment
            return FriendHolder(binding)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if(holder is FriendHolder) {
                val item = getItem(position)
                holder.bind(item)
            }
        }

    }
    inner class FriendHolder(val binding : ListItemFriendBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item : Friend) {
            Log.d(TAG, "FriendHolder - ${item}")
            binding.item = item
            binding.viewModel = viewModel

            if(!item.friend)
                binding.buttonSee.text = getString(R.string.waiting)
        }
    }

    inner class FriendDiffCallback : DiffUtil.ItemCallback<Friend>() {
        override fun areItemsTheSame(oldItem: Friend, newItem: Friend): Boolean =
            oldItem === newItem

        override fun areContentsTheSame(oldItem: Friend, newItem: Friend): Boolean =
            oldItem == newItem
    }

    inner class RequestDiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean =
            oldItem === newItem

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean =
            oldItem == newItem
    }
}
