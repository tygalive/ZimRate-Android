package com.tyganeutronics.myratecalculator.fragments.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.tyganeutronics.myratecalculator.R
import com.tyganeutronics.myratecalculator.database.contract.RewardContract
import com.tyganeutronics.myratecalculator.database.models.RewardModel
import com.tyganeutronics.myratecalculator.database.models.SpendModel
import com.tyganeutronics.myratecalculator.interfaces.AdFragmentSubscriberInterface
import com.tyganeutronics.myratecalculator.interfaces.RewardsActivity
import com.tyganeutronics.myratecalculator.ui.base.BaseFragment
import com.tyganeutronics.myratecalculator.utils.ads.rewarded.MaxRewardedAdListener
import com.tyganeutronics.myratecalculator.utils.contracts.RemoteConfigContract
import com.tyganeutronics.myratecalculator.utils.traits.requireViewById
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference


class FragmentSectionRewards : BaseFragment(), OnClickListener, AdFragmentSubscriberInterface {

    override fun onCanReshowAd() {
        requireViewById<AppCompatButton>(R.id.btn_trigger_earn_award).apply {
            val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig

            isEnabled = true
            text = getString(
                R.string.rewards_earn_advert,
                remoteConfig.getLong(RemoteConfigContract.REWARD_WATCH_ADVERT)
            )
        }
    }

    override fun onAdReady() {
        onCanReshowAd()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CoroutineScope(Dispatchers.Main).launch {

            MaxRewardedAdListener.apply {
                adUnit = getString(R.string.ads_max_video_id)
                adSubscriberRef = WeakReference(this@FragmentSectionRewards)
                loadAd()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile_rewards, container, false)
    }

    override fun bindViews() {
        super.bindViews()

        requireViewById<LinearLayoutCompat>(R.id.btn_show_award_history).setOnClickListener(this)
        requireViewById<AppCompatButton>(R.id.btn_trigger_earn_award).setOnClickListener(this)
    }

    override fun syncViews() {
        super.syncViews()

        CoroutineScope(Dispatchers.Main).launch {
            val streak = SpendModel.daysStreak()

            listOf(
                R.id.rewards_amount_day_1,
                R.id.rewards_amount_day_2,
                R.id.rewards_amount_day_3,
                R.id.rewards_amount_day_4,
                R.id.rewards_amount_day_5,
                R.id.rewards_amount_day_6,
                R.id.rewards_amount_day_7,
            ).forEachIndexed { index, id ->
                requireViewById<AppCompatTextView>(id).apply {
                    text = RewardModel.dayClockInReward(index).toString()

                    val drawable =
                        if (streak[index]) R.drawable.ic_rewarded else R.drawable.ic_reward
                    setCompoundDrawablesWithIntrinsicBounds(0, 0, drawable, 0)
                }
            }
        }

        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig

        requireViewById<AppCompatButton>(R.id.btn_trigger_earn_award).apply {
            text = getString(
                R.string.rewards_earn_advert,
                remoteConfig.getLong(RemoteConfigContract.REWARD_WATCH_ADVERT)
            )
        }
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.btn_show_award_history -> {
                    val bundle = Bundle()
                    bundle.putString(RewardContract.COLUMN_NAME_TYPE, "")

                    (requireActivity() as RewardsActivity).showRewardHistory(bundle)
                }

                R.id.btn_trigger_earn_award -> {
                    requireViewById<AppCompatButton>(R.id.btn_trigger_earn_award).apply {
                        isEnabled = false
                        text = getString(R.string.rewards_earn_advert_loading)
                    }

                    MaxRewardedAdListener.apply {
                        if (rewardedAd.isReady) {
                            rewardedAd.showAd()
                        }
                    }
                }
            }
        }
    }
}