package com.filex.studio

import android.app.Application

class FileXApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        container.shizukuController.start()
    }
}
