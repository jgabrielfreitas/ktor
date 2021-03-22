/*
 * Copyright 2014-2021 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */
package io.ktor.utils.io

import java.lang.Double.*
import java.lang.Float.*

/**
 * Writes long number and suspends until written.
 * Crashes if channel get closed while writing.
 */
public actual suspend fun ByteWriteChannel.writeLong(l: Long) {
    check(this is ByteBufferChannel)
    write(8) { it.putLong(l) }
}

/**
 * Writes int number and suspends until written.
 * Crashes if channel get closed while writing.
 */
public actual suspend fun ByteWriteChannel.writeInt(i: Int) {
    write(4) { it.putInt(i) }
}

/**
 * Writes short number and suspends until written.
 * Crashes if channel get closed while writing.
 */
public actual suspend fun ByteWriteChannel.writeShort(s: Short) {
    write(2) { it.putShort(s) }
}

/**
 * Writes byte and suspends until written.
 * Crashes if channel get closed while writing.
 */
public actual suspend fun ByteWriteChannel.writeByte(b: Byte) {
    write(1) { it.put(b) }
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
