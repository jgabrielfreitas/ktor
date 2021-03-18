/*
 * Copyright 2014-2021 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("EXTENSION_SHADOWED_BY_MEMBER")

package io.ktor.utils.io

import io.ktor.utils.io.bits.*
import io.ktor.utils.io.core.*

/**
 * Reads a long number (suspending if not enough bytes available) or fails if channel has been closed
 * and not enough bytes.
 */
public suspend fun ByteReadChannel.readLong(): Long {
    var result: Long
    read(8) { memory, startIndex, endIndex ->
        result = memory.loadLongAt(startIndex)
        8
    }

    return result
}

/**
 * Reads an int number (suspending if not enough bytes available) or fails if channel has been closed
 * and not enough bytes.
 */
public expect suspend fun ByteReadChannel.readInt(): Int

/**
 * Reads a short number (suspending if not enough bytes available) or fails if channel has been closed
 * and not enough bytes.
 */
public expect suspend fun ByteReadChannel.readShort(): Short

/**
 * Reads a byte (suspending if no bytes available yet) or fails if channel has been closed
 * and not enough bytes.
 */
public expect suspend fun ByteReadChannel.readByte(): Byte

/**
 * Reads a boolean value (suspending if no bytes available yet) or fails if channel has been closed
 * and not enough bytes.
 */
public expect suspend fun ByteReadChannel.readBoolean(): Boolean

/**
 * Reads double number (suspending if not enough bytes available) or fails if channel has been closed
 * and not enough bytes.
 */
public expect suspend fun ByteReadChannel.readDouble(): Double

/**
 * Reads float number (suspending if not enough bytes available) or fails if channel has been closed
 * and not enough bytes.
 */
public expect suspend fun ByteReadChannel.readFloat(): Float

internal suspend fun ByteChannelSequentialBase.readLongSlow(): Long {
    readNSlow(8) {
        return readable.readLong().reverseRead().also { afterRead(8) }
    }
}

internal suspend fun ByteChannelSequentialBase.readIntSlow(): Int {
    readNSlow(4) {
        return readable.readInt().reverseRead().also { afterRead(4) }
    }
}

internal suspend fun ByteChannelSequentialBase.readShortSlow(): Short {
    readNSlow(2) { return readable.readShort().reverseRead().also { afterRead(2) } }
}

internal suspend fun ByteChannelSequentialBase.readByteSlow(): Byte {
    do {
        awaitSuspend(1)

        if (readable.isNotEmpty) return readable.readByte().also { afterRead(1) }
        checkClosed(1)
    } while (true)
}

internal suspend fun ByteChannelSequentialBase.readBooleanSlow(): Boolean {
    awaitSuspend(1)
    checkClosed(1)
    return readBoolean()
}

internal suspend fun ByteChannelSequentialBase.readDoubleSlow(): Double {
    readNSlow(8) {
        return readable.readDouble().reverseRead().also { afterRead(8) }
    }
}

internal suspend fun ByteChannelSequentialBase.readFloatSlow(): Float {
    readNSlow(4) {
        return readable.readFloat().reverseRead().also { afterRead(4) }
    }
}
