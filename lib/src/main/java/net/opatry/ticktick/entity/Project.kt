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
import net.opatry.ticktick.entity.Project.Kind
import net.opatry.ticktick.entity.Project.Kind.Note
import net.opatry.ticktick.entity.Project.Kind.Task
import net.opatry.ticktick.entity.Project.Permission
import net.opatry.ticktick.entity.Project.Permission.Comment
import net.opatry.ticktick.entity.Project.Permission.Read
import net.opatry.ticktick.entity.Project.Permission.Write
import net.opatry.ticktick.entity.Project.ViewMode
import net.opatry.ticktick.entity.Project.ViewMode.Kanban
import net.opatry.ticktick.entity.Project.ViewMode.List
import net.opatry.ticktick.entity.Project.ViewMode.Timeline

/**
 * @property id Project identifier
 * @property name Project name
 * @property color Project color
 * @property sortOrder Order value
 * @property isClosed Project closed
 * @property groupId Project group identifier
 * @property viewMode view mode, [ViewMode.List], [ViewMode.Kanban], [ViewMode.Timeline]
 * @property permission [Permission.Read], [Permission.Write] or [Permission.Comment]
 * @property kind [Kind.Task] or [Kind.Note]
 */
data class Project(

    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("color")
    val color: String? = null,

    @SerializedName("sortOrder")
    val sortOrder: Long? = null,

    @SerializedName("closed")
    val isClosed: Boolean,

    @SerializedName("groupId")
    val groupId: String? = null,

    @SerializedName("viewMode")
    val viewMode: ViewMode? = null,

    @SerializedName("permission")
    val permission: Permission? = null,

    @SerializedName("kind")
    val kind: Kind? = null,
) {
    /**
     * @property Read `"read"` permission
     * @property Write `"write"` permission
     * @property Comment `"comment"` permission
     */
    enum class Permission {

        @SerializedName("read")
        Read,

        @SerializedName("write")
        Write,

        @SerializedName("comment")
        Comment,
    }

    /**
     * @property List `"list"` view mode
     * @property Kanban `"kanban"` view mode
     * @property Timeline `"timeline"` view mode
     */
    enum class ViewMode {

        @SerializedName("list")
        List,

        @SerializedName("kanban")
        Kanban,

        @SerializedName("timeline")
        Timeline,
    }

    /**
     * @property Task `"TASK"` kind
     * @property Note `"NOTE"` kind
     */
    enum class Kind {

        @SerializedName("TASK")
        Task,

        @SerializedName("NOTE")
        Note,
    }
}
