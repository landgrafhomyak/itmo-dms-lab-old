package com.github.landgrafhomyak.itmo.dms_lab.io

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.OutputStream

public actual class FileLogger(private val outputStream: OutputStream) : AbstractTextLogger() {
    public actual constructor(path: String) : this(File(path).outputStream())

    override suspend fun write(s: String) {
        withContext(Dispatchers.IO) {
            this@FileLogger.outputStream.write(s.toByteArray(Charsets.UTF_8))
        }
    }

    override val coloring: Coloring
        get() = NoColoring

    public actual suspend fun close() {}
}