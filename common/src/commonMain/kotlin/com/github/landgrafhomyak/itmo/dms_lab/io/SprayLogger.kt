package com.github.landgrafhomyak.itmo.dms_lab.io

import com.github.landgrafhomyak.itmo.dms_lab.commands.BoundCommand
import com.github.landgrafhomyak.itmo.dms_lab.objects.LabWork

@Suppress("SpellCheckingInspection", "unused")
/**
 * Дублирует сообщения в несколько логгеров
 */
class SprayLogger(private vararg val outputs: Logger) : Logger {
    override suspend fun info(message: String) {
        for (output in this.outputs) {
            output.info(message)
        }
    }

    override suspend fun warning(message: String) {
        for (output in this.outputs) {
            output.warning(message)
        }
    }

    override suspend fun error(message: String) {
        for (output in this.outputs) {
            output.error(message)
        }
    }

    override suspend fun request(level: UInt, request: BoundCommand) {
        for (output in this.outputs) {
            output.request(level, request)
        }
    }

    override suspend fun sendObject(obj: LabWork) {
        for (output in this.outputs) {
            output.sendObject(obj)
        }
    }

}