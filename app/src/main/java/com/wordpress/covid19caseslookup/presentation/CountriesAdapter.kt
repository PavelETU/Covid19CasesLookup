package com.wordpress.covid19caseslookup.presentation

import android.animation.ObjectAnimator
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wordpress.covid19caseslookup.R

private const val DEFAULT_ITEM = 1
private const val ANIMATED_ITEM = 2

class CountriesAdapter(private val listener: ClickListener) :
    ListAdapter<String, CountriesAdapter.ViewHolder>(object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean =
            oldItem == newItem
    }) {
    class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

    private var runAnimation = false
    private var positionToAnimate: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.country_item, parent, false) as TextView
        )
    }

    override fun getItemViewType(position: Int) =
        if (runAnimation && position == positionToAnimate) ANIMATED_ITEM
        else DEFAULT_ITEM


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val textView = holder.textView
        textView.setOnClickListener { listener.onItemClick(holder.adapterPosition) }
        textView.text = getItem(position)
        if (holder.itemViewType == ANIMATED_ITEM) {
            runAnimation = false
            ObjectAnimator.ofArgb(
                textView,
                "backgroundColor",
                Color.WHITE,
                Color.MAGENTA,
                Color.WHITE,
                Color.GRAY,
                Color.WHITE
            ).apply {
                duration = 3000
                start()
            }
        }
    }

    fun animateItem(position: Int) {
        runAnimation = true
        positionToAnimate = position
    }

    interface ClickListener {
        fun onItemClick(position: Int)
    }
}