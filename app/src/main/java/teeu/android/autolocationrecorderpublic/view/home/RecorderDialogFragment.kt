package teeu.android.autolocationrecorderpublic.view.home

import android.app.AlertDialog
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import teeu.android.autolocationrecorderpublic.R
import teeu.android.autolocationrecorderpublic.Repository
import teeu.android.autolocationrecorderpublic.model.Record

private const val TAG = "MYTAG"
class RecorderDialogFragment : DialogFragment() {
    companion object {
        fun newInstance(record: Record) : DialogFragment {
            return RecorderDialogFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("record", record)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                        putParcelable("record",record)
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val r = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("record",Record::class.java)
        } else {
            arguments?.getSerializable("record") as Record
        }

        val record = r!!

//        val record = arguments?.getSerializable("record") as Record
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.delete_record)
            .setPositiveButton(R.string.ok) { p0, p1 ->
                if(Repository.coin > 0) {

                
                    lifecycleScope.launch(Dispatchers.IO) {
                        Repository.getInstance().deleteRecord(record)
                    }
                    Toast.makeText(context,"DELETE",Toast.LENGTH_SHORT).show()

                    Repository.coin--

                    if(Repository.account != null) {
               
                        
                        
                        
                        val list = Repository.recordListFromFirestore.value
                        list?.remove(record)
                        Repository.recordListFromFirestore.value = list
                    }
                } else
                    Toast.makeText(context,R.string.no_coin,Toast.LENGTH_SHORT).show()

            }
            .setNegativeButton(R.string.cancel) { p0, p1 ->

            }
            .create()
    }
}
