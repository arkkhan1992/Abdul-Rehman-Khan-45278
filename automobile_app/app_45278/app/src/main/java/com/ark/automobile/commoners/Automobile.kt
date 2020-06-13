package com.ark.automobile.commoners

import android.support.multidex.MultiDexApplication
import timber.log.Timber

class Automobile : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            return
//        }
//
//        LeakCanary.install(this)
    }
}