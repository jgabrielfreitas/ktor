/*
 * Copyright 2014-2021 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("EXTENSION_SHADOWED_BY_MEMBER")

package io.ktor.utils.io

import java.lang.Double.*
import java.lang.Float.*

/**
 * Writes long number and suspends until written.
 * Crashes if channel get closed while writing.
 */
public actual suspend fun ByteWriteChannel.writeLong(l: Long) {
    check(this is ByteBufferChannel)
    writePrimitive(8, { writeLong(l) }, { putLong(l) })
}

/**
 * Writes int number and suspends until written.
 * Crashes if channel get closed while writing.
 */
public actual suspend fun ByteWriteChannel.writeInt(i: Int) {
    check(this is ByteBufferChannel)
    writePrimitive(4, { writeInt(i) }, { putInt(i) })
}

/**
 * Writes short number and suspends until written.
 * Crashes if channel get closed while writing.
 */
public actual suspend fun ByteWriteChannel.writeShort(s: Short) {
    check(this is ByteBufferChannel)
    writePrimitive(2, { writeShort(s) }, { putShort(s) })
}

/**
 * Writes byte and suspends until written.
 * Crashes if channel get closed while writing.
 */
public actual suspend fun ByteWriteChannel.writeByte(b: Byte) {
    check(this is ByteBufferChannel)
    writePrimitive(1, { writeByte(b) }, { put(b) })
}

/**
 * Writes double number and suspends until written.
 * Crashes if channel get closed while writing.
 */
public actual suspend fun ByteWriteChannel.writeDouble(d: Double) {
    writeLong(doubleToRawLongBits(d))
}

/**
 * Writes float number and suspends until written.
 * Crashes if channel get closed while writing.
 */
public actual suspend fun ByteWriteChannel.writeFloat(f: Float) {
    writeInt(floatToRawIntBits(f))
}
