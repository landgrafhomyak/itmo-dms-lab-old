package io.github.landgrafhomyak.itmo.dms_lab.interop

import io.github.landgrafhomyak.itmo.dms_lab.io.Coloring
import io.github.landgrafhomyak.itmo.dms_lab.io.RequestOutputAccessor
import io.github.landgrafhomyak.itmo.dms_lab.io.RequestOutputEntity
import io.github.landgrafhomyak.itmo.dms_lab.io.RequestOutputMessageType

public interface RequestOutputPrinter {
    public suspend fun print(list: RequestOutputAccessor)
}

public class RequestOutputConsolePrinter(private val coloring: Coloring) : RequestOutputPrinter {
    private inline fun colored(t: RequestOutputMessageType, s: String): String = when (t) {
        RequestOutputMessageType.INFO    -> s
        RequestOutputMessageType.WARNING -> "${this.coloring.color(Coloring.Color.YELLOW)}$s${this.coloring.reset()}"
        RequestOutputMessageType.ERROR   -> "${this.coloring.color(Coloring.Color.RED)}$s${this.coloring.reset()}"
    }

    override suspend fun print(list: RequestOutputAccessor) {
        val stack = mutableListOf<Iterator<RequestOutputEntity>>()
        for ((type, row) in list) {
            when (row) {
                is RequestOutputEntity.Str    -> println(this.colored(type, row.value))
                is RequestOutputEntity.Bool   -> println(this.colored(type, row.value.toString()))
                is RequestOutputEntity.Enum   -> println(this.colored(type, row.value))
                is RequestOutputEntity.Null   -> println(this.colored(type, row.value.toString()))
                is RequestOutputEntity.Number -> println(this.colored(type, row.value))
                is RequestOutputEntity.Dbl    -> println(this.colored(type, row.value))
                is RequestOutputEntity.Struct -> {
                    stack.add(row.value.iterator())
                    while (stack.isNotEmpty()) {
                        if (!stack.last().hasNext()) {
                            stack.removeLast()
                            continue
                        }

                        when (val e = stack.last().next()) {
                            is RequestOutputEntity.Bool   -> println(this.colored(type, "${" ".repeat(stack.size)}${e.fieldName} = ${e.value}"))
                            is RequestOutputEntity.Dbl    -> println(this.colored(type, "${" ".repeat(stack.size)}${e.fieldName} = ${e.value}"))
                            is RequestOutputEntity.Enum   -> println(this.colored(type, "${" ".repeat(stack.size)}${e.fieldName} = ${e.value}"))
                            is RequestOutputEntity.Null   -> println(this.colored(type, "${" ".repeat(stack.size)}${e.fieldName} = ${e.value}"))
                            is RequestOutputEntity.Number -> println(this.colored(type, "${" ".repeat(stack.size)}${e.fieldName} = ${e.value}"))
                            is RequestOutputEntity.Str    -> println(this.colored(type, "${" ".repeat(stack.size)}${e.fieldName} = ${e.value}"))
                            is RequestOutputEntity.Struct -> {
                                println(this.colored(type, "${" ".repeat(stack.size)}${e.fieldName} ="))
                                stack.add(e.value.iterator())
                            }
                        }
                    }
                }
            }
        }
    }

}