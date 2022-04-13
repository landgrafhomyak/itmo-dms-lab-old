package com.github.landgrafhomyak.itmo.dms_lab.interactive.console


class ParseError internal constructor(pos: Int, override val message: String) : RuntimeException() {
    @Suppress("unused")
    var pos: Int = pos
        internal set
}