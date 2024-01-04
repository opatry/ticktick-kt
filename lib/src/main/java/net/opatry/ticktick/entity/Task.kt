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
import net.opatry.ticktick.entity.Task.Priority
import net.opatry.ticktick.entity.Task.Priority.High
import net.opatry.ticktick.entity.Task.Priority.Low
import net.opatry.ticktick.entity.Task.Priority.Medium
import net.opatry.ticktick.entity.Task.Priority.None
import net.opatry.ticktick.entity.Task.Status
import net.opatry.ticktick.entity.Task.Status.Completed
import net.opatry.ticktick.entity.Task.Status.Normal

/**
 * @property id Task identifier
 * @property projectId Task project id
 * @property title Task title
 * @property isAllDay All day
 * @property completedTime Task completed time in `"yyyy-MM-dd'T'HH:mm:ssZ"` **Example:** `"2019-11-13T03:00:00+0000"`
 * @property content Task content
 * @property desc Task description of checklist
 * @property dueDate Task due date time in `"yyyy-MM-dd'T'HH:mm:ssZ"` **Example:** `"2019-11-13T03:00:00+0000"`
 * @property items Subtasks of Task
 * @property priority Task priority **Value:** [Priority.None], [Priority.Low], [Priority.Medium], [Priority.High]
 * @property reminders List of reminder triggers **Example:** `[ "TRIGGER:P0DT9H0M0S", "TRIGGER:PT0S" ]`
 * @property repeatFlag Recurring rules of task **Example:** `"RRULE:FREQ=DAILY;INTERVAL=1"`
 * @property sortOrder Task sort order **Example:** `12345`
 * @property startDate Start date time in `"yyyy-MM-dd'T'HH:mm:ssZ"` **Example:** `"2019-11-13T03:00:00+0000"`
 * @property status Task completion status **Value:** [Status.Normal], [Status.Completed]
 * @property timeZone Task timezone **Example:** `"America/Los_Angeles"`
 */
data class Task(

    @SerializedName("id")
    val id: String,

    @SerializedName("projectId")
    val projectId: String,

    @SerializedName("title")
    val title: String,

    @SerializedName("isAllDay")
    val isAllDay: Boolean,

    @SerializedName("completedTime")
    val completedTime: String? = null,

    @SerializedName("content")
    val content: String,

    @SerializedName("desc")
    val desc: String,

    @SerializedName("dueDate")
    val dueDate: String? = null,

    @SerializedName("items")
    val items: List<ChecklistItem>? = null,

    @SerializedName("priority")
    val priority: Priority,

    @SerializedName("reminders")
    val reminders: List<String>? = null,

    @SerializedName("repeatFlag")
    val repeatFlag: String? = null,

    @SerializedName("sortOrder")
    val sortOrder: Long? = null,

    @SerializedName("startDate")
    val startDate: String,

    @SerializedName("status")
    val status: Status,

    @SerializedName("timeZone")
    val timeZone: String,

    // unofficial/undocumented
    @SerializedName("tags")
    val tags: List<String>? = null,

    // unofficial/undocumented
    @SerializedName("columnId")
    val columnId: String? = null,
) {
    /**
     * @property None 0
     * @property Low 1
     * @property Medium 3
     * @property High 5
     */
    enum class Priority(val value: Int) {

        @SerializedName("0")
        None(0),

        @SerializedName("1")
        Low(1),

        @SerializedName("3")
        Medium(3),

        @SerializedName("5")
        High(5),
    }

    /**
     * @property Normal 0
     * @property Completed 2
     */
    enum class Status(val value: Int) {

        @SerializedName("0")
        Normal(0),

        @SerializedName("2")
        Completed(2),
    }
}
