package io.ktor.utils.io.core

import org.khronos.webgl.*

public fun Input.readFully(dst: Int8Array, offset: Int = 0, length: Int = dst.length - offset) {
    return readFully(dst as ArrayBufferView, offset, length)
}

public fun Input.readFully(dst: ArrayBuffer, offset: Int = 0, length: Int = dst.byteLength - offset) {
    if (remaining < length) throw IllegalArgumentException(
        "Not enough bytes available ($remaining) to read $length bytes"
    )
    var copied = 0

    takeWhile { buffer: Buffer ->
        val rc = buffer.readAvailable(dst, offset + copied, length - copied)
        if (rc > 0) copied += rc
        copied < length
    }
}

public fun Input.readFully(dst: ArrayBufferView, offset: Int = 0, length: Int = dst.byteLength - offset) {
    require(length <= dst.byteLength) {
        throw IndexOutOfBoundsException("length $length is greater than view size ${dst.byteLength}")
    }

    return readFully(dst.buffer, dst.byteOffset + offset, length)
}

public fun Input.readAvailable(dst: Int8Array, offset: Int = 0, length: Int = dst.length - offset): Int {
    val remaining = remaining
    if (remaining == 0L) return -1
    val size = minOf(remaining, length.toLong()).toInt()
    readFully(dst, offset, size)
    return size
}

internal fun Input.readAvailable(dst: ArrayBuffer, offset: Int, length: Int): Int {
    val remaining = remaining
    if (remaining == 0L) return -1
    val size = minOf(remaining, length.toLong()).toInt()
    readFully(dst, offset, size)
    return size
}

internal fun Input.readAvailable(dst: ArrayBufferView, offset: Int, length: Int): Int {
    val remaining = remaining
    if (remaining == 0L) return -1
    val size = minOf(remaining, length.toLong()).toInt()
    readFully(dst, offset, size)
    return size
}
