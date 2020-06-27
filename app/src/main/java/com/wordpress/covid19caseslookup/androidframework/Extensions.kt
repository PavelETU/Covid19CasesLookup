package com.wordpress.covid19caseslookup.androidframework

import android.view.View


fun View.visible(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}