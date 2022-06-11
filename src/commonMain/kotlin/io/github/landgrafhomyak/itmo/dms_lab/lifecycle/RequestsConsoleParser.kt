package io.github.landgrafhomyak.itmo.dms_lab.lifecycle

import io.github.landgrafhomyak.itmo.dms_lab.interop.ConsoleInputDecoder
import io.github.landgrafhomyak.itmo.dms_lab.interop.InLineObjectDecoder
import io.github.landgrafhomyak.itmo.dms_lab.interop.Logger
import io.github.landgrafhomyak.itmo.dms_lab.interop.RequestOutputPrinter
import io.github.landgrafhomyak.itmo.dms_lab.io.RequestTransmitter
import io.github.landgrafhomyak.itmo.dms_lab.requests.BoundRequest
import io.github.landgrafhomyak.itmo.dms_lab.requests.ExitRequestMeta
import io.github.landgrafhomyak.itmo.dms_lab.requests.HelpRequestMeta
import io.github.landgrafhomyak.itmo.dms_lab.requests.RequestMeta
import kotlinx.coroutines.sync.Mutex
import kotlinx.serialization.encoding.Decoder
import kotlin.coroutines.cancellation.CancellationException

public abstract class RequestsConsoleParser<R : BoundRequest<*, *>>(
    private val transmitter: RequestTransmitter<R>,
    private val logger: Logger,
    private val readLine: () -> String,
    private val triesCount: UInt = 1u,
    private val printer: RequestOutputPrinter
) {
    private val commandsFactories: Map<RequestMeta, BoundRequestFactory<R>>

    @Suppress("MemberVisibilityCanBePrivate")
    public val commands: Iterable<RequestMeta>
        get() = this.commandsFactories.keys


    protected inner class BoundRequestFactory<R : BoundRequest<*, *>> internal constructor(private val bindFun: suspend (Decoder) -> R?) {
        internal suspend fun bind(inlineExtraData: Decoder): R? = this.bindFun(inlineExtraData)
    }

    private val exitFactory = BoundRequestFactory<R> {
        throw ExitSignal()
    }
    private val helpFactory = BoundRequestFactory<R> {
        for (m in this@RequestsConsoleParser.commands) {
            if (m.consoleName != null)
                this@RequestsConsoleParser.logger.info("${m.consoleName} - ${m.description}")
        }
        return@BoundRequestFactory null
    }

    protected inner class InitializerContext(private val mapBuilder: MutableMap<RequestMeta, BoundRequestFactory<R>>) {
        public operator fun RequestMeta.invoke(factory: suspend (Decoder) -> R?) {
            when (this@invoke.consoleName) {
                "help" -> throw IllegalArgumentException("Use function .useLocalHelp() instead")
                "exit" -> throw IllegalArgumentException("Use function .useLocalExit() instead")
            }
            this@InitializerContext.mapBuilder[this@invoke] = BoundRequestFactory(factory)
        }

        public fun useLocalHelp() {
            this@InitializerContext.mapBuilder[HelpRequestMeta] = this@RequestsConsoleParser.helpFactory
        }

        public fun useLocalExit() {
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

    // @Suppress("MemberVisibilityCanBePrivate")
    // public var isRunning: Boolean = false
    //     private set

    /**
     * Запускает перенаправление [запросов][BoundRequest]
     */
    public suspend fun run() {
        @Suppress("SpellCheckingInspection")
        this.logger.info("Запуск парсера `${this::class.simpleName}`...")
        // if (this.isRunning) throw IllegalStateException("Parser already running, shutdown it first")
        if (!this.mutex.tryLock()) {
            @Suppress("SpellCheckingInspection")
            this.logger.fatal("Парсер `${this::class.simpleName}` уже был запущен ранее")
            throw IllegalStateException("Parser already running, shutdown it first")
        }
        // this.isRunning = true
        try {
            @Suppress("SpellCheckingInspection")
            this.logger.info("Парсер `${this::class.simpleName}` успешно запущен")
            reading@ while (true) {

                this.logger.debug("Получение команды...")
                val raw = this.readLine()
                val reqId: String
                val reqInlineExtraData: String?
                raw.split(' ', limit = 2).apply raw@{
                    reqId = this@raw.first()
                    reqInlineExtraData = this@raw.getOrNull(1)
                }
                this.logger.debug("Получена команда '$reqId' с однострочными данными: $reqInlineExtraData")

                val decoder =
                    if (reqInlineExtraData == null) ConsoleInputDecoder(this.triesCount)
                    else InLineObjectDecoder(reqInlineExtraData)


                for ((m, f) in this.commandsFactories) {
                    if (reqId == m.consoleName) {
                        this.logger.debug("Команда '$reqId' опознана, попытка прочитать аргументы и создать объект...")
                        try {
                            val req = f.bind(decoder)
                            if (req == null) {
                                this.logger.debug("Команда '$reqId' выполнена на стороне клиента")
                                continue@reading
                            }
                            this.logger.debug("Объект команды '$reqId' успешно создан, пересылка на сервер...")
                            this.printer.print(this.transmitter.send(req))
                            this.logger.debug("Команда '$reqId' успешно отослана на сервер, получен ответ")
                        } catch (s: ExitSignal) {
                            this.logger.debug("Команда '$reqId' передаёт сигнал остановки")
                            throw s
                        } catch (e: CancellationException) {
                            @Suppress("SpellCheckingInspection")
                            this.logger.debug("Выполнение команды `$reqId` остановлено отменой корутины")
                            throw e
                        } catch (e: Throwable) {
                            this.logger.error("Команда '$reqId' выдаёт ошибку:  \t\n${e.stackTraceToString()}")
                        }
                        continue@reading
                    }
                }
                this.logger.error("Неизвестная команда: $reqId")
            }
        } catch (_: ExitSignal) {
            @Suppress("SpellCheckingInspection")
            this.logger.debug("Получен сигнал остановки в парсере `${this::class.simpleName}`")
        } catch (e: CancellationException) {
            @Suppress("SpellCheckingInspection")
            this.logger.debug("Парсер `${this::class.simpleName}` остановлен отменой корутины")
            throw e
        } catch (e: Throwable) {
            @Suppress("SpellCheckingInspection")
            this.logger.fatal("Парсер `${this::class.simpleName}` прерван ошибкой: \t\n${e.stackTraceToString()}")
            throw e
        } finally {
            // this.isRunning = false
            this.mutex.unlock()
            @Suppress("SpellCheckingInspection")
            this.logger.info("Парсер `${this::class.simpleName}` остановлен")
        }
    }
}