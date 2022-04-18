package com.github.landgrafhomyak.itmo.dms_lab.examples.local_console

import com.github.landgrafhomyak.itmo.dms_lab.io.Logger
import com.github.landgrafhomyak.itmo.dms_lab.objects.LabWork
import com.github.landgrafhomyak.itmo.dms_lab.requests.BoundRequest
import com.github.landgrafhomyak.itmo.dms_lab.requests.UnexpectedRequestException

actual object Output : Logger {
    override suspend fun info(message: String) {
        println(message)
    }

    override suspend fun warning(message: String) {
        println("w: $message")
    }

    override suspend fun error(message: String) {
        println("e: $message")
    }

    override suspend fun request(level: UInt, request: BoundRequest) {
        try {
            println("${">".repeat(level.toInt())} ${StringifyHelpers.stringifyRequest(request)}")
        } catch (err: UnexpectedRequestException) {
            this.error(err.message)
        }
    }

    override suspend fun sendObject(obj: LabWork) {
        println("o: ${StringifyHelpers.stringifyLabWork(obj)}")
    }
}