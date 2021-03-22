// ktlint-disable filename
package io.ktor.utils.io.core

/**
 * This shouldn't be implemented directly. Inherit [AbstractOutput] instead.
 */
public actual interface Output : Appendable, Closeable {

    public actual fun writeByte(v: Byte)

    public actual fun append(csq: CharArray, start: Int, end: Int): Appendable

    public actual fun flush()

    actual override fun close()
}
