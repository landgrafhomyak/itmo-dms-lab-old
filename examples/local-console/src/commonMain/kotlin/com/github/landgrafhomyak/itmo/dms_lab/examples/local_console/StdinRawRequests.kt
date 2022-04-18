package com.github.landgrafhomyak.itmo.dms_lab.examples.local_console

object StdinRawRequests : Iterator<String> {
    @Suppress("VARIABLE_IN_SINGLETON_WITHOUT_THREAD_LOCAL")
    private var lastLine: String? = null

    override fun hasNext(): Boolean {
        if (this.lastLine != null) return true
        this.lastLine = readlnOrNull()
        return this.lastLine != null
    }

    override fun next(): String {
        if (!this.hasNext())
            throw IllegalStateException("Запросы в стандартном потоке ввода закончились")

        return this.lastLine!!.also { this.lastLine = null }
    }
}