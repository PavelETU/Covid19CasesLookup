package com.wordpress.covid19caseslookup.espressocustom

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

private class HasItemCount(private val count: Int): TypeSafeMatcher<View>() {
    override fun describeTo(description: Description) {
        description.appendText("amount of elements in the RecyclerView equals $count")
    }

    override fun matchesSafely(recyclerView: View): Boolean {
        if (recyclerView !is RecyclerView) throw RuntimeException("use hasItemView only for RecyclerView")
        return recyclerView.adapter?.itemCount == count
    }
}

fun hasItemCount(count: Int): Matcher<View> {
    return HasItemCount(count)
}