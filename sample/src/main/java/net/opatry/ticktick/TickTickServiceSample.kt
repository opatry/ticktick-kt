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

package net.opatry.ticktick

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import com.google.gson.stream.JsonReader
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.CurlUserAgent
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.gson.gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import net.opatry.ticktick.entity.ChecklistItem
import net.opatry.ticktick.entity.ChecklistItemEdit
import net.opatry.ticktick.entity.Project
import net.opatry.ticktick.entity.ProjectCreationRequest
import net.opatry.ticktick.entity.Task
import net.opatry.ticktick.entity.TaskCreationRequest
import net.opatry.ticktick.entity.TaskUpdateRequest
import java.awt.Desktop
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.net.URI
import java.util.*
import kotlin.time.Duration.Companion.seconds
import net.opatry.ticktick.entity.ChecklistItem.Status as ChecklistItemStatus
import net.opatry.ticktick.entity.Task.Status as TaskStatus


private val TaskStatus.checkbox: String
    get() = when(this) {
        TaskStatus.Normal -> "☐"
        TaskStatus.Completed -> "☑︎"
    }

private val ChecklistItemStatus.checkbox: String
    get() = when(this) {
        ChecklistItemStatus.Normal -> "☐"
        ChecklistItemStatus.Completed -> "☑︎"
    }

data class TokenCache(

    @SerializedName("access_token")
    val accessToken: String? = null,

    @SerializedName("expiration_time_millis")
    val expirationTimeMillis: Long,
)

suspend fun runAuthenticationFlow(): TickTickServiceAuthenticator.OAuthToken {
    val permissions = TickTickServiceAuthenticator.Permission.entries
    val config = HttpTickTickServiceAuthenticator.ApplicationConfig(
        redirectUrl = "http://localhost:8889/ticktick-callback",
        clientId = System.getenv("TICKTICK_API_CLIENT_ID"),
        clientSecret = System.getenv("TICKTICK_API_CLIENT_SECRET"),
    )
    val authenticator: TickTickServiceAuthenticator = HttpTickTickServiceAuthenticator(config)
    // even if we have a code, can't reuse it, authorize again
    val code = authenticator.authorize(permissions) { url ->
        Desktop.getDesktop().browse(URI.create(url))
    }

    return authenticator.getToken(code, permissions)
}

suspend fun File.storeTokenCache(tokenCache: TokenCache) {
    val self = this
    withContext(Dispatchers.IO) {
        FileWriter(self).use { writer ->
            GsonBuilder()
                .setPrettyPrinting()
                .create()
                .toJson(tokenCache, writer)
        }
    }
}

suspend fun File.loadTokenCache(): TokenCache? {
    val self = this
    return withContext(Dispatchers.IO) {
        if (self.isFile) {
            JsonReader(FileReader(self)).use { reader ->
                Gson().fromJson<TokenCache>(reader, TokenCache::class.java)
            }
        } else {
            null
        }
    }
}

fun main() {
    // totally unsafe, quick & dirty persistence of access token to avoid authorizing at each launch
    val tokenCacheFile = File("token_cache.json")
    runBlocking {
        // region access token negotiation
        val tokenCache = tokenCacheFile.loadTokenCache()
        val accessToken = if (tokenCache?.accessToken != null && tokenCache.expirationTimeMillis > System.currentTimeMillis() + 1_000) {
            tokenCache.accessToken
        } else {
            val t0 = System.currentTimeMillis()
            val token = runAuthenticationFlow()
            val expirationTimeMillis = t0 + (token.expiresIn ?: 0).seconds.inWholeMilliseconds

            println("Token expires in ${token.expiresIn} (${Date(expirationTimeMillis)})")

            tokenCacheFile.storeTokenCache(TokenCache(token.accessToken, expirationTimeMillis))
            token.accessToken
        }
        // endregion

        // region HTTP Client setup
        val httpClient = HttpClient(CIO) {
            CurlUserAgent()
            install(ContentNegotiation) {
                gson()
            }
            install(Auth) {
                bearer {
                    sendWithoutRequest { true }
                    loadTokens {
                        BearerTokens(accessToken, "")
                    }
                }
            }
            defaultRequest {
                url("https://api.ticktick.com")
            }
        }
        // endregion

        val tickTickService: TickTickService = HttpTickTickService(httpClient)

        if (true) {
            val projectId = tickTickService.getProjects().find {it.name == "This is my project" }?.id
            val task = tickTickService.createTask(
                TaskCreationRequest(
                projectId = projectId,
                isAllDay = true,
                title = "Task Title",
                content = "Task Content",
                desc = "Task Description",
                timeZone = "America/Los_Angeles",
                repeatFlag = "RRULE:FREQ=DAILY;INTERVAL=1",
                startDate = "2019-11-13T03:00:00+0000",
                dueDate = "2019-11-14T03:00:00+0000",
                reminders = listOf("TRIGGER:P0DT9H0M0S", "TRIGGER:PT0S"),
                priority = Task.Priority.Low,
                sortOrder = 12345,
                items = listOf(
                    ChecklistItemEdit(
                        status = ChecklistItem.Status.Normal,
                        title = "Item Title ${System.currentTimeMillis().toString().takeLast(7)}",
                    )
                )
            )
            )
            println("task created: $task")
            // FIXME how to move a task from a project to another (by default, it's inboxXXXXXXXXX)
            val updatedTask = tickTickService.updateTask(task.id, TaskUpdateRequest(id = task.id, projectId = task.projectId, title = "Foo bar"))
            println("task updated: $updatedTask")
        } else if (false) {
            val project = tickTickService.createProject(
                ProjectCreationRequest(
                    name = "This is my project",
                    color = "#FF00FF",
                    viewMode = Project.ViewMode.List,
                    kind = Project.Kind.Task,
                )
            )
            println("Project ${project.name} (#${project.id}) created")
        } else {
            val projects = tickTickService.getProjects()
            if (projects.isEmpty()) {
                println("No project found, creating one")
                val project = tickTickService.createProject(ProjectCreationRequest("My whole new project"))
                println("Project ${project.name} (#${project.id}) created")
            } else {
                projects.sortedBy(Project::sortOrder).forEach { project ->
                    println("Tasks of project ${project.name} (#${project.id})")
                    tickTickService.getProjectData(project.id).tasks
                        .sortedWith(compareBy(Task::status, Task::sortOrder))
                        .forEach { task ->
                            println("\t${task.status.checkbox} Task: ${task.title} ${task.priority} (#${task.id})")
                            task.items
                                ?.sortedWith(compareBy(ChecklistItem::status, ChecklistItem::sortOrder))
                                ?.forEach { checklistItem ->
                                    println("\t\t${checklistItem.status.checkbox} ${checklistItem.title} (#${checklistItem.id})")
                                }
                        }
                }
            }
        }
    }
}