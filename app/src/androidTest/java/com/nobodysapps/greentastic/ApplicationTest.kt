/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nobodysapps.greentastic

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.nobodysapps.greentastic.activity.MainActivity
import com.nobodysapps.greentastic.util.waitUntilVisible
import org.hamcrest.Matchers.anything
import org.hamcrest.Matchers.endsWith
import org.junit.Test


class ApplicationTest {

    @Test
    fun runApp() {
        ActivityScenario.launch(MainActivity::class.java)

        // Should be in Registration/EnterDetails because the user is not registered
        onView(withId(R.id.sourceSearchView)).check(matches(isDisplayed()))
        onView(withId(R.id.destinationSearchView)).check(matches(isDisplayed()))

//        onView(withHint("current location")).check(matches(hasFocus()))
//        onView(withId(R.id.sourceSearchView)).perform(typeText("zurich"))
        onView(withHint("current location")).perform(typeText("zurich"), pressImeActionButton())
//        onData(allOf(`is`(instanceOf(String::class.java)),`is`("Zürich, Switzerland"))).perform(click())
        onView(withClassName(endsWith("PopupDecorView"))).waitUntilVisible(2000).check(matches(isDisplayed()))
//        onView(withText("Zürich, Switzerland")).waitUntilVisible(2000)
//        onView(withText("Zürich, Switzerland")).inRoot(isPlatform)
        onData(anything()).atPosition(0).perform(click())
//        onView(nthChildOf(anyOf(withId(android.R.id.title)), 1))
        Thread.sleep(2000)

//        onView(withId(R.id.username)).perform(typeText("username"), closeSoftKeyboard())
//        onView(withId(R.id.password)).perform(typeText("password"), closeSoftKeyboard())
//        onView(withId(R.id.next)).perform(click())
    }
}
