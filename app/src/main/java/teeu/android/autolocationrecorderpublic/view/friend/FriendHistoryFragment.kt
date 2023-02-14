package teeu.android.autolocationrecorderpublic.view.friend

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import teeu.android.autolocationrecorderpublic.R
import teeu.android.autolocationrecorderpublic.databinding.FragmentFriendHistoryBinding
import teeu.android.autolocationrecorderpublic.databinding.ListItemBinding
import teeu.android.autolocationrecorderpublic.model.FirestoreData
import teeu.android.autolocationrecorderpublic.model.Record

private const val TAG = "MYTAG"
class FriendHistoryFragment : Fragment() {
    private lateinit var firestoreData : FirestoreData
    private lateinit var binding : FragmentFriendHistoryBinding
    private val myAdapter by lazy { MyAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_friend_history,container,false)
        firestoreData = arguments?.getSerializable("data") as FirestoreData
        Log.d(TAG, "firestoreData : ${firestoreData}")

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = myAdapter
            myAdapter.submitList(firestoreData.recordList.sortedByDescending { it.date })
        }

        if(firestoreData.switch) {
            binding.textViewSwitchOnOff.text = "ON"
            binding.textViewSwitchOnOff.setTextColor(Color.parseColor("#10F45C"))
        } else {
            binding.textViewSwitchOnOff.text = "OFF"
            binding.textViewSwitchOnOff.setTextColor(Color.parseColor("#FF0000"))
        }

        binding.textViewEmailId.setText(arguments?.getString("email"))

        binding.editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val list = firestoreData.recordList.filter { it.toString().contains(p0 ?: "") }.sortedByDescending { it.date }
                myAdapter.submitList(list)
            }
            override fun afterTextChanged(p0: Editable?) {}

        })

        binding.imageViewDelete.setOnClickListener {
            binding.editText.setText("")
            myAdapter.submitList(firestoreData.recordList.sortedByDescending { it.date })
        }

        return binding.root
    }

    override fun onStop() {
        super.onStop()
        val callback = context as FriendViewModel.BackButtonListener
        callback.onBack()
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
        }
    }

    inner class MyDiffCallback : DiffUtil.ItemCallback<Record>() {
        override fun areItemsTheSame(oldItem: Record, newItem: Record): Boolean =
            oldItem === newItem

        override fun areContentsTheSame(oldItem: Record, newItem: Record): Boolean =
            oldItem == newItem
    }
}