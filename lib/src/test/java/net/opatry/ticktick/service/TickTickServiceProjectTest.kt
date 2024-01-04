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

package net.opatry.ticktick.service

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.HttpRequestData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.runBlocking
import net.opatry.ticktick.entity.Project
import net.opatry.ticktick.entity.ProjectCreationRequest
import net.opatry.ticktick.entity.ProjectUpdateRequest
import net.opatry.ticktick.entity.data.projectData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class TickTickServiceProjectTest {

    @Test
    fun `TickTickService getProjects`() {
        val testData = projectData.first()

        var request: HttpRequestData? = null
        val mockEngine = MockEngine {
            request = it
            respond(
                content = ByteReadChannel("[${testData.jsonPayload}]"),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        usingTickTickService(mockEngine) { ticktickService ->
            val projects = ticktickService.getProjects()
            assertEquals("/open/v1/project", request?.url?.encodedPath)
            assertEquals("", request?.url?.encodedQuery)
            assertEquals(HttpMethod.Get, request?.method)
            assertEquals(listOf(testData.expectedEntity), projects)
        }
    }

    @Test
    fun `TickTickService getProjects failure`() {
        val mockEngine = MockEngine {
            respond(
                content = ByteReadChannel(""),
                status = HttpStatusCode.Forbidden,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        usingTickTickService(mockEngine) { ticktickService ->
            assertThrows(ClientRequestException::class.java) {
                runBlocking {
                    ticktickService.getProjects()
                }
            }
        }
    }

    @Test
    fun `TickTickService createProject`() {
        var request: HttpRequestData? = null
        val mockEngine = MockEngine {
            request = it
            respond(
                content = ByteReadChannel(
                    """{
                        "id": "6226ff9877acee87727f6bca",
                        "name": "project name",
                        "color": "#F18181",
                        "closed": false,
                        "groupId": "6436176a47fd2e05f26ef56e",
                        "viewMode": "list",
                        "kind": "TASK"
                    }""".trimIndent()
                ),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        usingTickTickService(mockEngine) { ticktickService ->
            val projectData = ProjectCreationRequest(
                name = "project name",
                color = "#F18181",
                viewMode = Project.ViewMode.List,
                kind = Project.Kind.Task
            )

            val project = ticktickService.createProject(projectData)
            assertEquals("/open/v1/project", request?.url?.encodedPath)
            assertEquals("", request?.url?.encodedQuery)
            assertEquals(HttpMethod.Post, request?.method)
            val expected = Project(
                id = "6226ff9877acee87727f6bca",
                name = "project name",
                color = "#F18181",
                isClosed = false,
                groupId = "6436176a47fd2e05f26ef56e",
                viewMode = Project.ViewMode.List,
                kind = Project.Kind.Task
            )
            assertEquals(expected, project)
        }
    }

    @Test
    fun `TickTickService createProject failure`() {
        val mockEngine = MockEngine {
            respond(
                content = ByteReadChannel(""),
                status = HttpStatusCode.Forbidden,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        usingTickTickService(mockEngine) { ticktickService ->
            assertThrows(ClientRequestException::class.java) {
                runBlocking {
                    ticktickService.createProject(ProjectCreationRequest("Foo"))
                }
            }
        }
    }

    @Test
    fun `TickTickService getProject`() {
        val testData = projectData.first()

        var request: HttpRequestData? = null
        val mockEngine = MockEngine {
            request = it
            respond(
                content = ByteReadChannel(testData.jsonPayload),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        usingTickTickService(mockEngine) { ticktickService ->
            val project = ticktickService.getProject("2203306141")
            assertEquals("/open/v1/project/2203306141", request?.url?.encodedPath)
            assertEquals("", request?.url?.encodedQuery)
            assertEquals(HttpMethod.Get, request?.method)
            assertEquals(testData.expectedEntity, project)
        }
    }

    @Test
    fun `TickTickService getProject failure`() {
        val mockEngine = MockEngine {
            respond(
                content = ByteReadChannel(""),
                status = HttpStatusCode.Forbidden,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        usingTickTickService(mockEngine) { ticktickService ->
            assertThrows(ClientRequestException::class.java) {
                runBlocking {
                    ticktickService.getProject("2203306141")
                }
            }
        }
    }

    @Test
    fun `TickTickService updateProject`() {
        var request: HttpRequestData? = null
        val mockEngine = MockEngine {
            request = it
            respond(
                content = ByteReadChannel(
                    """{
                        "id": "6226ff9877acee87727f6bca",
                        "name": "project name2",
                        "color": "#F18181",
                        "closed": false,
                        "groupId": "6436176a47fd2e05f26ef56e",
                        "viewMode": "list",
                        "kind": "TASK"
                    }""".trimIndent()
                ),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        usingTickTickService(mockEngine) { ticktickService ->
            val project = ticktickService.updateProject("2203306141", ProjectUpdateRequest(name = "project name2"))
            assertEquals("/open/v1/project/2203306141", request?.url?.encodedPath)
            assertEquals("", request?.url?.encodedQuery)
            assertEquals(HttpMethod.Post, request?.method)
            val expected = Project(
                id = "6226ff9877acee87727f6bca",
                name = "project name2",
                color = "#F18181",
                isClosed = false,
                groupId = "6436176a47fd2e05f26ef56e",
                viewMode = Project.ViewMode.List,
                kind = Project.Kind.Task
            )
            assertEquals(expected, project)
        }
    }

    @Test
    fun `TickTickService updateProject failure`() {
        val mockEngine = MockEngine {
            respond(
                content = ByteReadChannel(""),
                status = HttpStatusCode.Forbidden,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        usingTickTickService(mockEngine) { ticktickService ->
            assertThrows(ClientRequestException::class.java) {
                runBlocking {
                    ticktickService.updateProject("6247ee29630c800f064fd145", ProjectUpdateRequest(name = "Bar"))
                }
            }
        }
    }

    @Test
    fun `TickTickService deleteProject`() {
        var request: HttpRequestData? = null
        val mockEngine = MockEngine {
            request = it
            respond(
                content = ByteReadChannel(""),
                status = HttpStatusCode.NoContent,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        usingTickTickService(mockEngine) { ticktickService ->
            ticktickService.deleteProject("6226ff9877acee87727f6bca")
            assertEquals("/open/v1/project/6226ff9877acee87727f6bca", request?.url?.encodedPath)
            assertEquals("", request?.url?.encodedQuery)
            assertEquals(HttpMethod.Delete, request?.method)
        }
    }

    @Test
    fun `TickTickService deleteProject failure`() {
        val mockEngine = MockEngine {
            respond(
                content = ByteReadChannel(""),
                status = HttpStatusCode.Forbidden,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        usingTickTickService(mockEngine) { ticktickService ->
            assertThrows(ClientRequestException::class.java) {
                runBlocking {
                    ticktickService.deleteProject("6226ff9877acee87727f6bca")
                }
            }
        }
    }
}