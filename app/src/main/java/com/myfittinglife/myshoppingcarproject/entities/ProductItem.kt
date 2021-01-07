package com.myfittinglife.myshoppingcarproject.entities

/**
 * @Author LD
 * @Time 2020/12/8 11:15
 * @Describe 数据Bean类
 * @Modify
 */
//总体分类
data class ProductItem (
    var productList: List<Product>,
    //类别ID
    var typeId: String,
    //类别名称
    var typeName: String
):ProductItemParent()

//该分类中的某个产品
data class Product(
    var parentId: String,
    //产品ID
    var productId: String,
    //产品图片
    var productImg: Int,
    //产品价格
    var productMoney: Double,
    //产品月销售量
    var productMonthSale: Int,
    //产品名称
    var productName: String
)