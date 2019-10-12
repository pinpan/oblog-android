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

package com.applego.oblog.tppwatch

import android.app.Application
import com.applego.oblog.tppwatch.data.source.TppsRepository
import timber.log.Timber
import timber.log.Timber.DebugTree
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.room.Room
import androidx.core.content.ContextCompat.getSystemService
import com.applego.oblog.tppwatch.data.source.local.TppDatabase


/**
 * An application that lazily provides a repository. Note that this Service Locator pattern is
 * used to simplify the sample. Consider a Dependency Injection framework.
 *
 * Also, sets up Timber in the DEBUG BuildConfig. Read Timber's documentation for production setups.
 */
class TppWatchApplication : Application() {

    // Depends on the flavor,
    val tppRepository: TppsRepository
        get() = ServiceLocator.provideTppsRepository(this)

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(DebugTree())

        /*var database = Room.databaseBuilder(context.getApplicationContext(),
                TppDatabase::class.java!!, "Sample.db")
                .fallbackToDestructiveMigration()
                .build()*/
    }
}
