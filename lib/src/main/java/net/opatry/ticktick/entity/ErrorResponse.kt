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
import net.opatry.ticktick.entity.ErrorResponse.Error.AccessDenied
import net.opatry.ticktick.entity.ErrorResponse.Error.InvalidClient
import net.opatry.ticktick.entity.ErrorResponse.Error.InvalidGrant
import net.opatry.ticktick.entity.ErrorResponse.Error.InvalidRequest
import net.opatry.ticktick.entity.ErrorResponse.Error.InvalidScope
import net.opatry.ticktick.entity.ErrorResponse.Error.MethodNotAllowed
import net.opatry.ticktick.entity.ErrorResponse.Error.ServerError
import net.opatry.ticktick.entity.ErrorResponse.Error.TemporarilyUnavailable
import net.opatry.ticktick.entity.ErrorResponse.Error.UnauthorizedClient
import net.opatry.ticktick.entity.ErrorResponse.Error.UnsupportedGrantType
import net.opatry.ticktick.entity.ErrorResponse.Error.UnsupportedResponseType

/**
 * @property error Type of error
 * @property errorDescription Error description
 * @property scope provided scope in case of [InvalidScope], `null` otherwise
 * @property errorUri A URI identifying a human-readable web page with information about the error, used to provide the client developer with additional information about the error.
 */
data class ErrorResponse(

    @SerializedName("error")
    val error: Error,

    @SerializedName("error_description")
    val errorDescription: String? = null,

    @SerializedName("error_uri")
    val errorUri: String? = null,

    @SerializedName("scope")
    val scope: String? = null,
) {
    /**
     * @property MethodNotAllowed `"method_not_allowed"`
     * @property InvalidRequest `"invalid_request"`
     * @property InvalidClient `"invalid_client"`
     * @property InvalidGrant `"invalid_grant"`
     * @property InvalidScope `"invalid_scope"`
     * @property UnauthorizedClient `"unauthorized_client"`
     * @property AccessDenied `"access_denied"`
     * @property UnsupportedGrantType `"unsupported_grant_type"`
     * @property UnsupportedResponseType `"unsupported_response_type"`
     * @property ServerError `"server_error"`
     * @property TemporarilyUnavailable `"temporarily_unavailable"`
     */
    enum class Error {

        @SerializedName("method_not_allowed")
        MethodNotAllowed,

        @SerializedName("invalid_request")
        InvalidRequest,

        @SerializedName("invalid_client")
        InvalidClient,

        @SerializedName("invalid_grant")
        InvalidGrant,

        @SerializedName("invalid_scope")
        InvalidScope,

        @SerializedName("unauthorized_client")
        UnauthorizedClient,

        @SerializedName("access_denied")
        AccessDenied,

        @SerializedName("unsupported_grant_type")
        UnsupportedGrantType,

        @SerializedName("unsupported_response_type")
        UnsupportedResponseType,

        @SerializedName("server_error")
        ServerError,

        @SerializedName("temporarily_unavailable")
        TemporarilyUnavailable,
    }
}