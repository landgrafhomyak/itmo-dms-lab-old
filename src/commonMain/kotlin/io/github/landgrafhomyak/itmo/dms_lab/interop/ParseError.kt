package io.github.landgrafhomyak.itmo.dms_lab.interop

import kotlin.jvm.JvmStatic

/**
 * Ошибка, поднимаемая если нарушен формат входных данных при разборе запроса
 * @property pos позиция, на которой была обнаружена ошибка или `null`, если позицию вычислить не удаётся
 * @property message информация об синтаксической ошибке
 */
class ParseError(pos: Int?, override val message: String) : RuntimeException() {
    @Suppress("unused")
    var pos: Int? = pos
        private set

    companion object {
        /**
         * Выполняет заданный блок, и, если была поднята [эта][ParseError] ошибка, смещает позицию в ней на [offset]
         * @return возвращённое блоком значение, в случае успеха
         */
        @JvmStatic
        internal inline fun <T> shift(offset: Int, block: () -> T): T {
            try {
                return block()
            } catch (pe: ParseError) {
                pe.pos?.apply pos@{
                    pe.pos = this@pos + offset
                }
                throw pe
            }
        }
    }
}