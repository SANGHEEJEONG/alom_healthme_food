package com.example.food_add

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.food_add.databinding.ActivityMainBinding
import com.google.android.material.card.MaterialCardView
import java.util.Locale
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TimePicker
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.food_add.SharedPreference.addUserInput


class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var cardView : MaterialCardView
    private lateinit var cardview_time : MaterialCardView
    private lateinit var foodimageView : ImageView
    private lateinit var timeimageView : ImageView
    private lateinit var expandableLayout: View
    private lateinit var expandableLayout_time: View
    private var mList: MutableList<FoodData> = mutableListOf()
    private val deletedItems: MutableList<String> = mutableListOf()
    private lateinit var adapter: FoodAdapter
    private lateinit var binding: ActivityMainBinding
    private lateinit var expandBtn: Button
    private lateinit var expandBtn_time: Button
    private lateinit var foodaddBtn: Button
    private var isImage1Visible = true
    private var isImage2Visible = true
    private lateinit var gramEditText: EditText
    private lateinit var foodAddEditText: EditText
    private lateinit var kcalAddEditText: EditText
    private lateinit var timePicker: TimePicker



    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        cardView = binding.materialCardView
        cardview_time = binding.materialCardViewTime
        recyclerView = binding.recyclerView
        searchView = binding.searchView
        expandableLayout = binding.expandableLayout
        expandableLayout_time = binding.expandableLayoutTime
        expandBtn = binding.expandBtn
        expandBtn_time = binding.expandBtnTime
        foodaddBtn = binding.foodaddBtn
        foodimageView = binding.foodimageView
        timeimageView = binding.timeimageView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = FoodAdapter(mList,this)
        recyclerView.adapter = adapter
        gramEditText = findViewById(R.id.gramEditText)
        foodAddEditText = findViewById(R.id.foodAddEditText)
        kcalAddEditText = findViewById(R.id.kcalAddEditText)
        timePicker = findViewById(R.id.timePicker)


        addDefaultFoodToList()
        loadSavedData()

//        val userInputDataList = SharedPreference.loadUserInputList(this)
//        userInputDataList.forEach { (userInput, calories) ->
//            val userIndex = mList.indexOfFirst { it.title == userInput }
//            val deletedIndices = SharedPreference.loadDeletedItemIndices(this)
//            if (userIndex == -1 || userIndex !in deletedIndices) {
//                mList.add(FoodData(userInput, calories))
//            }
//        }
//        adapter.notifyDataSetChanged()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }
        })


        binding.expandBtn.setOnClickListener {
            toggleImage()

            if (expandableLayout.visibility == View.GONE) {
                expandableLayout.visibility = View.VISIBLE
                cardView.visibility = View.VISIBLE
            } else {
                expandableLayout.visibility = View.GONE
                cardView.visibility = View.GONE
            }

        }

        binding.expandBtnTime.setOnClickListener {
            toggleImageTime()

            Log.d("eT", "함수진입1")
            if (expandableLayout_time.visibility == View.GONE) {
                Log.d("eT", "함수진입2")
                expandableLayout_time.visibility = View.VISIBLE
                cardview_time.visibility = View.VISIBLE
                Log.d("eT", "함수진입3")
            } else {
                expandableLayout_time.visibility = View.GONE
                cardview_time.visibility = View.GONE
            }
        }

        binding.foodaddBtn.setOnClickListener {
            addFoodToList()
        }

        deleteData()

        // 리스트 아이템 클릭 이벤트 처리
        adapter.setOnItemClickListener { foodName ->
            toggleExpandableLayout(foodName)

            expandableLayout.visibility = View.GONE
            cardView.visibility = View.GONE
        }

        binding.timePicker.setOnTimeChangedListener { timePicker, hour, minute ->
            val timeFormat = if (hour > 12) {
                val adjustedHour = hour - 12
                "오후 $adjustedHour 시 $minute 분"
            } else {
                "오전 $hour 시 $minute 분"
            }

            binding.expandBtnTime.text = timeFormat
            expandBtn_time.setTextColor(Color.parseColor("#000000"))
        }

    }

    private fun filterList(query: String?) {
        if (query != null) {
            val filteredList = ArrayList<FoodData>()
            for (i in mList) {
                if (i.title.lowercase(Locale.ROOT).contains(query)) {
                    filteredList.add(i)
                }
            }
            if (filteredList.isEmpty()) {
                //Toast.makeText(this, "검색 결과 없음", Toast.LENGTH_SHORT).show()
            } else {
                adapter.setFilteredList(filteredList)
            }
        }
    }

    private fun addDefaultFoodToList() {
        val nextPosition = mList.size // 다음 위치는 현재 리스트의 크기와 같습니다.
        mList.add(FoodData("피자",100))
        mList.add(FoodData("치킨",200))
        mList.add(FoodData("떡볶이",300))
        mList.add(FoodData("김치볶음밥",400))
        mList.add(FoodData("짜장면",500))
        mList.add(FoodData("짬뽕",600))
        mList.add(FoodData("탕수육",700))
        mList.add(FoodData("초코우유",800))
        mList.add(FoodData("딸기우유",900))

        adapter.notifyDataSetChanged()
    }

    private fun addFoodToList() {
        val userInput = foodAddEditText.text.toString().trim()
        val userInput2 = kcalAddEditText.text.toString().trim()

        if (userInput.isNotEmpty()&&userInput2.isNotEmpty()) {
            try {
                    val newFood = FoodData(userInput, userInput2.toInt())
                    mList.add(newFood)
                    adapter.notifyItemInserted(mList.size - 1)
                    saveFoodListToSharedPreferences(mList, mutableListOf(), mutableListOf())
            } catch (e: NumberFormatException) {
                // 변환 실패 처리
                // 사용자가 정수로 변환할 수 없는 값을 입력한 경우 예외가 발생합니다.
            }
        }
        foodAddEditText.text.clear()
        kcalAddEditText.text.clear()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun toggleImage() {
        if (isImage1Visible) {
            foodimageView.setImageDrawable(getDrawable(R.drawable.group_124))
        } else {
            foodimageView.setImageDrawable(getDrawable(R.drawable.group_126))
        }

        isImage1Visible = !isImage1Visible
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun toggleImageTime() {
        if (isImage2Visible) {
            timeimageView.setImageDrawable(getDrawable(R.drawable.group_124))

        } else {
            timeimageView.setImageDrawable(getDrawable(R.drawable.group_126))
        }

        isImage2Visible = !isImage2Visible
    }

     private fun deleteData(){
        Log.d("deleteData","함수진입")
        val swipegesture = object : SwipeGesture(this){
            override fun onSwiped(viewHolder: ViewHolder,direction:Int){
                when(direction){
                    ItemTouchHelper.LEFT ->{
                        adapter.deleteItem(viewHolder.absoluteAdapterPosition)
                        // 삭제된 음식을 SharedPreferences에 저장
                        saveFoodListToSharedPreferences(mList, deletedItems, mutableListOf())
                    }
                }
            }
        }
        val touchHelper = ItemTouchHelper(swipegesture)
        touchHelper.attachToRecyclerView((recyclerView))
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun toggleExpandableLayout(clickedItemTitle: String) {
        expandBtn.text = clickedItemTitle
        expandBtn.setTextColor(Color.parseColor("#000000"))
        toggleImage()
    }

    private fun saveFoodListToSharedPreferences(
        foodList: MutableList<FoodData>,
        deletedItems: MutableList<String>,
        modifiedItems: MutableList<FoodData>
    ) {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val foodSet = foodList.map { "${it.title}|${it.calories}" }.toMutableSet()
        sharedPreferences.edit().putStringSet("foods", foodSet).apply()

        // 삭제된 음식 리스트 저장
        sharedPreferences.edit().putStringSet("deletedItems", deletedItems.toSet()).apply()

        // 수정된 음식 리스트 저장
        val modifiedFoodSet = modifiedItems.map { "${it.title}|${it.calories}" }.toMutableSet()
        sharedPreferences.edit().putStringSet("modifiedItems", modifiedFoodSet).apply()
    }

    private fun loadSavedData() {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val foodSet = sharedPreferences.getStringSet("foods", setOf()) ?: setOf()
        mList.clear() // 기존 데이터를 지우고 새로 불러옴
        foodSet.forEach { food ->
            val (title, calories) = food.split("|")
            mList.add(FoodData(title, calories.toInt()))
        }
        adapter.notifyDataSetChanged()

        // 삭제된 음식 리스트 불러오기
        val deletedItemsSet = sharedPreferences.getStringSet("deletedItems", setOf()) ?: setOf()
        val deletedItems = deletedItemsSet.toMutableList()

        // 수정된 음식 리스트 불러오기
        val modifiedItemsSet = sharedPreferences.getStringSet("modifiedItems", setOf()) ?: setOf()
        val modifiedItems = modifiedItemsSet.map {
            val (title, calories) = it.split("|")
            FoodData(title, calories.toInt())
        }.toMutableList()

        // 삭제된 음식 및 수정된 음식 적용
        mList.removeAll { deletedItems.contains(it.title) }
        modifiedItems.forEach { modifiedItem ->
            mList.find { it.title == modifiedItem.title }?.apply {
                this.calories = modifiedItem.calories // 수정된 칼로리 적용
            }
        }
        adapter.notifyDataSetChanged()
    }


//    fun getgramFromEditText(): Int {
//        val gramText = gramEditText.text.toString()
//        return if (gramText.isNotEmpty()) {
//            gramText.toInt()
//        } else {
//            // 기본값 또는 에러 처리를 원하는 대로 설정
//            0
//        }
//    }

//    @Suppress("DEPRECATION")
//    fun getTimeFromTimePicker(): String {
//        val hour: Int
//        val minute: Int
//
//        if (Build.VERSION.SDK_INT >= 23) {
//            hour = timePicker.hour
//            minute = timePicker.minute
//        } else {
//            @Suppress("DEPRECATION")
//            hour = timePicker.currentHour
//            @Suppress("DEPRECATION")
//            minute = timePicker.currentMinute
//        }
//
//        // 시간을 원하는 형식으로 포맷팅
//        return String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
}