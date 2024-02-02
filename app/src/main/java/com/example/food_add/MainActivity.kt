package com.example.food_add

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
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
import android.widget.TextView
import android.widget.TimePicker
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView.ViewHolder

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var cardView : MaterialCardView
    private lateinit var imageView1 : ImageView
    private lateinit var expandableLayout: View
    private lateinit var expandableLayout2 : View
    private var mList: MutableList<FoodData> = mutableListOf()
    private lateinit var adapter: FoodAdapter
    private lateinit var binding: ActivityMainBinding
    private lateinit var expandBtn: Button
    private var isImage1Visible = true
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
        recyclerView = binding.recyclerView
        searchView = binding.searchView
        expandableLayout = binding.expandableLayout
        expandableLayout2 = binding.expandableLayout2
        expandBtn = binding.expandBtn
        imageView1 = binding.foodimageView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = FoodAdapter(mList)
        recyclerView.adapter = adapter
        gramEditText = findViewById(R.id.gramEditText)
        foodAddEditText = findViewById(R.id.foodAddEditText)
        kcalAddEditText = findViewById(R.id.kcalAddEditText)
        timePicker = findViewById(R.id.timePicker)
        

        addDefaultFoodToList()

        // loadData()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }
        })

        binding.addbutton.setOnClickListener() {
            // saveData()
        }

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

        binding.expandBtn2.setOnClickListener {

            addFoodToList()

        }

        deleteData()

        // 리스트 아이템 클릭 이벤트 처리
        adapter.setOnItemClickListener { foodName ->
            // name: 클릭된 이름
            toggleExpandableLayout(foodName)

            expandableLayout.visibility = View.GONE
            cardView.visibility = View.GONE

            // EditText에서 그램을 입력받는 부분
            val kcal = getgramFromEditText()

            // TimePicker에서 시간을 입력받는 부분
            val time = getTimeFromTimePicker()

            // SharedPreferences에 데이터 저장
            val userPreferences = UserFoodClass(this)
            userPreferences.saveUserData(foodName, kcal, time)
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

        addFoodToList(mList.size - 1)
    }

    private fun addFoodToList(nextPosition: Int? = null) {
        Log.d("addfoodTOList","함수진입1")
        val userInput = foodAddEditText.text.toString().trim()
        val userInput2 = kcalAddEditText.text.toString().trim()



        if (userInput2.isNotEmpty()) {
            try {
                val calories = userInput2.toInt()
                // 이제 calories 변수에 정수 값이 들어 있습니다.
                // mList에 추가하는 등의 작업을 수행할 수 있습니다.

                if (userInput.isNotEmpty()) {
                    Log.d("addfoodTOList", "함수진입2")

                    if (nextPosition != null) {
                        mList.add(nextPosition, FoodData(userInput, calories))
                    } else {
                        mList.add(FoodData(userInput, calories))
                    }

                    Log.d("addfoodTOList", "함수진입3")

                    // 리스트 업데이트 등 추가적인 처리를 할 수 있음
                    // 예: 어댑터에 변경 사항 알림, UI 갱신 등

                    adapter.notifyDataSetChanged()
                }

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
            imageView1.setImageDrawable(getDrawable(R.drawable.group_124))

        } else {
            imageView1.setImageDrawable(getDrawable(R.drawable.group_126))
        }

        isImage1Visible = !isImage1Visible
    }

    private fun deleteData(){
        Log.d("deleteData","함수진입")

        val swipegesture = object : SwipeGesture(this){
            override fun onSwiped(viewHolder: ViewHolder,direction:Int){
                when(direction){
                    ItemTouchHelper.LEFT ->{
                        adapter.deleteItem(viewHolder.absoluteAdapterPosition)
                    }
                }
            }
        }

        val touchHelper = ItemTouchHelper(swipegesture)
        touchHelper.attachToRecyclerView((recyclerView))
    }

//    override fun onPause() {
//        super.onPause()
//        adapter.saveData(this)
//    }
//
//    override fun onResume() {
//        super.onResume()
//        adapter.loadData(this)
//    }

    private fun toggleExpandableLayout(clickedItemTitle: String) {

        //text를 foodname으로 바꾸는 함수

    }

    fun getgramFromEditText(): Int {
        val gramText = gramEditText.text.toString()
        return if (gramText.isNotEmpty()) {
            gramText.toInt()
        } else {
            // 기본값 또는 에러 처리를 원하는 대로 설정
            0
        }
    }

    fun getTimeFromTimePicker(): String {
        val hour = if (Build.VERSION.SDK_INT >= 23) {
            timePicker.hour
        } else {
            timePicker.currentHour
        }

        val minute = if (Build.VERSION.SDK_INT >= 23) {
            timePicker.minute
        } else {
            //timePicker.currentMinute
        }

        // 시간을 원하는 형식으로 포맷팅
        return String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
    }

}
