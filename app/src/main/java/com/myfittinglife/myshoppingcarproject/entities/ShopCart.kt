package com.myfittinglife.myshoppingcarproject.entities

import android.util.Log
import java.math.BigDecimal

/**
 * @Author LD
 * @Time 2020/12/9 11:30
 * @Describe 购物车实体类
 * @Modify
 */
class ShopCart {
    //总共的商品数量
    var totalAmount:Int = 0
    //总共的商品价格
    var totalPrice:BigDecimal = BigDecimal.valueOf(0.0)

    //保存各个产品添加的数量，将产品作为key，因为每个产品都不一样
    val productNumMap: MutableMap<Product, Int> = mutableMapOf()
    //保存同类产品的数量
    val typeCountMap:MutableMap<String,Int> = mutableMapOf()


    //商品数量添加
    fun addProductNum(product: Product){

        //该商品数量+1
        if(productNumMap.containsKey(product)){
            productNumMap[product] = productNumMap[product]!!+1
        }else{
            productNumMap[product] = 1
        }

        //该商品所在类别数量+1
        //虽然是第一次添加商品，但对于parentId来说，第一次添加的商品这个所属的类的数量可能为0
        if(typeCountMap.containsKey(product.parentId)){
            typeCountMap[product.parentId] = typeCountMap[product.parentId]!!+1
        }else{
            typeCountMap[product.parentId] = 1
        }

        //总体数量+1
        totalAmount += 1
        //总体价格
        totalPrice = totalPrice.add(BigDecimal.valueOf(product.productMoney))
    }

    //商品数量减少
    fun reduceProductNum(product: Product){
        //该商品数量-1
        productNumMap[product] = productNumMap[product]!!-1
        //该商品所在的类别数量-1
        typeCountMap[product.parentId] = typeCountMap[product.parentId]!!-1

        //总体数量-1
        totalAmount -= 1
        //总体价格
        totalPrice = totalPrice.subtract(BigDecimal.valueOf(product.productMoney))
    }



}