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

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import net.opatry.ticktick.entity.Project
import net.opatry.ticktick.entity.ProjectCreationRequest
import net.opatry.ticktick.entity.ProjectData
import net.opatry.ticktick.entity.ProjectUpdateRequest
import net.opatry.ticktick.entity.Task
import net.opatry.ticktick.entity.TaskCreationRequest
import net.opatry.ticktick.entity.TaskUpdateRequest

interface TickTickService {
    // region Task

    suspend fun getTask(projectId: String, taskId: String): Task

    suspend fun createTask(request: TaskCreationRequest): Task

    suspend fun updateTask(taskId: String, request: TaskUpdateRequest): Task

    suspend fun completeTask(projectId: String, taskId: String)

    suspend fun deleteTask(projectId: String, taskId: String)

    // endregion

    // region Project

    suspend fun getProjects(): List<Project>

    suspend fun getProject(projectId: String): Project

    suspend fun getProjectData(projectId: String): ProjectData

    suspend fun createProject(request: ProjectCreationRequest): Project

    suspend fun updateProject(projectId: String, request: ProjectUpdateRequest): Project

    suspend fun deleteProject(projectId: String)

    // endregion
}

class HttpTickTickService(private val httpClient: HttpClient) : TickTickService {

    private companion object {
        suspend inline fun <reified T> HttpClient.getOrThrow(endpoint: String, parameters: Map<String, Any> = emptyMap()): T {
            val response = get(endpoint) {
                contentType(ContentType.Application.Json)
                parameters.forEach { (k, v) ->
                    parameter(k, v)
                }
            }

            if (response.status.isSuccess()) {
                return response.body()
            } else {
                throw ClientRequestException(response, response.bodyAsText())
            }
        }

        suspend inline fun <reified T, reified R> HttpClient.postOrThrow(endpoint: String, body: T): R {
            val response = post(endpoint) {
                contentType(ContentType.Application.Json)
                setBody(body)
            }

            if (response.status.isSuccess()) {
                return response.body()
            } else {
                throw ClientRequestException(response, response.bodyAsText())
            }
        }

        suspend inline fun HttpClient.deleteOrThrow(endpoint: String) {
            val response = delete(endpoint)

            if (response.status.isSuccess()) {
                return response.body()
            } else {
                throw ClientRequestException(response, response.bodyAsText())
            }
        }
    }

    // region Task

    override suspend fun getTask(projectId: String, taskId: String): Task {
        return httpClient.getOrThrow("open/v1/project/${projectId}/task/${taskId}")
    }

    override suspend fun createTask(request: TaskCreationRequest): Task {
        return httpClient.postOrThrow("open/v1/task", request)
    }

    override suspend fun updateTask(taskId: String, request: TaskUpdateRequest): Task {
        require(taskId == request.id) { "Provided task id ($taskId) and update request id (${request.id}) doesn't match." }
        // FIXME edge cases encountered
        //  if the project id provided in the task is invalid, we receive a 500 error from server
        //  if the project id doesn't match the updated task's one, we receive a 200 success without data (can't be used to move a task to another project)
        //  return httpClient.postOrThrow("open/v1/task/${taskId}", request)
        val response = httpClient.post("open/v1/task/${taskId}") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        if (response.status.isSuccess()) {
            if (response.bodyAsText().isNotEmpty()) {
                return response.body()
            } else {
                throw ClientRequestException(response, "Task not updated (invalid project id? ${request.projectId})")
            }
        } else {
            throw ClientRequestException(response, response.bodyAsText())
        }
    }

    override suspend fun completeTask(projectId: String, taskId: String) {
        val response = httpClient.post("open/v1/project/${projectId}/task/${taskId}/complete")

        if (!response.status.isSuccess()) {
            throw ClientRequestException(response, response.bodyAsText())
        }
    }

    override suspend fun deleteTask(projectId: String, taskId: String) {
        httpClient.deleteOrThrow("open/v1/project/${projectId}/task/${taskId}")
    }

    // endregion

    // region Project

    override suspend fun getProjects(): List<Project> {
        return httpClient.getOrThrow("open/v1/project")
    }

    override suspend fun getProject(projectId: String): Project {
        return httpClient.getOrThrow("open/v1/project/${projectId}")
    }

    override suspend fun getProjectData(projectId: String): ProjectData {
        return httpClient.getOrThrow("open/v1/project/${projectId}/data")
    }

    override suspend fun createProject(request: ProjectCreationRequest): Project {
        return httpClient.postOrThrow("open/v1/project", request)
    }

    override suspend fun updateProject(projectId: String, request: ProjectUpdateRequest): Project {
        return httpClient.postOrThrow("open/v1/project/${projectId}", request)
    }

    override suspend fun deleteProject(projectId: String) {
        httpClient.deleteOrThrow("open/v1/project/${projectId}")
    }

    // endregion
}