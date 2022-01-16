package com.ari_d.justeatit.Extensions

import android.content.Context
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.ari_d.justeatit.R

fun View.slideUp(context: Context, animTime: Long, startOffset: Long) {
    val slideUp = AnimationUtils.loadAnimation(context, R.anim.slide_up).apply {
        duration = animTime
        interpolator = FastOutSlowInInterpolator()
        this.startOffset = startOffset
    }
    startAnimation(slideUp)
}

fun slideUpViews(context : Context, vararg views: View, animTime: Long = 300L, delay: Long = 150L) {
    for (i in views.indices) {
        views[i].slideUp(context, animTime, delay * i)
    }
}

class MyBounceInterpolator(
    amplitude: Double,
    frequency: Double
) : Interpolator{
    private var mAmplitude = 1.0
    private var mFrequency = 10.0

    override fun getInterpolation(time: Float): Float {
       return (-1 * Math.pow(Math.E, -time / mAmplitude) *
               Math.cos(mFrequency * time) + 1).toFloat()
    }

    init {
        mAmplitude = amplitude
        mFrequency = frequency
    }
}