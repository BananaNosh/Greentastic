package com.nobodysapps.greentastic.dependencyInjection

import android.content.Context
import com.nobodysapps.greentastic.networking.ApiServiceModule
import com.nobodysapps.greentastic.ui.search.SearchFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ViewModelModule::class, ApiServiceModule::class])
interface ApplicationComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): ApplicationComponent
    }

    fun inject(fragment: SearchFragment)

}