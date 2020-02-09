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

package com.applego.oblog.tppwatch.data.source

import com.applego.oblog.tppwatch.data.Result
import com.applego.oblog.tppwatch.data.TppsFilter
import com.applego.oblog.tppwatch.data.source.local.Tpp

/**
 * Interface to the data layer.
 */
interface TppsRepository {

    suspend fun getTpps(forceUpdate: Boolean = false): Result<List<Tpp>>

    suspend fun getTpps(forceUpdate: Boolean, filter: TppsFilter): Result<List<Tpp>>

    suspend fun getTpp(tppId: String, forceUpdate: Boolean = false): Result<Tpp>

    suspend fun saveTpp(tpp: Tpp)

    suspend fun setTppFollowedFlag(tppId: String, followed: Boolean)

    suspend fun setTppFollowedFlag(tpp: Tpp, followed: Boolean)

    //suspend fun unfollowTpp(tpp: Tpp)

    suspend fun setTppActivateFlag(tppId: String, active: Boolean)

    suspend fun setTppActivateFlag(tpp: Tpp, active: Boolean)

    //suspend fun deactivateTpp(tpp: Tpp)

    //suspend fun clearFollowedTpps()

    suspend fun deleteAllTpps()

    suspend fun deleteTpp(tppId: String)
}
