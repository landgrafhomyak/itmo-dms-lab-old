package com.github.landgrafhomyak.itmo.dms_lab.io

public actual class FileLogger actual constructor(path: String) : AbstractTextLogger() {
    init {
        TODO("Not yet implemented")
    }

    public actual suspend fun close() {
        TODO("Not yet implemented")
    }

    override suspend fun write(s: String) {
        TODO("Not yet implemented")
    }

    override val coloring: Coloring
        get() = NoColoring
}
