package io.github.landgrafhomyak.itmo.dms_lab.v444588.console

import io.github.landgrafhomyak.itmo.dms_lab.interop.ArgvParsed

public class Application(remoteHost: String? = null, public val debugMode: Boolean = false) {
    public constructor(argv: ArgvParsed) : this()

    public suspend fun run() {

    }
}