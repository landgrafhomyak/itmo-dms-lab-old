import io.github.landgrafhomyak.itmo.dms_lab.interop.EqualsArgvParsed
import io.github.landgrafhomyak.itmo.dms_lab.v444588.console.Application
import kotlinx.coroutines.runBlocking

public fun main(argv: Array<String>): Unit = runBlocking {
    Application(EqualsArgvParsed(argv)).instantiateAndRun()
}