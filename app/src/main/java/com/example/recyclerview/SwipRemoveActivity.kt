package com.example.recyclerview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import android.util.Log
import java.util.*
import kotlin.collections.ArrayList
import android.R.attr.left
import android.graphics.*
import android.graphics.Paint.Align
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import com.example.recyclerview.SwipRemoveActivity.UnderlayButton
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.util.DisplayMetrics
import android.util.TypedValue


import kotlin.collections.HashMap


class SwipRemoveActivity : AppCompatActivity() {
    val TAG = "MainActivity"
    val recyclerView by lazy {
        findViewById<RecyclerView>(R.id.recyclerView)
    }
    val layoutManager : LayoutManager by lazy {
        LinearLayoutManager(this)
    }

    val itemTouchHelper by lazy {
        ItemTouchHelper(object : SwipeHelper(this, recyclerView) {
            override fun instantiateUnderlayButton(
                viewHolder: RecyclerView.ViewHolder?,
                underlayButtons: MutableList<UnderlayButton>?
            ) {
                underlayButtons?.add(UnderlayButton("Delete", 0, Color.parseColor("#FF3C30")){

                })
                underlayButtons?.add(UnderlayButton("Transfer", 0, Color.parseColor("#FF9502")){

                })
                underlayButtons?.add(UnderlayButton("Unshare", 0, Color.parseColor("#C7C7CB")){

                })
            }

        })

    }

    val myAdapter = MyAdapter()

    var swapPos = -1


    val buttonsBuffer = HashMap<Int, List<UnderlayButton>>()

    val colorList = mutableListOf<Int>(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = myAdapter
        myAdapter.setData(getData())
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }




    fun getData(): List<String> {
        val list = ArrayList<String>()
        for (i in 0 until  100) {
            list.add("" + i)
        }
        return list
    }

    inner class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val textView by lazy {
            itemView.findViewById<TextView>(R.id.text_name)
        }

    }

    inner class MyAdapter : RecyclerView.Adapter<MyViewHolder>(), IItemHelper {
        override fun itemMoved(oldPosition: Int, newPosition: Int) {
            Collections.swap(data, oldPosition, newPosition)
            notifyItemMoved(oldPosition, newPosition)
        }

        override fun itemDismiss(position: Int) {
            data.removeAt(position)
            notifyItemRemoved(position)
        }

        val data = ArrayList<String>()
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_items, parent, false))
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.textView.text = data[position]
            holder.textView.setBackgroundResource(colorList[position % 3])
//            holder.textView.setOnTouchListener { view, motionEvent ->
//                itemTouchHelper.startDrag(holder)
//                itemTouchHelper.startSwipe(holder)
//                return@setOnTouchListener true
//            }
        }

        fun setData(list : List<String>) {
            data.clear()
            data.addAll(list)
            notifyDataSetChanged()
        }

    }

    private fun drawButtons(
        c: Canvas,
        itemView: View,
        buffer: List<UnderlayButton>,
        pos: Int,
        dX: Float
    ) {
        var right = itemView.right.toFloat()
        val dButtonWidth = -1 * dX / buffer.size

        for (button in buffer) {
            val left = right - dButtonWidth
            button.onDraw(
                c,
                RectF(
                    left,
                    itemView.top.toFloat(),
                    right,
                    itemView.bottom.toFloat()
                ),
                pos
            )

            right = left
        }
    }

    inner class UnderlayButton(
        private val text: String,
        private val imageResId: Int,
        private val color: Int,
        private val clickListener: UnderlayButtonClickListener
    ) {
        private var pos: Int = 0
        private var clickRegion: RectF? = null

        fun onClick(x: Float, y: Float): Boolean {
            if (clickRegion != null && clickRegion!!.contains(x, y)) {
                clickListener.onClick(pos)
                return true
            }

            return false
        }

        fun onDraw(c: Canvas, rect: RectF, pos: Int) {
            val p = Paint()

            // Draw background
            p.setColor(color)
            c.drawRect(rect, p)

            // Draw Text
            p.setColor(Color.WHITE)
            p.setTextSize(24f)

            val r = Rect()
            val cHeight = rect.height()
            val cWidth = rect.width()
            p.setTextAlign(Paint.Align.LEFT)
            p.getTextBounds(text, 0, text.length, r)
            val x = cWidth / 2f - r.width() / 2f - r.left
            val y = cHeight / 2f + r.height() / 2f - r.bottom
            c.drawText(text, rect.left + x, rect.top + y, p)

            clickRegion = rect
            this.pos = pos
        }
    }

    interface UnderlayButtonClickListener {
        fun onClick(pos: Int)
    }

    interface IItemHelper {

        fun itemMoved(oldPosition: Int, newPosition: Int)
        fun itemDismiss(position: Int)
    }
}
