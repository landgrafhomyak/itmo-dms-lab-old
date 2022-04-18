package com.github.landgrafhomyak.itmo.dms_lab.examples.local_console

import com.github.landgrafhomyak.itmo.dms_lab.interactive.console.StringCommandParser
import com.github.landgrafhomyak.itmo.dms_lab.io.ScriptSource
import com.github.landgrafhomyak.itmo.dms_lab.requests.BoundCommandFactory
import com.github.landgrafhomyak.itmo.dms_lab.requests.BoundRequest
import com.github.landgrafhomyak.itmo.dms_lab.requests.ExecuteScript
import com.github.landgrafhomyak.itmo.dms_lab.requests.Save

class TextScriptSource(
    private val rawRequests: Iterator<String>,
    saveClosure: BoundCommandFactory<Save, String>,
    executeClosure: BoundCommandFactory<ExecuteScript, String>
) : ScriptSource {
    private val parser = StringCommandParser(saveClosure, executeClosure)
    override suspend fun fetch(): BoundRequest? {
        if (!this.rawRequests.hasNext()) {
            return null
        }
        return this.parser.parse(this.rawRequests.next())
    }
}