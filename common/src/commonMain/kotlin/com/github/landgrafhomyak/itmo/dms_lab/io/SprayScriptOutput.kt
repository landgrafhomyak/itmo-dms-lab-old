package com.github.landgrafhomyak.itmo.dms_lab.io

@Suppress("SpellCheckingInspection", "unused")
/**
 * Дублирует сообщения в несколько логгеров
 */
class SprayScriptOutput(private vararg val outputs: ScriptOutput) : ScriptOutput {
    override fun info(message: String) {
        for (output in this.outputs) {
            output.info(message)
        }
    }

    override fun warning(message: String) {
        for (output in this.outputs) {
            output.warning(message)
        }
    }

    override fun error(message: String) {
        for (output in this.outputs) {
            output.error(message)
        }
    }

    override fun request(level: UInt, request: String) {
        for (output in this.outputs) {
            output.request(level, request)
        }
    }

}