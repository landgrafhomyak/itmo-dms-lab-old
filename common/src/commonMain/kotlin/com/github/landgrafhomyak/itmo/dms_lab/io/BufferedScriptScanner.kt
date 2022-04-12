package com.github.landgrafhomyak.itmo.dms_lab.io


/**
 * Делает проверку на наличие очередного запроса путём его получения
 */
abstract class BufferedScriptScanner : ScriptScanner {
    //todo make synchronized
    @Suppress("VARIABLE_IN_SINGLETON_WITHOUT_THREAD_LOCAL")
    private var nextLine: String? = null

    override fun hasNextRequest(): Boolean {
        if (this.nextLine == null) {
            this.nextLine = this.getNextRequestRaw()
        }
        return this.nextLine != null
    }

    override fun getNextRequest(): String {
        if (!this.hasNextRequest()) {
            this.onClosed()
        }
        return this.nextLine!!.also {
            this.nextLine = null
        }
    }

    @Suppress("SpellCheckingInspection")
    /**
     * Функция обратного вызова для получения следующего запроса в питоновском стиле
     * @return строка с новым запросом или null, если поток закрыт
     */
    protected abstract fun getNextRequestRaw(): String?

    /**
     * Функция обратного вызова для пресечения доступа к закрытому потоку
     */
    protected abstract fun onClosed(): Nothing
}