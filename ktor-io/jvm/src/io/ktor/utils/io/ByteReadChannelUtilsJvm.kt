/*
 * Copyright 2014-2021 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("EXTENSION_SHADOWED_BY_MEMBER")

package io.ktor.utils.io

import java.lang.Double.*
import java.lang.Float.*
import java.nio.*

/**
 * Reads an int number (suspending if not enough bytes available) or fails if channel has been closed
 * and not enough bytes.
 */
public actual suspend fun ByteReadChannel.readInt(): Int {
    check(this is ByteBufferChannel)
    return readPrimitive(4, ByteBuffer::getInt)
}

/**
 * Reads a short number (suspending if not enough bytes available) or fails if channel has been closed
 * and not enough bytes.
 */
public actual suspend fun ByteReadChannel.readShort(): Short {
    check(this is ByteBufferChannel)
    return readPrimitive(2, ByteBuffer::getShort)
}

/**
 * Reads a byte (suspending if no bytes available yet) or fails if channel has been closed
 * and not enough bytes.
 */
public actual suspend fun ByteReadChannel.readByte(): Byte {
    check(this is ByteBufferChannel)
    return readPrimitive(1, ByteBuffer::get)
}

/**
 * Reads a boolean value (suspending if no bytes available yet) or fails if channel has been closed
 * and not enough bytes.
 */
public actual suspend fun ByteReadChannel.readBoolean(): Boolean = readByte() != 0.toByte()

/**
 * Reads double number (suspending if not enough bytes available) or fails if channel has been closed
 * and not enough bytes.
 */
public actual suspend fun ByteReadChannel.readDouble(): Double = longBitsToDouble(readLong())

/**
 * Reads float number (suspending if not enough bytes available) or fails if channel has been closed
 * and not enough bytes.
 */
public actual suspend fun ByteReadChannel.readFloat(): Float = intBitsToFloat(readInt())
