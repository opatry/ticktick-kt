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
 * @property name name of the project
 * @property color color of project
 * @property sortOrder sort order value, default 0
 * @property viewMode view mode, [Project.ViewMode.List], [Project.ViewMode.Kanban], [Project.ViewMode.Timeline]
 * @property kind project kind, [Project.Kind.Task], [Project.Kind.Note]
 */
data class ProjectUpdateRequest(

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("color")
    val color: String? = null,

    @SerializedName("sortOrder")
    val sortOrder: Long? = 0,

    @SerializedName("viewMode")
    val viewMode: Project.ViewMode? = null,

    @SerializedName("kind")
    val kind: Project.Kind? = null,
)
