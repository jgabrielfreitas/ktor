// ktlint-disable filename
package io.ktor.utils.io.core

import io.ktor.utils.io.core.internal.*

/**
 * This shouldn't be implemented directly. Inherit [AbstractOutput] instead.
 */
public actual interface Output : Appendable, Closeable {
    @Deprecated("Write with writeXXXLittleEndian or do X.reverseByteOrder() and then writeXXX instead.")
    public actual var byteOrder: ByteOrder

    public actual fun writeByte(v: Byte)

    public actual fun append(csq: CharArray, start: Int, end: Int): Appendable

    public actual fun flush()

    actual override fun close()
}
