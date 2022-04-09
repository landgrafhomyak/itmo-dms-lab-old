package com.github.landgrafhomyak.itmo.dms_lab.io

/**
 * Собирает запросы из разных источников в один поток
 *
 * Приоритет у первых потоков в списке
 * @param sources входные потоки для объединения
 */
@Suppress("unused")
class CollectScriptInput(private vararg val sources: ScriptInput) : BufferedScriptInput() {
    override fun getNextRequestRaw(): String? {
        for (source in this.sources) {
            if (source.hasNextRequest()) {
                return source.getNextRequest()
            }
        }
        return null
    }

    override fun onClosed(): Nothing {
        throw IllegalStateException("Все входные потоки закрыты, чтение команд невозможно")
    }

}