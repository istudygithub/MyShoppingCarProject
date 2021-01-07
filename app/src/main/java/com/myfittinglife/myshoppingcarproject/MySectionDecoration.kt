package com.myfittinglife.myshoppingcarproject

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Build
import android.text.TextPaint
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

/**
 * @Author LD
 * @Time 2020/12/10 9:47
 * @Describe
 * @Modify
 */
internal class MySectionDecoration(
    context: Context,
    dataList: List<String>
) : ItemDecoration() {
    private val titleDataList: List<String>
    //绘制标题文字的画笔
    private val textPaint: TextPaint
    //绘制背景颜色的画笔
    private val paint: Paint
    //想要的矩形的高度
    private val topHeight: Int
    //字体度量对象，记录我们要绘制字体的有关信息
    private var fontMetrics: Paint.FontMetrics

    init {
        val res = context.resources
        this.titleDataList = dataList
        //设置悬浮栏的画笔---paint
        paint = Paint()
        //设置绘制背景的画笔颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            paint.color = res.getColor(R.color.color_, null)
        } else {
            paint.color = res.getColor(R.color.color_)
        }

        //设置悬浮栏中文本的画笔
        textPaint = TextPaint()
        textPaint.isAntiAlias = true
        textPaint.textSize = DensityUtil.dip2px(context, 14f).toFloat()
        textPaint.color = Color.DKGRAY
        textPaint.textAlign = Paint.Align.LEFT
        fontMetrics = Paint.FontMetrics()
        //决定悬浮栏的高度等
        topHeight = res.getDimensionPixelSize(R.dimen.sectioned_top)

    }

    /**
     * 为我们的头部偏移指定的高度
     * @param outRect
     * @param view
     * @param parent
     * @param state
     */
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val pos = parent.getChildAdapterPosition(view)

        //只有是同一组的第一个才显示悬浮栏
        if (pos == 0 || isFirstInGroup(pos)) {
            //距离ItemView的上方偏移topHeight高度
            outRect.top = topHeight
        } else {
            outRect.top = 0
        }
    }

    /**
     * 绘制标题栏，注意这里的onDraw方法针对的是每个Item
     * @param c
     * @param parent
     * @param state
     */
    override fun onDraw(
        c: Canvas,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.onDraw(c, parent, state)
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight

        //获取总共的Item数量(测试发现是当前页面可以显示的item数量)
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val view = parent.getChildAt(i)
            //获取当前view在总共集合中的位置
            val position = parent.getChildAdapterPosition(view)
            //获取当前view的分类名
            val groupName = titleDataList[position]

            //获取textPaint中的字体信息  setTextSize要在它前面
            fontMetrics = textPaint.fontMetrics
            //当是第一个或者是每组中的第一个时才绘制
            if (position == 0 || isFirstInGroup(position)) {
                //往上移动就要减
                val top = view.top - topHeight.toFloat()
                val bottom = view.top.toFloat()
                //基线距离文字中心的距离
                val distance =
                    (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom
                //基线的位置
                val textBaseLine = (bottom + top) / 2 + distance
                //绘制悬浮栏
                c.drawRect(left.toFloat(), top, right.toFloat(), bottom, paint)
                //绘制文本
                c.drawText(groupName, left.toFloat(), textBaseLine, textPaint)
            }
        }
    }

    /**
     * 这个相当于一直在最上面绘制了一个项
     * @param c 13453182279
     * @param parent
     * @param state
     */
    override fun onDrawOver(
        c: Canvas,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.onDrawOver(c, parent, state)


        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight

        //当前布局的名字
        var groupTitleName = ""
        //因为我们只需要在当前界面可显示的第一条数据上方绘制，所以这个就getChildAt(0)
        val itemView = parent.getChildAt(0)
        val position = parent.getChildAdapterPosition(itemView)
        //获取该位置的标题名称
        groupTitleName = titleDataList[position].toUpperCase()

        //绘制悬浮标题栏在该布局
        //默认是指定的高度
        var viewTitleHeight = topHeight.toFloat()
        //获取当前itemView的底部
        val viewBottom = itemView.bottom

        //---------------这里实现了上升和下拉平滑的效果--------------------
        //下一个item的名字
        val nextGroupTitleName = titleDataList[position + 1]
        //下一个item的名字和顶部0位置的名字不同，说明要更换新的标题了（viewBottom<viewTitleHeight,
        //说明itemView向上移动，此时要绘制的标题的高度其实就是顶部下标为0的itemView的bottom的距离，因为recyclerview最上面是0坐标）
        if (nextGroupTitleName !== groupTitleName && viewBottom < viewTitleHeight) {
            //所以此时的高度，就为剩下的itemView距离0点的高度了，因为是动态变化的，所有就有了效果
            viewTitleHeight = viewBottom.toFloat()
        }

        //获取paint中的字体信息  settextSize要在他前面
        val fontMetrics = textPaint.fontMetrics
        //基线距离文字中心的距离
        val distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom
        //因为按道理一直在第一项，所以高应该为0，但是这么写相当于固定了头部，因为要有动画效果，所以可能变化，这里的top和bottom都要发生变化
        c.drawRect(
            left.toFloat(),
            viewTitleHeight - topHeight,
            right.toFloat(),
            viewTitleHeight,
            paint
        )

        //绘制文字
        c.drawText(
            groupTitleName,
            left.toFloat(),
            2 * viewTitleHeight - topHeight - distance,
            textPaint
        )
    }

    /**
     * 判断是不是组中的第一个位置
     *
     * @param pos
     * @return
     */
    private fun isFirstInGroup(pos: Int): Boolean {
        return if (pos == 0) {
            true
        } else {
            //当前位置所对应的类型名
            val typeName = titleDataList[pos]
            //前一个位置所对应的类型名
            val previewTypeName = titleDataList[pos - 1]
            //判断前一个字符串 与 当前字符串 是否相同，相同返回true，不同返回false
            previewTypeName != typeName
        }
    }

}