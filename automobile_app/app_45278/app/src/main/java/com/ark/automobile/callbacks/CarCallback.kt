package com.ark.automobile.callbacks

import android.view.View
import com.ark.automobile.models.Car

interface CarCallback {

    fun onClick(v: View, car: Car)

}