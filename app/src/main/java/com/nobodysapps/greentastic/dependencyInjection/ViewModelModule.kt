package com.nobodysapps.greentastic.dependencyInjection

import android.app.Application
import androidx.lifecycle.ViewModel
import com.nobodysapps.greentastic.ui.ViewModelFactory
import com.nobodysapps.greentastic.repository.SearchApiRepository
import com.nobodysapps.greentastic.repository.TransportRepository
import com.nobodysapps.greentastic.ui.search.SearchViewModel
import com.nobodysapps.greentastic.ui.transport.TransportFragment
import com.nobodysapps.greentastic.ui.transport.TransportViewModel
import dagger.MapKey
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import javax.inject.Provider
import kotlin.reflect.KClass

@Module
class ViewModelModule {
    @Target(AnnotationTarget.FUNCTION)
    @kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
    @MapKey
    internal annotation class ViewModelKey(val value: KClass<out ViewModel>)

    @Provides
    fun provideViewModelFactory(providerMap: MutableMap<Class<out ViewModel>, Provider<ViewModel>>): ViewModelFactory {
        return ViewModelFactory(providerMap)
    }

    @Provides
    @IntoMap
    @ViewModelKey(SearchViewModel::class)
    fun provideSearchViewModel(repository: SearchApiRepository): ViewModel {
        return SearchViewModel(repository)
    }

    @Provides
    @IntoMap
    @ViewModelKey(TransportViewModel::class)
    fun provideTransportViewModel(repository: TransportRepository, application: Application): ViewModel {
        return TransportViewModel(repository, application)
    }
}