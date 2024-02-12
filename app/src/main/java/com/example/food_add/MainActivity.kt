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

            if (expandableLayout_time.visibility == View.GONE) {
                expandableLayout_time.visibility = View.VISIBLE
                cardview_time.visibility = View.VISIBLE
            } else {
                expandableLayout_time.visibility = View.GONE
                cardview_time.visibility = View.GONE
            }
        }

        binding.foodaddBtn.setOnClickListener {
            addFoodToList()
        }

        deleteData()

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
                    saveFoodListToSharedPreferences(mList, mutableListOf())
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
        val swipegesture = object : SwipeGesture(this){
            override fun onSwiped(viewHolder: ViewHolder,direction:Int){
                when(direction){
                    ItemTouchHelper.LEFT ->{
                        adapter.deleteItem(viewHolder.absoluteAdapterPosition)
                        // 삭제된 음식을 SharedPreferences에 저장
                        saveFoodListToSharedPreferences(mList, deletedItems)
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
    ) {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val foodSet = foodList.map { "${it.title}|${it.calories}" }.toMutableSet()
        sharedPreferences.edit().putStringSet("foods", foodSet).apply()

        // 삭제된 음식 리스트 저장
        sharedPreferences.edit().putStringSet("deletedItems", deletedItems.toSet()).apply()
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

        // 삭제된 음식 및 수정된 음식 적용
        mList.removeAll { deletedItems.contains(it.title) }
        adapter.notifyDataSetChanged()
    }

}