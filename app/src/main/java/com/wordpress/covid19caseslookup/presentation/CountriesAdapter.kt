package com.wordpress.covid19caseslookup.presentation

import android.animation.ObjectAnimator
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wordpress.covid19caseslookup.R

class CountriesAdapter(private val countries: List<String>, private val listener: ClickListener): RecyclerView.Adapter<CountriesAdapter.ViewHolder>() {
    class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)
    private var runAnimation = false
    private var positionToAnimate: Int? = null
    private var animatedHolder: ViewHolder? = null
    private var objectAnimator: ObjectAnimator? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.country_item, parent, false) as TextView)
    }

    override fun getItemCount() = countries.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val textView = holder.textView
        textView.setOnClickListener { listener.onItemClick(position) }
        textView.text = countries[position]
        if (runAnimation) {
            runAnimation = false
            animatedHolder = holder
            objectAnimator = ObjectAnimator.ofArgb(textView, "backgroundColor", Color.WHITE, Color.MAGENTA, Color.WHITE, Color.GRAY, Color.WHITE).apply {
                duration = 3000
                start()
            }
        } else if (animatedHolder == holder && position != positionToAnimate && objectAnimator?.isRunning == true) {
            objectAnimator!!.end()
            animatedHolder = null
            positionToAnimate = null
        }
    }

    fun animateItem(position: Int) {
        runAnimation = true
        positionToAnimate = position
        notifyItemChanged(position)
    }

    interface ClickListener {
        fun onItemClick(position: Int)
    }
}