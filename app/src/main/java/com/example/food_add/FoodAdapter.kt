package com.example.food_add

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FoodAdapter(var mList : MutableList<FoodData>) :
    RecyclerView.Adapter<FoodAdapter.FoodViewHolder>(){

    private var onItemClickListener: ((String) -> Unit)? = null

    fun setOnItemClickListener(listener: (String) -> Unit) {
        onItemClickListener = listener
    }

    inner class FoodViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val titleTv : TextView = itemView.findViewById(R.id.titleTv)
        val titleTv2 : TextView = itemView.findViewById(R.id.titleTv2)

        init {
            // 아이템 뷰가 클릭되었을 때의 동작을 정의
            itemView.setOnClickListener {
                onItemClickListener?.invoke(mList[absoluteAdapterPosition].title)
            }
        }
    }

    fun setFilteredList(mList: List<FoodData>){
        this.mList = mList.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view =LayoutInflater.from(parent.context).inflate(R.layout.each_item,parent,false)
        return FoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        holder.titleTv.text = mList[position].title
        holder.titleTv2.text = mList[position].calories.toString()
    }

    override fun getItemCount(): Int {
        return mList.size
    }


    //삭제
    fun deleteItem(i : Int){
        mList.removeAt(i)
        notifyDataSetChanged()
    }

//    fun saveData(context: Context) {
//        val preferences = context.getSharedPreferences("food_data", Context.MODE_PRIVATE)
//        val editor = preferences.edit()
//        val jsonStringList = mList.map { it.toJsonString() }
//        editor.putStringSet("food_list", jsonStringList.toSet())
//        editor.apply()
//    }
//
//    fun loadData(context: Context) {
//        val preferences = context.getSharedPreferences("food_data", Context.MODE_PRIVATE)
//        val jsonStringList = preferences.getStringSet("food_list", emptySet())
//        mList = jsonStringList?.map { FoodData.fromString(it) }?.toMutableList() ?: mutableListOf()
//        notifyDataSetChanged()
//    }
}