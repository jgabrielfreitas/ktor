/*
 * Copyright 2014-2021 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */
package io.ktor.utils.io

import io.ktor.utils.io.bits.*

/**
 * Writes long number and suspends until written.
 * Crashes if channel get closed while writing.
 */
public suspend fun ByteWriteChannel.writeLong(l: Long) {
    write(8) { memory, start, _ ->
        memory.storeLongAt(start, l)
        8
    }
}

/**
 * Writes int number and suspends until written.
 * Crashes if channel get closed while writing.
 */
public suspend fun ByteWriteChannel.writeInt(i: Int) {
    write(4) { memory, start, _ ->
        memory.storeIntAt(start, i)
        4
    }
}

/**
 * Writes short number and suspends until written.
 * Crashes if channel get closed while writing.
 */
public suspend fun ByteWriteChannel.writeShort(s: Short) {
    write(2) { memory, start, _ ->
        memory.storeShortAt(start, s)
        2
    }
}

/**
 * Writes byte and suspends until written.
 * Crashes if channel get closed while writing.
 */
public suspend fun ByteWriteChannel.writeByte(b: Byte) {
    write(1) { memory, start, _ ->
        memory.storeAt(start, b)
        1
    }
}

/**
 * Writes double number and suspends until written.
 * Crashes if channel get closed while writing.
 */
public suspend fun ByteWriteChannel.writeDouble(d: Double) {
    write(8) { memory, start, _ ->
        memory.storeDoubleAt(start, d)
        8
    }
}

/**
 * Writes float number and suspends until written.
 * Crashes if channel get closed while writing.
 */
public suspend fun ByteWriteChannel.writeFloat(f: Float) {
    write(4) { memory, start, _ ->
        memory.storeFloatAt(start, f)
        4
    }
}
