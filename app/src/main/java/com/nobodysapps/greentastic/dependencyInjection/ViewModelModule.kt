package com.nobodysapps.greentastic.dependencyInjection

import androidx.lifecycle.ViewModel
import com.nobodysapps.greentastic.ui.ViewModelFactory
import com.nobodysapps.greentastic.ui.search.SearchApiRepository
import com.nobodysapps.greentastic.ui.search.SearchViewModel
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
}