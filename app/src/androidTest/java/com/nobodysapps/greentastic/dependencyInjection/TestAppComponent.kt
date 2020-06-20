package com.nobodysapps.greentastic.dependencyInjection

import com.nobodysapps.greentastic.networking.FakeApiServiceModule
import com.nobodysapps.greentastic.storage.DatabaseModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ContextModule::class, FakeApiServiceModule::class, ViewModelModule::class, DatabaseModule::class])
interface TestAppComponent: ApplicationComponent