package com.github.landgrafhomyak.itmo.dms_lab.examples.local_console

import com.github.landgrafhomyak.itmo.dms_lab.requests.ExecuteScript
import com.github.landgrafhomyak.itmo.dms_lab.requests.BoundCommandFactory

expect class ExecuteScriptFactory() : BoundCommandFactory<ExecuteScript, String>