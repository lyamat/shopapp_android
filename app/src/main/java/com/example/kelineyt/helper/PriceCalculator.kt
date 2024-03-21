package com.example.kelineyt.helper

fun Float?.getProductPrice(price: Float): Float {
    //this --> Percentage
    if (this == null)
        return price

    val discountDecimal = this / 100.0f
    val remainingPricePercentage = 1f - discountDecimal

    return remainingPricePercentage * price
}