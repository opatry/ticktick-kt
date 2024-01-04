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
import net.opatry.ticktick.entity.Column
import net.opatry.ticktick.entity.Project
import net.opatry.ticktick.entity.ProjectData
import net.opatry.ticktick.entity.Task

val projectDataTestData = listOf(
    EntityTestParam.build(
        """{
            project = {
                id = "6226ff9877acee87727f6bca",
                name = "project name",
                color = "#F18181",
                closed = false,
                groupId = "6436176a47fd2e05f26ef56e",
                viewMode = "list",
                kind = "TASK"
            },
            tasks = [{
                id = "6247ee29630c800f064fd145",
                isAllDay = true,
                projectId = "6226ff9877acee87727f6bca",
                title = "Task Title",
                content = "Task Content",
                desc = "Task Description",
                timeZone = "America/Los_Angeles",
                repeatFlag = "RRULE:FREQ=DAILY;INTERVAL=1",
                startDate = "2019-11-13T03:00:00+0000",
                dueDate = "2019-11-14T03:00:00+0000",
                reminders = [
                    "TRIGGER:P0DT9H0M0S",
                    "TRIGGER:PT0S"
                ],
                priority = 1,
                status = 0,
                completedTime = "2019-11-13T03:00:00+0000",
                sortOrder = 12345,
                items = [{
                    id = "6435074647fd2e6387145f20",
                    status = 0,
                    title = "Subtask Title",
                    sortOrder = 12345,
                    startDate = "2019-11-13T03:00:00+0000",
                    isAllDay = false,
                    timeZone = "America/Los_Angeles",
                    completedTime = "2019-11-13T03:00:00+0000"
                }]
            }],
            columns = [{
                id = "6226ff9e76e5fc39f2862d1b",
                projectId = "6226ff9877acee87727f6bca",
                name = "Column Name",
                sortOrder = 0
            }]
        }""".trimIndent(),
        ProjectData(
            project = Project(
                id = "6226ff9877acee87727f6bca",
                name = "project name",
                color = "#F18181",
                isClosed = false,
                groupId = "6436176a47fd2e05f26ef56e",
                viewMode = Project.ViewMode.List,
                kind = Project.Kind.Task
            ),
            tasks = listOf(
                Task(
                    id = "6247ee29630c800f064fd145",
                    isAllDay = true,
                    projectId = "6226ff9877acee87727f6bca",
                    title = "Task Title",
                    content = "Task Content",
                    desc = "Task Description",
                    timeZone = "America/Los_Angeles",
                    repeatFlag = "RRULE:FREQ=DAILY;INTERVAL=1",
                    startDate = "2019-11-13T03:00:00+0000",
                    dueDate = "2019-11-14T03:00:00+0000",
                    reminders = listOf(
                        "TRIGGER:P0DT9H0M0S",
                        "TRIGGER:PT0S"
                    ),
                    priority = Task.Priority.Low,
                    status = Task.Status.Normal,
                    completedTime = "2019-11-13T03:00:00+0000",
                    sortOrder = 12345,
                    items = listOf(
                        ChecklistItem(
                            id = "6435074647fd2e6387145f20",
                            status = ChecklistItem.Status.Normal,
                            title = "Subtask Title",
                            sortOrder = 12345,
                            startDate = "2019-11-13T03:00:00+0000",
                            isAllDay = false,
                            timeZone = "America/Los_Angeles",
                            completedTime = "2019-11-13T03:00:00+0000"
                        )
                    )
                )
            ),
            columns = listOf(
                Column(
                    id = "6226ff9e76e5fc39f2862d1b",
                    projectId = "6226ff9877acee87727f6bca",
                    name = "Column Name",
                    sortOrder = 0
                )
            ),
        )
    )
)