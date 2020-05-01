package com.nobodysapps.greentastic.dependencyInjection

import com.nobodysapps.greentastic.application.GreentasticApplication

class TestApplication: GreentasticApplication() {
    override fun initializeComponent(): ApplicationComponent {
        return DaggerTestAppComponent.create()
    }
}