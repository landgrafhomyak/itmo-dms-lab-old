package com.github.landgrafhomyak.itmo.dms_lab.examples.local_console

import com.github.landgrafhomyak.itmo.dms_lab.requests.BoundCommandFactory
import com.github.landgrafhomyak.itmo.dms_lab.requests.Save

expect class SaveFactory() : BoundCommandFactory<Save, String>