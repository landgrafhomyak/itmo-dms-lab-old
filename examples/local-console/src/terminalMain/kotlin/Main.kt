@file:JvmName("Main")
@file:Suppress("OPT_IN_IS_NOT_ENABLED")
@file:OptIn(DelicateCoroutinesApi::class)

import com.github.landgrafhomyak.itmo.dms_lab.examples.local_console.Application
import com.github.landgrafhomyak.itmo.dms_lab.platform.coroutines.runCoro
import kotlin.jvm.JvmName
import kotlinx.coroutines.DelicateCoroutinesApi


fun main(argv: Array<String>) {
    runCoro {
        Application().mainloop()
    }
}