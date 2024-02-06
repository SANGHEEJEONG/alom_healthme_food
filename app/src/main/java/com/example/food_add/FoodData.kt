package com.example.food_add

import java.io.Serializable

data class FoodData(
    var title: String,
    var calories: Int
) : Serializable