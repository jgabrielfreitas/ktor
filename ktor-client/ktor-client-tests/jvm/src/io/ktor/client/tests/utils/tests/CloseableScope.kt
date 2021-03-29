/*
 * Copyright 2014-2021 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package io.ktor.client.tests.utils.tests

import java.io.*

class CloseableScope : Closeable {
    private val resources = mutableListOf<Closeable>()

    fun use(resource: Closeable) {
        resources.add(resource)
    }

    override fun close() {
        resources.forEach { it.close() }
    }
}
