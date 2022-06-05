package io.github.landgrafhomyak.itmo.dms_lab.lifecycle

import io.github.landgrafhomyak.itmo.dms_lab.interop.Logger
import io.github.landgrafhomyak.itmo.dms_lab.io.RequestTransmitter
import io.github.landgrafhomyak.itmo.dms_lab.requests.BoundRequest
import io.github.landgrafhomyak.itmo.dms_lab.requests.ExitRequestMeta
import io.github.landgrafhomyak.itmo.dms_lab.requests.HelpRequestMeta
import io.github.landgrafhomyak.itmo.dms_lab.requests.RequestMeta
import kotlinx.coroutines.sync.Mutex

public abstract class RequestsConsoleParser<R : BoundRequest<*, *>>(
    private val transmitter: RequestTransmitter<R>,
    private val logger: Logger,
    private val readLine: () -> String
) {
    private val commandsFactories: Map<RequestMeta, BoundRequestFactory<R>>

    @Suppress("MemberVisibilityCanBePrivate")
    public val commands: Iterable<RequestMeta>
        get() = this.commandsFactories.keys


    protected inner class BoundRequestFactory<R : BoundRequest<*, *>> internal constructor(private val bindFun: suspend (String?) -> R?) {
        internal suspend fun bind(inlineExtraData: String?): R? = this.bindFun(inlineExtraData)
    }

    private val exitFactory = BoundRequestFactory<R> { null }
    private val helpFactory = BoundRequestFactory<R> {
        for (m in this@RequestsConsoleParser.commands) {
            if (m.consoleName != null)
                this@RequestsConsoleParser.logger.info("${m.consoleName}")
        }
        return@BoundRequestFactory null
    }

    protected inner class InitializerContext(private val mapBuilder: MutableMap<RequestMeta, BoundRequestFactory<R>>) {
        public operator fun RequestMeta.invoke(factory: suspend (String?) -> R?) {
            when (this@invoke.consoleName) {
                "help" -> throw IllegalArgumentException("Use function .useHelp() instead")
                "exit" -> throw IllegalArgumentException("Use function .useExit() instead")
            }
            this@InitializerContext.mapBuilder[this@invoke] = BoundRequestFactory(factory)
        }

        public fun useHelp() {
            this@InitializerContext.mapBuilder[HelpRequestMeta] = this@RequestsConsoleParser.helpFactory
        }

        public fun useExit() {
            this@InitializerContext.mapBuilder[ExitRequestMeta] = this@RequestsConsoleParser.exitFactory
        }
    }

    protected abstract fun InitializerContext.initRequestsFactories()

    init {
        this.commandsFactories = buildMap map@{
            InitializerContext(this@map).initRequestsFactories()
        }
    }

    private val mutex = Mutex()

    @Suppress("MemberVisibilityCanBePrivate")
    public var isRunning: Boolean = false
        private set

    /**
     * Запускает перенаправление [запросов][BoundRequest]
     */
    public suspend fun run() {
        if (this.isRunning) throw IllegalStateException("Parser already running, shutdown it first")
        this.mutex.lock()
        this.isRunning = true
        try {
            while (this.isRunning) {
                this.logger.debug("Получение команды")
                val raw = this.readLine()
                val reqId: String
                val reqInlineExtraData: String?
                raw.split(' ', limit = 1).apply raw@{
                    reqId = this@raw.first()
                    reqInlineExtraData = this@raw.getOrNull(1)
                }
                this.logger.debug("Получена команда '$reqId' с однострочными данными: $reqInlineExtraData")

                for ((m, f) in this.commandsFactories) {
                    if (reqId == m.consoleName) {
                        this.logger.debug("Команда '$reqId' опознана")
                        try {
                            val req = f.bind(reqInlineExtraData)
                            if (req == null) {
                                this.logger.debug("Команда '$reqId' выполнена на стороне клиента")
                                continue
                            }
                            this.logger.debug("Команда '$reqId' успешно создана")
                            this.transmitter.send(req)
                            this.logger.debug("Команда '$reqId' успешно отослана на сервер")
                        } catch (e: Throwable) {
                            this.logger.error(e.stackTraceToString())
                        }

                    }
                }
            }
        } finally {
            this.isRunning = false
            this.mutex.unlock()
        }
    }

    /**
     * Останавливает перенаправление [запросов][BoundRequest]
     */
    public suspend fun shutdown() {
        this.isRunning = false
        this.mutex.lock()
        this.mutex.unlock()
    }
}