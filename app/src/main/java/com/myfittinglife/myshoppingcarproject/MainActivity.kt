package com.myfittinglife.myshoppingcarproject

import android.animation.Animator
import android.animation.ValueAnimator
import android.graphics.Path
import android.graphics.PathMeasure
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.myfittinglife.myshoppingcarproject.adapter.LeftTypeAdapter
import com.myfittinglife.myshoppingcarproject.adapter.RightProductAdapter
import com.myfittinglife.myshoppingcarproject.entities.Product
import com.myfittinglife.myshoppingcarproject.entities.ProductItem
import com.myfittinglife.myshoppingcarproject.entities.ShopCart
import com.myfittinglife.myshoppingcarproject.imp.ShopCartListener
import kotlinx.android.synthetic.main.activity_main.*

/**
@Author LD
@Time 2020/12/8 10:57
@Describe 自己写的购物车的逻辑，使用itemDecoration实现悬停
@Modify
 */
class MainActivity : AppCompatActivity(), LeftTypeAdapter.LeftTypeClickListener, ShopCartListener {

    companion object {
        const val TAG = "ceshi"
    }

    var data = mutableListOf<ProductItem>()
    //记录右侧列表的所有数据
    private var productData = mutableListOf<Product>()
    lateinit var leftAdapter: LeftTypeAdapter
    private lateinit var rightAdapter: RightProductAdapter

    //区分是左侧点击导致的右侧滑动，还是右侧主动的滑动
    var leftClickType = false

    //购物车实体类
    private var shopCart = ShopCart()

    //计算path路径中点的坐标
   private var mPathMeasure: PathMeasure? = null

    /**
     * 贝塞尔曲线中间过程的点的坐标
     */
    private val mCurrentPosition = FloatArray(2)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initData()
        leftAdapter = LeftTypeAdapter(data)
        rightAdapter = RightProductAdapter(productData, shopCart)
        getAllRightData()
        left_menu.layoutManager = LinearLayoutManager(this)
        left_menu.adapter = leftAdapter
        leftAdapter.setLeftTypeClickListener(this)

        right_menu.layoutManager = LinearLayoutManager(this)

        var productList2 = setPullAction()
        //绘制头部
        right_menu.addItemDecoration(MySectionDecoration(this,productList2))

        //购物车按钮
        rl_bottom_shopping_cart.setOnClickListener {
            //TODO(可以做弹框显示已选的物品)
            Log.i(TAG, "onCreate: 总价格：${shopCart.totalPrice}" + "总数量：" + shopCart.totalAmount)
        }
        //结算按钮
        btn_shopping_cart_pay.setOnClickListener {
            Toast.makeText(this, "总共价格为：${shopCart.totalPrice}", Toast.LENGTH_SHORT).show()
        }


        right_menu.adapter = rightAdapter
        //取消掉动画解决更新时闪烁问题
        (right_menu.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false

        //右侧菜单滑动联动左侧菜单选择
        right_menu.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                //当无法上滑时，即右侧列表滑动到底部时，移动左侧列表到最后一项,将左侧的最后一项Item变为选中状态
                if (!recyclerView.canScrollVertically(1)) {
                    leftAdapter.setSelectedNum(data.size - 1)

                }else{
                    //当右侧主动滑动时，才联动左侧列表；左侧列表带动的右侧列表滑动时，不再返回来联动左侧列表
                    if (leftClickType) {
                        leftClickType = false
                    } else {
                        //当右侧列表滑动时，获取右侧列表的第一条数据，找其所对应的左侧列表的Title对象即：productItem
                        var position =
                            (right_menu.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

                        var productItem = getTitleByPosition(position, data)
                        //找到该productItem在左侧列表对应的position
                        for (i in data.indices) {
                            if (data[i] === productItem) {
                                //左侧列表适配器设置选中项并进行更新
                                leftAdapter.setSelectedNum(i)
                                break
                            }
                        }
                    }
                }
            }
        })

        //设置这个监听器目的是为了改变左边栏的数字
        rightAdapter.setShopCartListener(this)

    }

    /**
     * 初始化数据
     */
    private fun initData() {

        var productItem1List = mutableListOf<Product>()
        var productItem1 = ProductItem(productItem1List, "1", "零食")
        productItem1List.add(Product("1", "1", R.drawable.ic1, 3.5, 21, "威龙大面筋65g"))
        productItem1List.add(Product("1", "2", R.drawable.ic2, 5.9, 22, "恰恰焦糖化子108g"))
        productItem1List.add(Product("1", "3", R.drawable.ic3, 4.9, 23, "浪味仙"))
        productItem1List.add(Product("1", "4", R.drawable.ic4, 3.8, 24, "旺旺小小酥"))
        productItem1List.add(Product("1", "5", R.drawable.ic5, 3.5, 25, "上好佳鲜虾条"))

        var productItem2List = mutableListOf<Product>()
        var productItem2 = ProductItem(productItem2List, "2", "新鲜水果")
        productItem2List.add(Product("2", "6", R.drawable.ic6, 39.9, 30, "盒装草莓约250g"))
        productItem2List.add(Product("2", "7", R.drawable.ic7, 4.98, 31, "砂糖桔500g"))
        productItem2List.add(Product("2", "8", R.drawable.ic8, 9.8, 32, "进口橙500g"))
        productItem2List.add(Product("2", "9", R.drawable.ic9, 29.8, 33, "西柚1kg"))
        productItem2List.add(Product("2", "10", R.drawable.ic10, 60.0, 34, "雪花梨2粒约650g"))

        var productItem3List = mutableListOf<Product>()
        var productItem3 = ProductItem(productItem3List, "3", "蛋肉家禽")
        productItem3List.add(Product("3", "11", R.drawable.ic11, 14.9, 40, "带皮五花肉250g"))
        productItem3List.add(Product("3", "12", R.drawable.ic12, 21.8, 41, "启心澳洲和牛丸120g"))
        productItem3List.add(Product("3", "13", R.drawable.ic13, 23.9, 42, "味从鲜精品肥牛300g/袋"))
        productItem3List.add(Product("3", "14", R.drawable.ic14, 23.8, 43, "新西兰眼肉牛排170g"))
        productItem3List.add(Product("3", "15", R.drawable.ic15, 23.8, 44, "潮香村至尊牛排(儿童牛排)90g"))

        var productItem4List = mutableListOf<Product>()
        var productItem4 = ProductItem(productItem4List, "4", "酒水饮料")
        productItem4List.add(Product("4", "16", R.drawable.ic16, 5.0, 50, "昆仑山雪山矿泉水350ml"))
        productItem4List.add(Product("4", "17", R.drawable.ic17, 3.3, 51, "名仁6个柠檬375ml"))
        productItem4List.add(Product("4", "18", R.drawable.ic18, 4.0, 52, "怡泉桃汁苏打水400ml"))
        productItem4List.add(Product("4", "19", R.drawable.ic19, 2.9, 53, "可口可乐500ml"))
        productItem4List.add(Product("4", "20", R.drawable.ic20, 5.3, 54, "雪碧柠檬味汽水2L"))

        var productItem5List = mutableListOf<Product>()
        var productItem5 = ProductItem(productItem5List, "5", "火锅节")
        productItem5List.add(Product("5", "21", R.drawable.ic21, 9.8, 60, "八则牛肚板丝150g"))
        productItem5List.add(Product("5", "22", R.drawable.ic22, 22.8, 61, "梅林午餐肉340g"))
        productItem5List.add(Product("5", "23", R.drawable.ic23, 35.8, 62, "恒都精选羊肉卷约500g"))
        productItem5List.add(Product("5", "24", R.drawable.ic24, 14.32, 63, "双汇午餐火腿500g"))
        productItem5List.add(Product("5", "25", R.drawable.ic25, 19.9, 64, "福润生牛百叶120g"))

        data.add(productItem1)
        data.add(productItem2)
        data.add(productItem3)
        data.add(productItem4)
        data.add(productItem5)


    }

    /**
     * 左侧列表的点击事件，来联动滑动右侧布局
     */
    override fun onItemClick(position: Int) {
        Log.i(TAG, "onItemClick: 点击的位置为:$position")
        var sum = 0
        for (i in 0 until position) {
            sum += data[i].productList.size
        }
        var layoutManager = right_menu.layoutManager as LinearLayoutManager

        //将不在屏幕上的item移动到屏幕上
        //将该item移动到第一项
        layoutManager.scrollToPositionWithOffset(sum, 0)
        Log.i(TAG, "onItemClick: 左侧点击右侧联动")

        leftClickType = true
    }

    /**
     * 减少产品数量
     */
    override fun onReduce(parentId: String, parentCount: Int) {
        super.onReduce(parentId, parentCount)
        //更新左侧列表类别数量
        leftAdapter.updateTypeCount(parentId, parentCount)

        //更新购物车的数量
        if (shopCart.totalAmount > 0) {
            tv_shopping_cart_count.visibility = View.VISIBLE
            tv_shopping_cart_count.text = shopCart.totalAmount.toString()
        } else {
            tv_shopping_cart_count.visibility = View.INVISIBLE
        }

        //更新购物车的价格
        tv_shopping_cart_money.text = shopCart.totalPrice.toString()
    }



    /**
     * 增加产品数量
     */
    override fun onAdd(view: View, parentId: String, parentCount: Int) {
        super.onAdd(view, parentId, parentCount)
        //更新左侧列表类别数量
        leftAdapter.updateTypeCount(parentId, parentCount)

        //更新购物车的数量
        if (shopCart.totalAmount > 0) {
            tv_shopping_cart_count.visibility = View.VISIBLE
            tv_shopping_cart_count.text = shopCart.totalAmount.toString()
        } else {
            tv_shopping_cart_count.visibility = View.INVISIBLE
        }

        //更新购物车的价格
        tv_shopping_cart_money.text = shopCart.totalPrice.toString()

        //添加的动画效果
        addAnimation(view)
    }



    //将所有项的名字放进来
    private fun setPullAction(): MutableList<String> {
        var titleList = mutableListOf<String>()
        for (i in data.indices) {
            for (j in data[i].productList.indices) {
                titleList.add(data[i].typeName)
            }
        }
        return titleList
    }

    /**
     * 动画
     */
    private fun addAnimation(view: View) {

//   一、创造出执行动画的主题---imageview
        //代码new一个imageview，图片资源是上面的imageview的图片
        // (这个图片就是执行动画的图片，从开始位置出发，经过一个抛物线（贝塞尔曲线），移动到购物车里)
        val goods = ImageView(this@MainActivity)
        goods.setImageDrawable(
            resources.getDrawable(
                R.drawable.shape_shopping_cart_num_bg,
                null
            )
        )
        val params = RelativeLayout.LayoutParams(50, 50)
        rl.addView(goods, params)

//    二、计算动画开始/结束点的坐标的准备工作
        //得到父布局的起始点坐标（用于辅助计算动画开始/结束时的点的坐标）
        val parentLocation = IntArray(2)
        rl.getLocationInWindow(parentLocation)

        //得到商品图片的坐标（用于计算动画开始的坐标）
        val startLoc = IntArray(2)
        view.getLocationInWindow(startLoc)

        //得到购物车图片的坐标(用于计算动画结束后的坐标)
        val endLoc = IntArray(2)
        iv_shopping_cart_img.getLocationInWindow(endLoc)

//    三、正式开始计算动画开始/结束的坐标
        //开始掉落的商品的起始点：商品起始点-父布局起始点+该商品图片的一半
        val startX =
            startLoc[0] - parentLocation[0] + goods.width / 2.toFloat()
        val startY =
            startLoc[1] - parentLocation[1] + goods.height / 2.toFloat()

        //商品掉落后的终点坐标：购物车起始点-父布局起始点+购物车图片的1/5
        val toX =
            endLoc[0] - parentLocation[0] + iv_shopping_cart_img.width / 5.toFloat()
        val toY = endLoc[1] - parentLocation[1].toFloat()

        //四、计算中间动画的插值坐标（贝塞尔曲线）（其实就是用贝塞尔曲线来完成起终点的过程）
        //开始绘制贝塞尔曲线
        val path = Path()
        //移动到起始点（贝塞尔曲线的起点）
        path.moveTo(startX, startY)
        //使用二次萨贝尔曲线：注意第一个起始坐标越大，贝塞尔曲线的横向距离就会越大，一般按照下面的式子取即可
        //想实现类似抛物线的形式，这里的控制点取的是(startX+toX)/2,startY
        path.quadTo((startX + toX) / 2, startY, toX, toY)
        //mPathMeasure用来计算贝塞尔曲线的曲线长度和贝塞尔曲线中间插值的坐标，
        // 如果是true，path会形成一个闭环,Path用来计算path路径中的点的坐标
        mPathMeasure = PathMeasure(path, false)

        //★★★属性动画实现（从0到贝塞尔曲线的长度之间进行插值计算，获取中间过程的距离值）
        val valueAnimator = ValueAnimator.ofFloat(0f, mPathMeasure!!.length)
        valueAnimator.duration = 500
        // 匀速线性插值器
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.addUpdateListener { animation -> // 当插值计算进行时，获取中间的每个值，
            // 这里这个值是中间过程中的曲线长度（下面根据这个值来得出中间点的坐标值）
            val value = animation.animatedValue as Float
            // ★★★★★获取当前点坐标封装到mCurrentPosition
            // boolean getPosTan(float distance, float[] pos, float[] tan) ：
            // 传入一个距离distance(0<=distance<=getLength())，然后会计算当前距
            // 离的坐标点和切线，pos会自动填充上坐标，这个方法很重要。
            mPathMeasure!!.getPosTan(
                value,
                mCurrentPosition,
                null
            ) //mCurrentPosition此时就是中间距离点的坐标值
            // 移动的商品图片（动画图片）的坐标设置为该中间点的坐标
            goods.translationX = mCurrentPosition[0]
            goods.translationY = mCurrentPosition[1]
        }
        //   五、 开始执行动画
        valueAnimator.start()
        //   六、动画结束后的处理
        valueAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}

            //当动画结束后：
            override fun onAnimationEnd(animation: Animator) {
                // 把移动的图片imageview从父布局里移除
                rl.removeView(goods)
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
    }


    /**
     * 右侧数据整理
     */
    private fun getAllRightData() {
        for (i in data.indices) {
            productData.addAll(data[i].productList)
        }
    }

    /**
     * 根据position位置来获取相应的标题Bean !!!(用于滑动右侧时联动左侧)
     */
    fun getTitleByPosition(_position: Int, dataList: MutableList<ProductItem>): ProductItem? {
        var sum = 0
        for (productItem in dataList) {
            sum += productItem.productList.size
            if (_position < sum) {
                return productItem
            }
        }
        return null
    }
}