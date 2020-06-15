package com.nobodysapps.greentastic.utils

import android.content.res.ColorStateList
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.core.widget.ImageViewCompat


fun ImageView.setTint(@ColorInt color: Int) {
    ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(color))
}


@ColorInt
fun List<Int>.toRGB(): Int {
    return (255 shl 24) or
            (this[0] shl 16) or
            (this[1] shl 8) or
            this[2]
}