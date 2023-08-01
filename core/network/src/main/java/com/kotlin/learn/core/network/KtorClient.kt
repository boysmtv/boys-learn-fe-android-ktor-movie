package com.kotlin.learn.core.network

import android.util.Log
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.kotlin.learn.core.utilities.Constant
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.plugins.resources.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.URLProtocol
import io.ktor.http.appendPathSegments
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit

class KtorClient(
    chuckerInterceptor: ChuckerInterceptor,
) {
    private val client = initializeKtor(chuckerInterceptor)

    private val springUrl = "192.168.0.6:8080"
    private val springClient = initializeKtor(
        chuckerInterceptor = chuckerInterceptor,
        springUrl = springUrl,
        springHeader = mapOf(
            "X-Api-Key" to "SECRET"
        )
    )

    internal suspend inline fun <reified Z : Any, reified T> sendRequestApiWithQuery(
        resources: Z,
        query: Map<String, String>,
        path: String? = null,
        body: Z? = null,
    ): T {
        return client
            .get(resources) {
                url {
                    query.forEach { item ->
                        parameters.append(item.key, item.value)
                    }
                    path?.let {
                        appendPathSegments(it)
                    }
                    body?.let {
                        setBody(body)
                    }
                }
            }
            .body()
    }

    internal suspend inline fun <reified Z : Any, reified T> postRequestApi(
        resources: String,
        query: Map<String, String>? = null,
        path: String? = null,
        body: Z? = null,
    ): T {
        return springClient
            .post(resources) {
                url {
                    query?.let {
                        it.forEach { item ->
                            parameters.append(item.key, item.value)
                        }
                    }
                    path?.let {
                        appendPathSegments(it)
                    }
                }
                body?.let {
                    setBody(body)
                }
            }.body()
    }

    companion object {

        private const val timeout = 60L

        private fun initializeKtor(
            chuckerInterceptor: ChuckerInterceptor,
            springUrl: String? = null,
            springHeader: Map<String, String>? = null,
        ) = HttpClient(OkHttp) {

            engine {
                addInterceptor(chuckerInterceptor)
                config {
                    connectTimeout(timeout = timeout, unit = TimeUnit.SECONDS)
                    writeTimeout(timeout = timeout, unit = TimeUnit.SECONDS)
                    readTimeout(timeout = timeout, unit = TimeUnit.SECONDS)
                }
            }

            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        prettyPrint = true
                        isLenient = true
                    },
                )
            }

            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.v("Logger Ktor ->", message)
                    }
                }
                level = LogLevel.ALL
            }

            install(Auth) {
                bearer {
                    loadTokens {
                        BearerTokens(Constant.TOKEN, Constant.EMPTY_STRING)
                    }
                }
            }

            install(ResponseObserver) {
                onResponse { response ->
                    Log.d("Http status:", "${response.status.value}")
                }
            }

            install(DefaultRequest) {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                springHeader?.let { map ->
                    map.forEach {
                        header(it.key, it.value)
                    }
                }
            }

            install(Resources)
            defaultRequest {
                url {
                    protocol = if (springUrl.isNullOrBlank()) URLProtocol.HTTPS else URLProtocol.HTTP
                    host = springUrl ?: Constant.BASE_URL
                }
            }

            HttpResponseValidator {
                validateResponse { response: HttpResponse ->
                    val statusCode = response.status.value
                    when (statusCode) {
                        in 300..399 -> throw RedirectResponseException(response, "Redirect Response")
                        in 400..499 -> throw ClientRequestException(response, "Client Request")
                        in 500..599 -> throw ServerResponseException(response, "Server Response")
                    }

                    if (statusCode >= 600) {
                        throw ResponseException(response, "Response Exception")
                    }
                }
            }
        }
    }
}