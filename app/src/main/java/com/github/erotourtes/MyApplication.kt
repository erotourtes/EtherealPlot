package com.github.erotourtes

import android.app.Application
import com.github.erotourtes.data.AppContainer
import com.github.erotourtes.data.AppContainerImpl

class EtherealPlotApplication : Application() {

    // AppContainer is a dependency container that holds single instances of repositories, daos, etc.
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainerImpl(this)
    }
}