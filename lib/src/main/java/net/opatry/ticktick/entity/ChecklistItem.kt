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
import net.opatry.ticktick.entity.ChecklistItem.Status
import net.opatry.ticktick.entity.ChecklistItem.Status.Completed
import net.opatry.ticktick.entity.ChecklistItem.Status.Normal

/**
 * @property id Subtask identifier
 * @property title Subtask title
 * @property status The completion status of subtask **Value:** [Status.Normal], [Status.Completed]
 * @property completedTime Subtask completed time in `"yyyy-MM-dd'T'HH:mm:ssZ"` **Example:** `"2019-11-13T03:00:00+0000"`
 * @property isAllDay All day
 * @property sortOrder Subtask sort order **Example:** `234444`
 * @property startDate Subtask start date time in `"yyyy-MM-dd'T'HH:mm:ssZ"` **Example:** `"2019-11-13T03:00:00+0000"`
 * @property timeZone Subtask timezone **Example:** `"America/Los_Angeles"`
 */
data class ChecklistItem(

    @SerializedName("id")
    val id: String,

    @SerializedName("title")
    val title: String,

    @SerializedName("status")
    val status: Status,

    @SerializedName("completedTime")
    val completedTime: String? = null,

    @SerializedName("isAllDay")
    val isAllDay: Boolean,

    @SerializedName("sortOrder")
    val sortOrder: Long? = null,

    @SerializedName("startDate")
    val startDate: String? = null,

    @SerializedName("timeZone")
    val timeZone: String,
) {

    /**
     * @property Normal 0
     * @property Completed 1
     */
    enum class Status(val value: Int) {

        @SerializedName("0")
        Normal(0),

        @SerializedName("1")
        Completed(1),
    }
}
