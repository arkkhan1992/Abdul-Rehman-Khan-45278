package com.ark.automobile.callbacks

import android.view.View
import com.ark.automobile.models.Part

interface PartCallback {

    fun onClick(v: View, part: Part)

}