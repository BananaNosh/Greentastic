package com.nobodysapps.greentastic.application

import android.app.Application
import com.nobodysapps.greentastic.dependencyInjection.ApplicationComponent
import com.nobodysapps.greentastic.dependencyInjection.DaggerApplicationComponent

open class GreentasticApplication: Application() {
    val appComponent: ApplicationComponent by lazy {
        initializeComponent()
    }

    open fun initializeComponent() = DaggerApplicationComponent.factory().create(this)
}