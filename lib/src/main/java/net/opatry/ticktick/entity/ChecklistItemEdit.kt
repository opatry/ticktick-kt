/*
 * Copyright (c) 2024 Olivier Patry
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 * OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.opatry.ticktick.entity

import com.google.gson.annotations.SerializedName


/**
 * @property title Subtask title
 * @property status The completion status of subtask
 * @property completedTime Completed time in `"yyyy-MM-dd'T'HH:mm:ssZ"` format **Example:** `"2019-11-13T03:00:00+0000"`
 * @property isAllDay All day
 * @property sortOrder The order of subtask
 * @property startDate Start date and time in `"yyyy-MM-dd'T'HH:mm:ssZ"` format
 * @property timeZone The time zone in which the Start time is specified
 */
data class ChecklistItemEdit(

    @SerializedName("title")
    val title: String? = null,

    @SerializedName("status")
    val status: ChecklistItem.Status? = null,

    @SerializedName("completedTime")
    val completedTime: String? = null,

    @SerializedName("isAllDay")
    val isAllDay: Boolean? = null,

    @SerializedName("sortOrder")
    val sortOrder: Long? = null,

    @SerializedName("startDate")
    val startDate: String? = null,

    @SerializedName("timeZone")
    val timeZone: String? = null,
)
