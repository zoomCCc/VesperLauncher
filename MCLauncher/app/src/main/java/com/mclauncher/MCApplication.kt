package com.mclauncher

import android.app.Application

class MCApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        @JvmStatic
        lateinit var instance: MCApplication
            private set
    }
}
