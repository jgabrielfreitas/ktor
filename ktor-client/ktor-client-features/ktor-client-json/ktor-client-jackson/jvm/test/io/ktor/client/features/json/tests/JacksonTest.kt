/*
 * Copyright 2014-2019 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package io.ktor.client.features.json.tests

import com.fasterxml.jackson.annotation.*
import io.ktor.application.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.client.tests.utils.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlin.test.*

class JacksonTest : JsonTest() {
    override val serializerImpl = JacksonSerializer()

    override fun Routing.createRoutes() {
        super.createRoutes()

        post("/jackson") {
            assertEquals(Jackson("request", null), call.receive())
            call.respond(
                Response(
                    true,
                    listOf(Jackson("response", "not_ignored"))
                )
            ) // encoded with GsonConverter
        }
    }

    @Test
    fun testJackson() = testWithEngine(CIO) {
        configClient()

        test { client ->
            val response = client.post<Response<List<Jackson>>>(
                port = serverPort,
                path = "jackson",
                body = Jackson(
                    "request",
                    "ignored"
                )
            ) {
                contentType(ContentType.Application.Json)
            }

            assertTrue(response.ok)
            val list = response.result!!
            assertEquals(1, list.size)
            assertEquals(Jackson("response", null), list[0]) // encoded with GsonConverter
        }
    }

    data class Jackson(val value: String, @JsonIgnore val ignoredValue: String?)
}
