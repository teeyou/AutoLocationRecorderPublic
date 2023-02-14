package teeu.android.autolocationrecorderpublic.view.ad

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import teeu.android.autolocationrecorderpublic.R
import teeu.android.autolocationrecorderpublic.Repository
import teeu.android.autolocationrecorderpublic.databinding.FragmentAdBinding

private const val TAG = "MYTAG"
//private const val AD_ID = "ca-app-pub-3940256099942544/1033173712" //테스트용
private const val AD_ID = "YOUR_AD_ID"
class AdFragment : Fragment() {
    private lateinit var binding : FragmentAdBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_ad,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Repository.interstitialAd.observe(viewLifecycleOwner) {
            if (it != null) {
                it.show(requireActivity())
            } else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        loadAds()
    }

    fun loadAds() {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(requireContext(), AD_ID, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(TAG, adError.message)
                Repository.interstitialAd.value = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d(TAG, "Ad was loaded.")
                Repository.interstitialAd.value = interstitialAd

                Repository.interstitialAd.value?.fullScreenContentCallback = object: FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        Log.d(TAG, "Ad was dismissed.")
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        Log.d(TAG, "Ad failed to show.")
                    }
                    override fun onAdShowedFullScreenContent() {
                        Log.d(TAG, "Ad showed fullscreen content.")
                        Repository.interstitialAd.value = null;
                        Repository.coin += 1
                        if(Repository.account != null)
                            
                    }
                }
            }
        })
    }
}
