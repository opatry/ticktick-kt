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


package net.opatry.ticktick.entity.data

import net.opatry.ticktick.entity.ChecklistItem
import net.opatry.ticktick.entity.Task

val taskData = listOf(
    EntityTestParam.build(
        """{  
            "id" : "63b7bebb91c0a5474805fcd4",  
            "isAllDay" : true,  
            "projectId" : "6226ff9877acee87727f6bca",  
            "title" : "Task Title",  
            "content" : "Task Content",  
            "desc" : "Task Description",  
            "timeZone" : "America/Los_Angeles",  
            "repeatFlag" : "RRULE:FREQ=DAILY;INTERVAL=1",  
            "startDate" : "2019-11-13T03:00:00+0000",  
            "dueDate" : "2019-11-14T03:00:00+0000",  
            "reminders" : [ "TRIGGER:P0DT9H0M0S", "TRIGGER:PT0S" ],  
            "priority" : 1,  
            "status" : 0,  
            "completedTime" : "2019-11-13T03:00:00+0000",  
            "sortOrder" : 12345,  
            "items" : [ {  
                "id" : "6435074647fd2e6387145f20",  
                "status" : 0,  
                "title" : "Item Title",  
                "sortOrder" : 12345,  
                "startDate" : "2019-11-13T03:00:00+0000",  
                "isAllDay" : false,  
                "timeZone" : "America/Los_Angeles",  
                "completedTime" : "2019-11-13T03:00:00+0000"  
                } ]  
        }""".trimIndent(),
        Task(
            id = "63b7bebb91c0a5474805fcd4",
            isAllDay = true,
            projectId = "6226ff9877acee87727f6bca",
            title = "Task Title",
            content = "Task Content",
            desc = "Task Description",
            timeZone = "America/Los_Angeles",
            repeatFlag = "RRULE:FREQ=DAILY;INTERVAL=1",
            startDate = "2019-11-13T03:00:00+0000",
            dueDate = "2019-11-14T03:00:00+0000",
            reminders = listOf("TRIGGER:P0DT9H0M0S", "TRIGGER:PT0S"),
            priority = Task.Priority.Low,
            status = Task.Status.Normal,
            completedTime = "2019-11-13T03:00:00+0000",
            sortOrder = 12345,
            items = listOf(
                ChecklistItem(
                    id = "6435074647fd2e6387145f20",
                    status = ChecklistItem.Status.Normal,
                    title = "Item Title",
                    sortOrder = 12345,
                    startDate = "2019-11-13T03:00:00+0000",
                    isAllDay = false,
                    timeZone = "America/Los_Angeles",
                    completedTime = "2019-11-13T03:00:00+0000"
                )
            )
        )
    ),
)
