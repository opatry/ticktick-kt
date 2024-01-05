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
import net.opatry.ticktick.entity.ChecklistItem
import net.opatry.ticktick.entity.ChecklistItemEdit
import net.opatry.ticktick.entity.Task
import net.opatry.ticktick.entity.TaskCreationRequest
import net.opatry.ticktick.entity.TaskUpdateRequest
import net.opatry.ticktick.entity.data.taskData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class TickTickServiceTaskTest {

    @Test
    fun `TickTickService createTask`() {
        var request: HttpRequestData? = null
        val mockEngine = MockEngine {
            request = it
            respond(
                content = ByteReadChannel(
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
                    }""".trimIndent()
                ),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        usingTickTickService(mockEngine) { ticktickService ->
            val taskData = TaskCreationRequest(
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
                        title = "Item Title",
                        sortOrder = 12345,
                        startDate = "2019-11-13T03:00:00+0000",
                        isAllDay = false,
                        timeZone = "America/Los_Angeles",
                        completedTime = "2019-11-13T03:00:00+0000"
                    )
                )
            )

            val task = ticktickService.createTask(taskData)
            assertEquals("/open/v1/task", request?.url?.encodedPath)
            assertEquals("", request?.url?.encodedQuery)
            assertEquals(HttpMethod.Post, request?.method)
            val expected = Task(
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
            assertEquals(expected, task)
        }
    }

    @Test
    fun `TickTickService createTask failure`() {
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
                    ticktickService.createTask(TaskCreationRequest("Foo", "6247ee29630c800f064fd145"))
                }
            }
        }
    }

    @Test
    fun `TickTickService getTask`() {
        val testData = taskData.first()

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
            val task = ticktickService.getTask("6247ee29630c800f064fd145", "6247ee29630c800f064fd145")
            assertEquals(
                "/open/v1/project/6247ee29630c800f064fd145/task/6247ee29630c800f064fd145",
                request?.url?.encodedPath
            )
            assertEquals("", request?.url?.encodedQuery)
            assertEquals(HttpMethod.Get, request?.method)
            assertEquals(testData.expectedEntity, task)
        }
    }

    @Test
    fun `TickTickService getTask failure`() {
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
                    ticktickService.getTask("6247ee29630c800f064fd145", "6247ee29630c800f064fd145")
                }
            }
        }
    }

    @Test
    fun `TickTickService updateTask`() {
        var request: HttpRequestData? = null
        val mockEngine = MockEngine {
            request = it
            respond(
                content = ByteReadChannel(
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
                    }""".trimIndent()
                ),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        usingTickTickService(mockEngine) { ticktickService ->
            val task = ticktickService.updateTask(
                "63b7bebb91c0a5474805fcd4",
                TaskUpdateRequest(projectId = "6226ff9877acee87727f6bca", id = "63b7bebb91c0a5474805fcd4", content = "Buy Coffee")
            )
            assertEquals("/open/v1/task/63b7bebb91c0a5474805fcd4", request?.url?.encodedPath)
            assertEquals("", request?.url?.encodedQuery)
            assertEquals(HttpMethod.Post, request?.method)
            val expected = Task(
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
            assertEquals(expected, task)
        }
    }

    @Test
    fun `TickTickService updateTask failure`() {
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
                    ticktickService.updateTask(
                        "6247ee29630c800f064fd145",
                        TaskUpdateRequest(projectId = "6226ff9877acee87727f6bca", id = "6247ee29630c800f064fd145", content = "Bar")
                    )
                }
            }
        }
    }

    @Test
    fun `TickTickService completeTask`() {
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
            ticktickService.completeTask("6226ff9877acee87727f6bca", "6247ee29630c800f064fd145")
            assertEquals(
                "/open/v1/project/6226ff9877acee87727f6bca/task/6247ee29630c800f064fd145/complete",
                request?.url?.encodedPath
            )
            assertEquals("", request?.url?.encodedQuery)
            assertEquals(HttpMethod.Post, request?.method)
        }
    }

    @Test
    fun `TickTickService completeTask failure`() {
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
                    ticktickService.completeTask("6226ff9877acee87727f6bca", "6247ee29630c800f064fd145")
                }
            }
        }
    }

    @Test
    fun `TickTickService deleteTask`() {
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
            ticktickService.deleteTask("6226ff9877acee87727f6bca", "6247ee29630c800f064fd145")
            assertEquals(
                "/open/v1/project/6226ff9877acee87727f6bca/task/6247ee29630c800f064fd145",
                request?.url?.encodedPath
            )
            assertEquals("", request?.url?.encodedQuery)
            assertEquals(HttpMethod.Delete, request?.method)
        }
    }

    @Test
    fun `TickTickService deleteTask failure`() {
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
                    ticktickService.deleteTask("6226ff9877acee87727f6bca", "6247ee29630c800f064fd145")
                }
            }
        }
    }
}