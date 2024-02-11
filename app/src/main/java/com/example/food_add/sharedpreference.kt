package com.example.food_add

// SharedPreferenceUtils.kt

import android.content.Context
import android.content.SharedPreferences

object SharedPreference {

    private const val USER_INPUT_PREF_KEY = "user_input"
    private const val USER_CALORIES_PREF_KEY = "user_calories"
    private const val DELETED_ITEM_INDICES_PREF_KEY = "deleted_item_indices"

    // 사용자 입력을 추가하는 함수
    fun addUserInput(context: Context, userInput: String, calories: Int) {
        val prefs = context.getSharedPreferences("user_input", Context.MODE_PRIVATE)
        val nextPosition = prefs.getInt("next_position", 0) // 다음 위치를 불러옴
        val editor = prefs.edit()
        editor.putString("food_title_$nextPosition", userInput)
        editor.putInt("food_calories_$nextPosition", calories)
        editor.putInt("next_position", nextPosition + 1) // 다음 위치를 업데이트
        editor.apply()
    }

    // 저장된 사용자 입력을 불러오는 함수
    fun loadUserInputList(context: Context): List<Pair<String, Int>> {
        val userInputPref = getUserInputSharedPreferences(context)
        val userCaloriesPref = getUserCaloriesSharedPreferences(context)
        val nextPosition = userInputPref.getInt("next_position", 0)
        val userInputList = mutableListOf<Pair<String, Int>>()
        for (i in 0 until nextPosition) {
            val userInput = userInputPref.getString("food_title_$i", null)
            val calories = userCaloriesPref.getInt("food_calories_$i", -1)
            if (userInput != null && calories != -1) {
                userInputList.add(Pair(userInput, calories))
            }
        }
        return userInputList
    }

    // 삭제된 항목의 인덱스를 저장하기 위한 함수
    fun saveDeletedItemIndices(context: Context, indices: List<Int>) {
        val sharedPreferences = getDeletedItemIndicesSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putString(DELETED_ITEM_INDICES_PREF_KEY, indices.joinToString(","))
        editor.apply()
    }

    // 삭제된 항목의 인덱스를 불러오기 위한 함수
    fun loadDeletedItemIndices(context: Context): List<Int> {
        val sharedPreferences = getDeletedItemIndicesSharedPreferences(context)
        val indicesString = sharedPreferences.getString(DELETED_ITEM_INDICES_PREF_KEY, "")
        return if (indicesString.isNullOrEmpty()) {
            emptyList()
        } else {
            indicesString.split(",").mapNotNull { it.toIntOrNull() }
        }
    }

    // 사용자 입력을 저장하기 위한 SharedPreferences를 가져오는 함수
    private fun getUserInputSharedPreferences(context: Context): SharedPreferences =
        context.getSharedPreferences("user_input_pref", Context.MODE_PRIVATE)

    private fun getUserCaloriesSharedPreferences(context: Context): SharedPreferences =
        context.getSharedPreferences("user_calories_pref", Context.MODE_PRIVATE)

    // 삭제된 항목의 인덱스를 저장하기 위한 SharedPreferences를 가져오는 함수
    private fun getDeletedItemIndicesSharedPreferences(context: Context): SharedPreferences =
        context.getSharedPreferences("deleted_item_indices_pref", Context.MODE_PRIVATE)


}