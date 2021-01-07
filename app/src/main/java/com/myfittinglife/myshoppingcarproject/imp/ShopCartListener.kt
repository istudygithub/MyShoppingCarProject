package com.myfittinglife.myshoppingcarproject.imp

import android.view.View

/**
 * @Author LD
 * @Time 2020/12/9 11:22
 * @Describe 右侧商品数据增加减少点击接口
 * @Modify
 */
interface ShopCartListener {

    fun onReduce(parentId:String, parentCount:Int){

    }

    fun onAdd(view: View, parentId: String, parentCount: Int){

    }
}