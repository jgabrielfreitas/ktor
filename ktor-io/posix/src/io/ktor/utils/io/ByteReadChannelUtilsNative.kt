/*
 * Copyright 2014-2021 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("EXTENSION_SHADOWED_BY_MEMBER")

package io.ktor.utils.io

import io.ktor.utils.io.core.*

/**
 * Reads an int number (suspending if not enough bytes available) or fails if channel has been closed
 * and not enough bytes.
 */
public actual suspend fun ByteReadChannel.readInt(): Int {
    check(this is ByteChannelSequentialBase)

    return if (readable.hasBytes(4)) {
        readable.readInt().reverseRead().also { afterRead(4) }
    } else {
        readIntSlow()
    }
}

/**
 * Reads a short number (suspending if not enough bytes available) or fails if channel has been closed
 * and not enough bytes.
 */
public actual suspend fun ByteReadChannel.readShort(): Short {
    check(this is ByteChannelSequentialBase)

    return if (readable.hasBytes(2)) {
        readable.readShort().reverseRead().also { afterRead(2) }
    } else {
        readShortSlow()
    }
}

/**
 * Reads a byte (suspending if no bytes available yet) or fails if channel has been closed
 * and not enough bytes.
 */
public actual suspend fun ByteReadChannel.readByte(): Byte {
    check(this is ByteChannelSequentialBase)

    return if (readable.isNotEmpty) {
        readable.readByte().also { afterRead(1) }
    } else {
        readByteSlow()
    }
}

/**
 * Reads a boolean value (suspending if no bytes available yet) or fails if channel has been closed
 * and not enough bytes.
 */
public actual suspend fun ByteReadChannel.readBoolean(): Boolean {
    check(this is ByteChannelSequentialBase)

    return if (readable.canRead()) {
        (readable.readByte() == 1.toByte()).also { afterRead(1) }
    } else {
        readBooleanSlow()
    }
}

/**
 * Reads double number (suspending if not enough bytes available) or fails if channel has been closed
 * and not enough bytes.
 */
public actual suspend fun ByteReadChannel.readDouble(): Double {
    check(this is ByteChannelSequentialBase)

    return if (readable.hasBytes(8)) {
        readable.readDouble().reverseRead().also { afterRead(8) }
    } else {
        readDoubleSlow()
    }
}

/**
 * Reads float number (suspending if not enough bytes available) or fails if channel has been closed
 * and not enough bytes.
 */
public actual suspend fun ByteReadChannel.readFloat(): Float {
    check(this is ByteChannelSequentialBase)

    return if (readable.hasBytes(4)) {
        readable.readFloat().reverseRead().also { afterRead(4) }
    } else {
        readFloatSlow()
    }
}
