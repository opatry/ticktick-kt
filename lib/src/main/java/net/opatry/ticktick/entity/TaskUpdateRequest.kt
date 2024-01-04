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
 * @property projectId Project id.
 * @property id Task id.
 * @property title Task title
 * @property content Task content
 * @property desc Description of checklist
 * @property isAllDay All day
 * @property startDate Start date and time in `"yyyy-MM-dd'T'HH:mm:ssZ"` format **Example:** `"2019-11-13T03:00:00+0000"`
 * @property dueDate Due date and time in `"yyyy-MM-dd'T'HH:mm:ssZ"` format **Example:** `"2019-11-13T03:00:00+0000"`
 * @property timeZone The time zone in which the time is specified
 * @property reminders Lists of reminders specific to the task
 * @property repeatFlag Recurring rules of task
 * @property priority The priority of task, default is "0"
 * @property sortOrder The order of task
 * @property items The list of subtasks
 */
data class TaskUpdateRequest(

    @SerializedName("projectId")
    val projectId: String,

    @SerializedName("id")
    val id: String,

    @SerializedName("title")
    val title: String? = null,

    @SerializedName("content")
    val content: String? = null,

    @SerializedName("desc")
    val desc: String? = null,

    @SerializedName("isAllDay")
    val isAllDay: Boolean? = null,

    @SerializedName("startDate")
    val startDate: String? = null,

    @SerializedName("dueDate")
    val dueDate: String? = null,

    @SerializedName("timeZone")
    val timeZone: String? = null,

    @SerializedName("reminders")
    val reminders: List<String>? = null,

    @SerializedName("repeatFlag")
    val repeatFlag: String? = null,

    @SerializedName("priority")
    val priority: Task.Priority? = null,

    @SerializedName("sortOrder")
    val sortOrder: Long? = null,

    @SerializedName("items")
    val items: List<ChecklistItemEdit>? = null,
)
