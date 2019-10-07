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
package com.applego.oblog.tppwatch.data.source.remote

/**
 * Immutable model class for a Tpp. In order to compile with Room, we can't use @JvmOverloads to
 * generate multiple constructors.
 *
 * @param title       title of the tpp
 * @param description description of the tpp
 * @param isFollowed whether or not this tpp is followed
 * @param id          id of the tpp
 */
data class TppDto @JvmOverloads constructor(
        var title: String = "",
        var description: String = "",
        //var isFollowed: Boolean = false,
        val id: String = ""

) {
    val srcDirectory: String = ""
    val srcId : String = ""

    @Deprecated("TODO: Remove unused")
    val titleForList: String
        get() = if (title.isNotEmpty()) title else description

}
