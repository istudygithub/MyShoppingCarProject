package com.myfittinglife.myshoppingcarproject.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.myfittinglife.myshoppingcarproject.R
import com.myfittinglife.myshoppingcarproject.entities.Product
import com.myfittinglife.myshoppingcarproject.entities.ShopCart
import com.myfittinglife.myshoppingcarproject.imp.ShopCartListener

/**
 * @Author LD
 * @Time 2020/12/8 14:01
 * @Describe 右侧产品适配器，没有使用多布局，而是使用ItemDecoration来实现头部
 * @Modify
 */
class RightProductAdapter(
    private var dataList: MutableList<Product>,
    private var shopCart: ShopCart
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var shopCartListener: ShopCartListener? = null

    /**
     * 产品的ViewHolder
     */
    inner class MyRightProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        //产品名称
        var productName = view.findViewById<TextView>(R.id.tv_item_life_product_name)

        //产品月售个数
        var productSaleCount = view.findViewById<TextView>(R.id.tv_item_life_product_monty)

        //产品价格
        var productPrice = view.findViewById<TextView>(R.id.tv_item_life_product_money)

        //添加到购物车的数量
        var selectCount = view.findViewById<TextView>(R.id.tv_group_list_item_count_num)

        //减
        var reduce = view.findViewById<ImageView>(R.id.iv_group_list_item_count_reduce)

        //加
        var add = view.findViewById<ImageView>(R.id.iv_group_list_item_count_add)

        //图片
        var image = view.findViewById<ImageView>(R.id.iv_item)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_life_product_bf, parent, false)
        return MyRightProductViewHolder(view)

    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        //根据下标获取商品，这个position是右侧数据展示的下标
        var product = dataList[position]

        var productHolder = holder as MyRightProductViewHolder
        //商品名称
        productHolder.productName.text = product?.productName
        //价格
        productHolder.productPrice.text = product?.productMoney.toString()
        //已售个数
        productHolder.productSaleCount.text = product?.productMonthSale.toString()
        //商品图片
        productHolder.image.setImageResource(product?.productImg)
        //已下单的个数显示与隐藏
        if (shopCart.productNumMap.containsKey(product)) {
            if (shopCart.productNumMap[product]!! > 0) {
                //数量不为零
                productHolder.selectCount.text = shopCart.productNumMap[product].toString()
                productHolder.selectCount.visibility = View.VISIBLE
                productHolder.reduce.visibility = View.VISIBLE
            } else {
                //数量为0不显示
                productHolder.selectCount.visibility = View.INVISIBLE
                productHolder.reduce.visibility = View.INVISIBLE
            }
        } else {
            productHolder.selectCount.visibility = View.INVISIBLE
            productHolder.reduce.visibility = View.INVISIBLE
        }

        //增加商品
        productHolder.add.setOnClickListener {

            product?.let { it1->
                shopCart.addProductNum(it1)
                notifyItemChanged(position)
                shopCartListener?.onAdd(
                    it,
                    product.parentId,
                    shopCart.typeCountMap[product.parentId]!!
                )
            }
        }
        //减少商品
        productHolder.reduce.setOnClickListener {

            product?.let {it1->
                shopCart.reduceProductNum(it1)
                notifyItemChanged(position)
                shopCartListener?.onReduce(
                    product.parentId,
                    shopCart.typeCountMap[product.parentId]!!
                )
            }
        }
    }

    /**
     * 设置监听器
     */
    fun setShopCartListener(_shopCartListener: ShopCartListener) {
        this.shopCartListener = _shopCartListener
    }


}