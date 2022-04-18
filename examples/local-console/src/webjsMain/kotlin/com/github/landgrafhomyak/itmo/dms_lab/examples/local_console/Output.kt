package com.github.landgrafhomyak.itmo.dms_lab.examples.local_console

import com.github.landgrafhomyak.itmo.dms_lab.io.Logger
import com.github.landgrafhomyak.itmo.dms_lab.objects.LabWork
import com.github.landgrafhomyak.itmo.dms_lab.requests.BoundRequest

actual object Output : Logger {
    override suspend fun info(message: String) {
        TODO("Not yet implemented")
    }

    override suspend fun warning(message: String) {
        TODO("Not yet implemented")
    }

    override suspend fun error(message: String) {
        TODO("Not yet implemented")
    }

    override suspend fun request(level: UInt, request: BoundRequest) {
        TODO("Not yet implemented")
    }

    override suspend fun sendObject(obj: LabWork) {
        TODO("Not yet implemented")
    }
}