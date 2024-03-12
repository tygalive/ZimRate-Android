package com.tyganeutronics.myratecalculator.database.models

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.tyganeutronics.myratecalculator.AppZimrate
import com.tyganeutronics.myratecalculator.database.entities.SpendEntity

object SpendModel {

    fun consume(context: Context, credits: Long, type: String, description: String) {

        AppZimrate.database.let {
            it.transactionExecutor.execute {

                var rewards = emptyList<Long>()

                for (i in 0..credits) {
                    // If there is no active reward, over draw the longest living reward
                    val reward =
                        it.rewards().oldestActiveReward() ?: it.rewards().longestLivingReward()!!

                    reward.balance--
                    reward.dirty = true
                    reward.save()

                    rewards = rewards.plus(reward.id).distinct()
                }

                if (rewards.isNotEmpty()) {
                    val purchase = SpendEntity()
                    purchase.rewards = rewards
                    purchase.amount = credits
                    purchase.type = type
                    purchase.description = description.format(credits)
                    purchase.save()
                }
            }
        }

        val bundle = Bundle()
        bundle.putLong("amount", credits)

        FirebaseAnalytics.getInstance(context).logEvent("spend_coins", bundle)
    }

    fun daysStreak(days: Int = 7): List<Boolean> {

        val streak = arrayOfNulls<Boolean>(days)
        streak.fill(false)

        AppZimrate.database.let {
            for (i in 0 until days) {
                if (it.rewards().getDayRewards(i).isEmpty()) {
                    break
                }

                streak[i] = true
            }
        }

        return streak.toList().filterNotNull()
    }

}