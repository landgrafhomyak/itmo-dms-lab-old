package com.github.landgrafhomyak.itmo.dms_lab.examples.local_console

import com.github.landgrafhomyak.itmo.dms_lab.collections.ArrayStack
import com.github.landgrafhomyak.itmo.dms_lab.io.Logger
import com.github.landgrafhomyak.itmo.dms_lab.io.ScriptSource
import com.github.landgrafhomyak.itmo.dms_lab.objects.LabWorksCollection
import com.github.landgrafhomyak.itmo.dms_lab.requests.ApplicableToCollection
import com.github.landgrafhomyak.itmo.dms_lab.requests.Empty
import com.github.landgrafhomyak.itmo.dms_lab.requests.ExecuteScript
import com.github.landgrafhomyak.itmo.dms_lab.requests.Exit
import com.github.landgrafhomyak.itmo.dms_lab.requests.Help
import com.github.landgrafhomyak.itmo.dms_lab.requests.History
import com.github.landgrafhomyak.itmo.dms_lab.requests.UnexpectedRequestException

class Application(
    @Suppress("MemberVisibilityCanBePrivate")
    val logger: Logger = Output,
    @Suppress("MemberVisibilityCanBePrivate")
    val collection: LabWorksCollection = LabWorksCollection()
) {
    private val stack = ArrayStack<ScriptSource>().apply {
        add(TextScriptSource(StdinRawRequests, SaveFactory(), ExecuteScriptFactory()))
    }

    suspend fun mainloop() {
        stack@ while (stack.isNotEmpty()) {
            fetching@ while (true) {
                val request = this.stack.top.fetch() ?: break@fetching
                if (stack.size > 1) {
                    this.logger.request(stack.size.toUInt(), request)
                }
                when (request) {
                    is Empty                  -> {}
                    is ApplicableToCollection -> request.applyTo(this.logger, this.collection)
                    is Exit                   -> break@fetching
                    is ExecuteScript          -> {
                        this.stack.add(request.factory.build())
                        continue@stack
                    }
                    is Help                   -> TODO()
                    is History                -> TODO()
                    else                      -> this.logger.error(UnexpectedRequestException(request.meta.id).message)
                }
            }
        }
    }
}
