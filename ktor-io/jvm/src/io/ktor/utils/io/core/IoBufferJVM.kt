package io.ktor.utils.io.core

import io.ktor.utils.io.bits.*
import java.nio.*
import kotlin.contracts.*

public fun Buffer.readFully(dst: ByteBuffer, length: Int) {
    readExact(length, "buffer content") { memory, offset ->
        val limit = dst.limit()
        try {
            dst.limit(dst.position() + length)
            memory.copyTo(dst, offset)
        } finally {
            dst.limit(limit)
        }
    }
}

public fun Buffer.readAvailable(dst: ByteBuffer, length: Int = dst.remaining()): Int {
    if (!canRead()) return -1
    val size = minOf(readRemaining, length)
    readFully(dst, size)
    return size
}

public inline fun Buffer.readDirect(block: (ByteBuffer) -> Unit): Int {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    return read { memory, start, endExclusive ->
        val nioBuffer = memory.slice(start, endExclusive - start).buffer
        block(nioBuffer)
        check(nioBuffer.limit() == endExclusive - start) { "Buffer's limit change is not allowed" }

        nioBuffer.position()
    }
}

public inline fun Buffer.writeDirect(size: Int = 1, block: (ByteBuffer) -> Unit): Int {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    return write { memory, start, endExclusive ->
        val nioBuffer = memory.slice(start, endExclusive - start).buffer
        block(nioBuffer)
        check(nioBuffer.limit() == endExclusive - start) { "Buffer's limit change is not allowed" }

        nioBuffer.position()
    }
}
