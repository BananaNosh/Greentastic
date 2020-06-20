package com.nobodysapps.greentastic.dependencyInjection

import android.app.Application
import android.content.Context
import com.nobodysapps.greentastic.networking.FakeApiServiceModule
import com.nobodysapps.greentastic.storage.DatabaseModule
import com.nobodysapps.greentastic.ui.search.SearchFragment
import com.nobodysapps.greentastic.ui.transport.TransportFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Suppress("unused")
@Singleton
@Component(modules = [ContextModule::class, ViewModelModule::class, FakeApiServiceModule::class, DatabaseModule::class])
interface ApplicationComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Application): ApplicationComponent
    }

    fun inject(fragment: SearchFragment)
    fun inject(fragment: TransportFragment)

}