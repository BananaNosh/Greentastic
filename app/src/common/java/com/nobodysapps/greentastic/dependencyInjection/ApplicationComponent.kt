package com.nobodysapps.greentastic.dependencyInjection

import android.app.Application
import com.nobodysapps.greentastic.networking.ApiServiceModule
import com.nobodysapps.greentastic.storage.DatabaseModule
import com.nobodysapps.greentastic.ui.search.SearchFragment
import com.nobodysapps.greentastic.ui.transport.TransportFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ContextModule::class, ViewModelModule::class, ApiServiceModule::class, DatabaseModule::class])
interface ApplicationComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Application): ApplicationComponent
    }

    fun inject(fragment: SearchFragment)
    fun inject(fragment: TransportFragment)

}