package com.nobodysapps.greentastic.dependencyInjection

import com.nobodysapps.greentastic.networking.FakeApiServiceModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [FakeApiServiceModule::class])
interface TestAppComponent: ApplicationComponent