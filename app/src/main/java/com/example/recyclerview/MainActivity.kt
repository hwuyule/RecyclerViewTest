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


class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity"
    val recyclerView by lazy {
        findViewById<RecyclerView>(R.id.recyclerView)
    }
    val layoutManager : LayoutManager by lazy {
        LinearLayoutManager(this)
    }

    val itemTouchHelper by lazy {
        ItemTouchHelper(CallBack())
    }

    val myAdapter = MyAdapter()

    val colorList = mutableListOf<Int>(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = myAdapter
        myAdapter.setData(getData())
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    inner class CallBack : ItemTouchHelper.Callback() {
        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            Log.i(TAG, "getMovementFlags")
            val dragFlags =
                ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            val swipedFlags = ItemTouchHelper.START or ItemTouchHelper.END
            return makeMovementFlags(dragFlags, swipedFlags)

        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            Log.i(TAG, "onMove,old pos[${viewHolder.layoutPosition}], new pos[${target.layoutPosition}]")
            myAdapter.notifyItemMoved(viewHolder.layoutPosition, target.layoutPosition)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            Log.i(TAG, "onSwiped, pos[${viewHolder.layoutPosition} dismiss]")
            myAdapter.itemDismiss(viewHolder.layoutPosition)
        }

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
        }

        fun setData(list : List<String>) {
            data.clear()
            data.addAll(list)
            notifyDataSetChanged()
        }

    }

    interface IItemHelper {

        fun itemMoved(oldPosition: Int, newPosition: Int)
        fun itemDismiss(position: Int)
    }
}
