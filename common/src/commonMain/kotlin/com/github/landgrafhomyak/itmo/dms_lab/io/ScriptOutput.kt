package com.github.landgrafhomyak.itmo.dms_lab.io

@Suppress("SpellCheckingInspection")
/**
 * Логгер
 */
interface ScriptOutput {
    /**
     * Передаёт информационное сообщение
     * @param message сообщение
     */
    fun info(message: String)

    /**
     * Передаёт предупреждение
     * @param message информация о предупреждении
     */
    fun warning(message: String)

    /**
     * Передаёт ошибку
     * @param message информация об ошибке
     */
    fun error(message: String)


    /**
     * Дублирует запрос в выходной поток, если он был передан в не связанном входном потоке
     * @param level уровень вложенности запроса
     * @param request строка с запросом
     */
    fun request(level: UInt, request: String)
}