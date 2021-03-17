/*
 * Copyright 2014-2021 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("EXTENSION_SHADOWED_BY_MEMBER")

package io.ktor.utils.io

import io.ktor.utils.io.bits.*
import io.ktor.utils.io.core.*

/**
 * Writes long number and suspends until written.
 * Crashes if channel get closed while writing.
 */
public actual suspend fun ByteWriteChannel.writeLong(l: Long) {
    check(this is ByteChannelSequentialBase)

    awaitAtLeastNBytesAvailableForWrite(8)
    writable.writeLong(reverseWrite({ l }, { l.reverseByteOrder() }))
    afterWrite(8)
}

/**
 * Writes int number and suspends until written.
 * Crashes if channel get closed while writing.
 */
public actual suspend fun ByteWriteChannel.writeInt(i: Int) {
    check(this is ByteChannelSequentialBase)

    awaitAtLeastNBytesAvailableForWrite(4)
    writable.writeInt(reverseWrite({ i }, { i.reverseByteOrder() }))
    afterWrite(4)
}

/**
 * Writes short number and suspends until written.
 * Crashes if channel get closed while writing.
 */
public actual suspend fun ByteWriteChannel.writeShort(s: Short) {
    check(this is ByteChannelSequentialBase)

    awaitAtLeastNBytesAvailableForWrite(2)
    writable.writeShort(reverseWrite({ s }, { s.reverseByteOrder() }))
    afterWrite(2)
}

/**
 * Writes byte and suspends until written.
 * Crashes if channel get closed while writing.
 */
public actual suspend fun ByteWriteChannel.writeByte(b: Byte) {
    check(this is ByteChannelSequentialBase)

    awaitAtLeastNBytesAvailableForWrite(1)
    writable.writeByte(b)
    afterWrite(1)
}

/**
 * Writes double number and suspends until written.
 * Crashes if channel get closed while writing.
 */
public actual suspend fun ByteWriteChannel.writeDouble(d: Double) {
    check(this is ByteChannelSequentialBase)

    awaitAtLeastNBytesAvailableForWrite(8)
    writable.writeDouble(reverseWrite({ d }, { d.reverseByteOrder() }))
    afterWrite(8)
}

/**
 * Writes float number and suspends until written.
 * Crashes if channel get closed while writing.
 */
public actual suspend fun ByteWriteChannel.writeFloat(f: Float) {
    check(this is ByteChannelSequentialBase)

    awaitAtLeastNBytesAvailableForWrite(4)
    writable.writeFloat(reverseWrite({ f }, { f.reverseByteOrder() }))
    afterWrite(4)
}
