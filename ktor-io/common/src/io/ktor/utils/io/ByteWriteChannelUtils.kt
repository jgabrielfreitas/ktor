/*
 * Copyright 2014-2021 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */
package io.ktor.utils.io

/**
 * Writes long number and suspends until written.
 * Crashes if channel get closed while writing.
 */
public expect suspend fun ByteWriteChannel.writeLong(l: Long)

/**
 * Writes int number and suspends until written.
 * Crashes if channel get closed while writing.
 */
public expect suspend fun ByteWriteChannel.writeInt(i: Int)

/**
 * Writes short number and suspends until written.
 * Crashes if channel get closed while writing.
 */
public expect suspend fun ByteWriteChannel.writeShort(s: Short)

/**
 * Writes byte and suspends until written.
 * Crashes if channel get closed while writing.
 */
public expect suspend fun ByteWriteChannel.writeByte(b: Byte)

/**
 * Writes double number and suspends until written.
 * Crashes if channel get closed while writing.
 */
public expect suspend fun ByteWriteChannel.writeDouble(d: Double)

/**
 * Writes float number and suspends until written.
 * Crashes if channel get closed while writing.
 */
public expect suspend fun ByteWriteChannel.writeFloat(f: Float)
