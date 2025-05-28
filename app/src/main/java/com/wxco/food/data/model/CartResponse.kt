package com.wxco.food.data.model

data class CartResponse(
    val sepet_yemekler: List<CartFood>?,
    val success: Int
)

data class CrudResponse(
    val success: Int,
    val message: String
) 