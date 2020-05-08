package com.wordpress.covid19caseslookup.data.entities

import com.google.gson.annotations.SerializedName

data class Country(@SerializedName("Country") val country: String,
                   @SerializedName("Slug") val slug: String,
                   @SerializedName("ISO2") val iso: String)