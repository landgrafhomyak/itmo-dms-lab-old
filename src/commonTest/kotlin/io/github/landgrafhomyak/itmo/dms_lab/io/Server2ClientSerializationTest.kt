@file:OptIn(ExperimentalUnsignedTypes::class)

package io.github.landgrafhomyak.itmo.dms_lab.io


import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Тестирует кодировку данных при передаче от сервера к клиенту
 * @see Client2ServerEncoder
 * @see Client2ServerDecoder
 */
@Suppress("unused")
internal class Server2ClientSerializationTest : AbstractSerializationTest() {
    override fun createEncoder(buffer: MutableList<UByteArray>): Encoder = Client2ServerEncoder(buffer)

    override fun createDecoder(raw: UByteArray): Decoder = Client2ServerDecoder(raw)
}