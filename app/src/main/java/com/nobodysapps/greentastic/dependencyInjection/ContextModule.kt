package com.nobodysapps.greentastic.dependencyInjection

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides

@Module
object ContextModule {
    @JvmStatic
    @Provides
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }
}