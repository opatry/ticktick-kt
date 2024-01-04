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

import net.opatry.ticktick.entity.Project

val projectData = listOf(
    EntityTestParam.build(
        """{
            "id": "6226ff9877acee87727f6bca",
            "name": "project name",
            "color": "#F18181",
            "closed": false,
            "groupId": "6436176a47fd2e05f26ef56e",
            "viewMode": "list",
            "kind": "TASK"
        }""".trimIndent(),
        Project(
            id = "6226ff9877acee87727f6bca",
            name = "project name",
            color = "#F18181",
            isClosed = false,
            groupId = "6436176a47fd2e05f26ef56e",
            viewMode = Project.ViewMode.List,
            kind = Project.Kind.Task
        )
    ),
)
