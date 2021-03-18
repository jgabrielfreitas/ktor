/*
 * Copyright 2014-2021 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package io.ktor.utils.io.core.internal

import io.ktor.utils.io.bits.*
import java.nio.*


@DangerousInternalIoApi
public fun ChunkBuffer(
    buffer: ByteBuffer
): ChunkBuffer {
    return ChunkBuffer(Memory.of(buffer), null)
}

@DangerousInternalIoApi
public fun ChunkBuffer.resetFromContentToWrite(child: ByteBuffer) {
    resetForWrite(child.limit())
    commitWrittenUntilIndex(child.position())
}
