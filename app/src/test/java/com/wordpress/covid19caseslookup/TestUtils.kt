package com.wordpress.covid19caseslookup

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

fun <T> LiveData<T>.waitForValueToSet(times: Int = 1) {
    val latch = CountDownLatch(times)
    val observer = object : Observer<T> {
        override fun onChanged(t: T) {
            latch.countDown()
            this@waitForValueToSet.removeObserver(this)
        }
    }
    observeForever(observer)
    latch.await(2, TimeUnit.SECONDS)
}