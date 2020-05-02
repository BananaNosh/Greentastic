package com.nobodysapps.greentastic.dependencyInjection

import com.nobodysapps.greentastic.networking.TestApiServiceModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [TestApiServiceModule::class])
interface TestAppComponent: ApplicationComponent