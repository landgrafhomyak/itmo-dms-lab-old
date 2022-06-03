package io.github.landgrafhomyak.itmo.dms_lab.interop

import io.github.landgrafhomyak.itmo.dms_lab.io.Coloring
import io.github.landgrafhomyak.itmo.dms_lab.io.NoColoring
import kotlinx.cinterop.CPointer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.posix.FILE
import platform.posix.fclose
import platform.posix.fopen
import platform.posix.fputs

public actual class FileLogger(private var file: CPointer<FILE>?) : AbstractTextLogger() {
    public actual constructor(path: String) : this(fopen(path, "wt") ?: throw IllegalArgumentException("File \"$path\" not found"))

    public actual suspend fun close() {
        if (this.file == null) throw UnsupportedOperationException("File already closed")
        withContext(Dispatchers.Default) {
            fclose(this@FileLogger.file)
        }
        this.file = null
    }

    override suspend fun write(s: String) {
        withContext(Dispatchers.Default) {
            fputs(s, this@FileLogger.file ?: throw UnsupportedOperationException("Can't write to closed file"))
        }
    }

    override val coloring: Coloring
        get() = NoColoring
}