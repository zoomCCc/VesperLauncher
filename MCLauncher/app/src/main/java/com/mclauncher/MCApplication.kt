package com.mclauncher

import android.app.Application

class MCApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: MCApplication
            private set
    }
}
