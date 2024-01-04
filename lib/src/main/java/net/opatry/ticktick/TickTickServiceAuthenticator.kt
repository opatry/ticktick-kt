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

import com.google.gson.annotations.SerializedName
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.CurlUserAgent
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.http.Url
import io.ktor.http.contentType
import io.ktor.http.fullPath
import io.ktor.http.isSuccess
import io.ktor.serialization.gson.gson
import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.call
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import net.opatry.ticktick.TickTickServiceAuthenticator.OAuthToken.TokenType.Bearer
import net.opatry.ticktick.TickTickServiceAuthenticator.OAuthToken.TokenType.Mac
import net.opatry.ticktick.TickTickServiceAuthenticator.Permission.TasksRead
import net.opatry.ticktick.TickTickServiceAuthenticator.Permission.TasksWrite
import java.util.*

interface TickTickServiceAuthenticator {

    /**
     * @property TasksWrite `"tasks:write"` permission scope
     * @property TasksRead `"tasks:read"` permission scope
     */
    enum class Permission(val scope: String) {
        TasksWrite("tasks:write"),
        TasksRead("tasks:read"),
    }

    /**
     * @property accessToken The access token issued by the authorization server.
     * @property tokenType The type of the token issued.
     * @property expiresIn The lifetime in seconds of the access token. For example, the value `"3600"` denotes that the access token will expire in one hour from the time the response was generated. If omitted, the authorization server SHOULD provide the expiration time via other means or document the default value.
     */
    data class OAuthToken(

        @SerializedName("access_token")
        val accessToken: String,

        @SerializedName("token_type")
        val tokenType: TokenType,

        @SerializedName("expires_in")
        val expiresIn: Long? = 0,

        @SerializedName("refresh_token")
        val refreshToken: String? = null,

        @SerializedName("scope")
        val scope: Permission? = null,

        @SerializedName("state")
        val state: String? = null,
    ) {
        /**
         * Value is case insensitive.
         *
         * @property Bearer `"bearer"` token type defined in [RFC6750](https://datatracker.ietf.org/doc/html/rfc6750) is utilized by simply including the access token string in the request.
         * @property Mac `"mac"` token type defined in [OAuth-HTTP-MAC](https://datatracker.ietf.org/doc/html/rfc6749#ref-OAuth-HTTP-MAC) is utilized by issuing a Message Authentication Code (MAC) key together with the access token that is used to sign certain components of the HTTP requests.
         */
        enum class TokenType {

            @SerializedName("bearer")
            Bearer,

            @SerializedName("mac")
            Mac,
        }
    }

    /**
     * @param permissions Permission scope. The currently available scopes are [Permission.TasksWrite], [Permission.TasksRead]
     * @param requestUserAuthorization The URL to which to request user authorization before direction
     *
     * @return auth code
     *
     * @see Permission
     */
    suspend fun authorize(permissions: List<Permission>, requestUserAuthorization: (url: String) -> Unit): String

    /**
     * @param code The code obtained through [authorize].
     * @param permissions Permission scope. The currently available scopes are [Permission.TasksWrite], [Permission.TasksRead]
     *
     * @return OAuth access token
     *
     * @see Permission
     */
    suspend fun getToken(code: String, permissions: List<Permission>): OAuthToken
}

class HttpTickTickServiceAuthenticator(private val config: ApplicationConfig) : TickTickServiceAuthenticator {

    /**
     * @property redirectUrl Redirect url
     * @property clientId OAuth2 Client ID
     * @property clientSecret OAuth2 Client Secret
     */
    data class ApplicationConfig(
        val redirectUrl: String,
        val clientId: String,
        val clientSecret: String,
    )

    private companion object {
        const val TICKTICK_ROOT_URL = "https://ticktick.com"
    }

    private val httpClient: HttpClient by lazy {
        HttpClient(CIO) {
            CurlUserAgent()
            install(ContentNegotiation) {
                gson()
            }
            defaultRequest {
                url(TICKTICK_ROOT_URL)
            }
        }
    }

    override suspend fun authorize(permissions: List<TickTickServiceAuthenticator.Permission>, requestUserAuthorization: (url: String) -> Unit): String {
        val uuid = UUID.randomUUID()
        val params = mapOf(
            "client_id" to config.clientId,
            "scope" to permissions.joinToString("%20", transform = TickTickServiceAuthenticator.Permission::scope),
            "state" to uuid.toString(),
            "redirect_uri" to config.redirectUrl,
            "response_type" to "code",
        ).entries.joinToString(prefix = "?", separator = "&") {
            "${it.key}=${it.value}"
        }

        return callbackFlow {
            val url = Url(config.redirectUrl)
            val server = embeddedServer(Netty, port = url.port, host = url.host) {
                routing {
                    get(url.fullPath.takeIf(String::isNotEmpty) ?: "/") {
                        fun Parameters.require(key: String): String = requireNotNull(get(key)) { "Expected '$key' query parameter not available." }

                        val queryParams = call.request.queryParameters
                        try {
                            // throw if any error query parameter if provided
                            queryParams["error"]?.let(::error)

                            val state = queryParams.require("state")
                            require(uuid == UUID.fromString(state)) { "Mismatch between expected & provided state ($state)." }
                            val authCode = queryParams.require("code")
                            val status = HttpStatusCode.OK
                            call.respond(status, "$status: Authorization accepted.")
                            send(authCode)
                            close(null)
                        } catch (e: Exception) {
                            val status = HttpStatusCode.BadRequest
                            call.respond(status, "$status: ${e.message}")
                            close(e)
                        }
                    }
                }
            }
            server.environment.monitor.subscribe(ApplicationStarted) {
                requestUserAuthorization("$TICKTICK_ROOT_URL/oauth/authorize$params")
            }
            server.start(wait = false)
            awaitClose(server::stop)
        }.first()
    }

    override suspend fun getToken(code: String, permissions: List<TickTickServiceAuthenticator.Permission>): TickTickServiceAuthenticator.OAuthToken {
        val scope = permissions.joinToString(" ", transform = TickTickServiceAuthenticator.Permission::scope)
        val response = httpClient.post("oauth/token") {
            parameter("client_id", config.clientId)
            parameter("client_secret", config.clientSecret)
            parameter("code", code)
            parameter("grant_type", "authorization_code")
            parameter("scope", scope)
            parameter("redirect_uri", config.redirectUrl)
            contentType(ContentType.Application.FormUrlEncoded)
        }

        if (response.status.isSuccess()) {
            return response.body()
        } else {
            throw ClientRequestException(response, response.bodyAsText())
        }
    }
}