package com.myfittinglife.myshoppingcarproject.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.myfittinglife.myshoppingcarproject.R
import com.myfittinglife.myshoppingcarproject.entities.ProductItem

/**
 * @Author LD
 * @Time 2020/12/8 11:35
 * @Describe 左侧分类适配器
 * @Modify
 */
class LeftTypeAdapter(var dataList: MutableList<ProductItem>) : RecyclerView.Adapter<LeftTypeAdapter.MyLeftViewHolder>() {

    //默认选中的位置
    var selectPosition = 0
    //左侧列表的点击事件
    private var leftTypeClickListener:LeftTypeClickListener? = null

    //要更新的ID号
    var mUpdateTypeId=""
    //要更新的ID号的数量
    var mUpdateTypeCount =0

    companion object{
        const val TAG = "ceshi_left"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeftTypeAdapter.MyLeftViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.left_menu_item,parent,false)
        return MyLeftViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: LeftTypeAdapter.MyLeftViewHolder, position: Int) {
        var productItem = dataList[position]
        holder.typeName.text = productItem.typeName

        //是选中位置,小红线显示
        if(selectPosition==position){
            holder.typeLine.visibility = View.VISIBLE
            holder.typeLayout.isSelected = true
        }else{
            //不是则小红线隐藏
            holder.typeLine.visibility = View.INVISIBLE
            //布局设置为未选中
            holder.typeLayout.isSelected = false
        }

        //设置数量(要更新的类别的产品数量，将productItem的bean类内数量进行更新)
        if(productItem.typeId==mUpdateTypeId){
            productItem.typeSelectCount = mUpdateTypeCount
        }
        //设置数量显示
        if(productItem.typeSelectCount>0){
            holder.typeSelectCount.visibility = View.VISIBLE
            holder.typeSelectCount.text = productItem.typeSelectCount.toString()
        }else{
            holder.typeSelectCount.visibility = View.INVISIBLE
        }

        //左侧布局的点击事件
        holder.typeLayout.setOnClickListener {
            if(holder.adapterPosition in 0 until itemCount){
                this.selectPosition = holder.adapterPosition
                //这个就实现了selected的更新了
                notifyDataSetChanged()
            }
            //为啥一直是-1，原Demo却可以用adapterPosition（https://www.jianshu.com/p/64ab65e6dde8）
            Log.i(TAG, "onBindViewHolder: 点击的位置为adapterPosition：${holder.adapterPosition},position为：$position")
            //写个接口回调来滑动右侧recyclerview布局
            leftTypeClickListener?.onItemClick(position)
        }

    }

    inner class MyLeftViewHolder(view: View):RecyclerView.ViewHolder(view){

        //名称
        val typeName = view.findViewById<TextView>(R.id.left_menu_textview)
        //已选中的数量
        val typeSelectCount = view.findViewById<TextView>(R.id.tv_left_menu_count)
        //左侧布局(为了更改背景颜色)
        val typeLayout = view.findViewById<LinearLayout>(R.id.left_menu_item)
        //左侧选中时的线条
        val typeLine = view.findViewById<View>(R.id.v_left_menu_item_line)

    }

    /**
     * 接口回调
     */
    interface  LeftTypeClickListener{
        //点击布局
        fun onItemClick(position: Int)
    }

    /**
     * 设置监听器
     */
    fun setLeftTypeClickListener(_leftTypeClickListener: LeftTypeClickListener){
        leftTypeClickListener =_leftTypeClickListener
    }

    /**
     * 设置左侧选中的区域，主要用于滑动右侧时需要联动左侧列表
     */
    fun setSelectedNum(selectedNum:Int){
        if(selectedNum in 0 until itemCount){
            selectPosition = selectedNum
            notifyDataSetChanged()
        }
    }

    /**
     * 更新左侧列表角标(用于右侧添加或减少商品时它数量的更新)
     */
    fun updateTypeCount(parentId:String,parentCount:Int){

        mUpdateTypeId = parentId
        mUpdateTypeCount = parentCount
        notifyDataSetChanged()
    }
}