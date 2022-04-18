package com.github.landgrafhomyak.itmo.dms_lab.interactive.console

import com.github.landgrafhomyak.itmo.dms_lab.requests.BoundRequest
import com.github.landgrafhomyak.itmo.dms_lab.requests.BoundCommandFactory
import kotlin.jvm.JvmStatic

object NoOpStringCommandParser {
    private class EmptyExternCommand<T : BoundRequest>(private val constructor: () -> T) : BoundCommandFactory<T, String> {
        override fun build(arg: String): T = this.constructor.invoke()
    }

    private val origin = StringCommandParser(
        EmptyExternCommand { throw NotImplementedError() },
        EmptyExternCommand { throw NotImplementedError() }
    )

    @JvmStatic
    fun parse(raw: String) = this.origin.parse(raw)
}