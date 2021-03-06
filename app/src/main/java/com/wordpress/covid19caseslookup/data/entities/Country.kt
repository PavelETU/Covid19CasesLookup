package com.wordpress.covid19caseslookup.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Country(@PrimaryKey(autoGenerate = true) val id: Int = 0,
                   @SerializedName("Country") val country: String,
                   @SerializedName("Slug") val slug: String,
                   @SerializedName("ISO2") val iso: String)