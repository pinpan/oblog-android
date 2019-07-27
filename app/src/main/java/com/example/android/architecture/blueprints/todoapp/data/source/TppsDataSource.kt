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
package com.example.android.architecture.blueprints.todoapp.data.source

import com.example.android.architecture.blueprints.todoapp.data.Result
import com.example.android.architecture.blueprints.todoapp.data.Tpp

/**
 * Main entry point for accessing tpps data.
 */
interface TppsDataSource {

    suspend fun getTpps(): Result<List<Tpp>>

    suspend fun getTpp(tppId: String): Result<Tpp>

    suspend fun saveTpp(tpp: Tpp)

    suspend fun completeTpp(tpp: Tpp)

    suspend fun completeTpp(tppId: String)

    suspend fun activateTpp(tpp: Tpp)

    suspend fun activateTpp(tppId: String)

    suspend fun clearCompletedTpps()

    suspend fun deleteAllTpps()

    suspend fun deleteTpp(tppId: String)
}
