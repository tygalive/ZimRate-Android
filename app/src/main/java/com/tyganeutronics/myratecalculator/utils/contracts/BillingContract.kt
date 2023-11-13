package com.tyganeutronics.myratecalculator.utils.contracts

import com.tyganeutronics.myratecalculator.R

object BillingContract {
    private const val SKU_TINY_DONATION = "tiny_donation"
    private const val SKU_SMALL_DONATION = "small_donation"
    private const val SKU_MEDIUM_DONATION = "medium_donation"
    private const val SKU_LARGE_DONATION = "large_donation"
    private const val SKU_HUGE_DONATION = "huge_donation"
    private const val SKU_GIGANTIC_DONATION = "gigantic_donation"

    val ids: List<String>
        get() {
            return listOf(
                SKU_TINY_DONATION,
                SKU_SMALL_DONATION,
                SKU_MEDIUM_DONATION,
                SKU_LARGE_DONATION,
                SKU_HUGE_DONATION,
                SKU_GIGANTIC_DONATION
            )
        }

    val coins: Map<String, Pair<Long, Long>>
        get() {
            return buildMap {
                put(SKU_TINY_DONATION, Pair(500, 100))
                put(SKU_SMALL_DONATION, Pair(1000, 200))
                put(SKU_MEDIUM_DONATION, Pair(1500, 500))
                put(SKU_LARGE_DONATION, Pair(2000, 1000))
                put(SKU_HUGE_DONATION, Pair(3000, 1500))
                put(SKU_GIGANTIC_DONATION, Pair(5000, 2000))
            }
        }

    fun mapSkuToViewId(sku: String): Int? {
        return when (sku) {
            SKU_TINY_DONATION -> R.id.purchase_coins_tiny
            SKU_SMALL_DONATION -> R.id.purchase_coins_small
            SKU_MEDIUM_DONATION -> R.id.purchase_coins_medium
            SKU_LARGE_DONATION -> R.id.purchase_coins_large
            SKU_HUGE_DONATION -> R.id.purchase_coins_huge
            SKU_GIGANTIC_DONATION -> R.id.purchase_coins_gigantic
            else -> null
        }
    }

    fun mapViewIdToSku(viewId: Int): String {
        return when (viewId) {
            R.id.purchase_coins_tiny -> SKU_TINY_DONATION
            R.id.purchase_coins_small -> SKU_SMALL_DONATION
            R.id.purchase_coins_medium -> SKU_MEDIUM_DONATION
            R.id.purchase_coins_large -> SKU_LARGE_DONATION
            R.id.purchase_coins_huge -> SKU_HUGE_DONATION
            R.id.purchase_coins_gigantic -> SKU_GIGANTIC_DONATION
            else -> ""
        }
    }
}