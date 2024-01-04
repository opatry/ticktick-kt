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
import net.opatry.ticktick.entity.data.projectDataTestData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class TickTickServiceProjectDataTest {

    @Test
    fun `TickTickService getProjectData`() {
        val testData = projectDataTestData.first()

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
            val projectData = ticktickService.getProjectData("6226ff9877acee87727f6bca")
            assertEquals("/open/v1/project/6226ff9877acee87727f6bca/data", request?.url?.encodedPath)
            assertEquals("", request?.url?.encodedQuery)
            assertEquals(HttpMethod.Get, request?.method)
            assertEquals(testData.expectedEntity, projectData)
        }
    }

    @Test
    fun `TickTickService getProjectData failure`() {
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
                    ticktickService.getProjectData("6226ff9877acee87727f6bca")
                }
            }
        }
    }
}