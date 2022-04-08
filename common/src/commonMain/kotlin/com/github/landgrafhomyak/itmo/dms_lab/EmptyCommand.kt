package com.github.landgrafhomyak.itmo.dms_lab

/**
 * Объект заглушка для случаев когда команда отсутствует, но синтаксис не нарушен
 * (например, введён пустой запрос)
 */
object EmptyCommand : BundledCommand(CommandTypes.EMPTY)