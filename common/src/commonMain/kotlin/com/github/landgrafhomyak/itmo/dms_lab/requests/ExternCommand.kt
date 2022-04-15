package com.github.landgrafhomyak.itmo.dms_lab.requests

/**
 * Отложенное создание конечного [объекта запроса][BoundRequest]
 */
interface ExternCommand<R : BoundRequest, A> {
    /**
     * Создаёт объект с переданным аргументом
     */
    fun build(arg: A): R
}