package io.github.landgrafhomyak.itmo.dms_lab.interop

import io.github.landgrafhomyak.itmo.dms_lab.io.Coloring
import kotlinx.datetime.Clock
import kotlinx.serialization.KSerializer
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Абстрактный класс для генерации текстовых логов
 * @see TextViewEncoder
 */
public abstract class AbstractTextLogger : Logger {
    /**
     * Функция записывающая [стоку][String] в поток
     */
    protected abstract suspend fun write(s: String)

    /**
     * Вспомогательный класс для [AbstractTextLogger.buildWrite]
     */
    private inner class ColoredBuilder : Coloring {
        private val builder = StringBuilder()

        override fun reset(): String =
            this@AbstractTextLogger.coloring.reset().also(this@ColoredBuilder.builder::append)

        override fun color(color: Coloring.Color): String =
            this@AbstractTextLogger.coloring.color(color).also(this@ColoredBuilder.builder::append)

        override fun decorate(vararg decorations: Coloring.Decoration): String =
            this@AbstractTextLogger.coloring.decorate(*decorations).also(this@ColoredBuilder.builder::append)

        fun write(s: String) {
            this@ColoredBuilder.builder.append(s)
        }

        fun exportString() = this.builder.toString()
    }

    /**
     * Собирает [строку][String], которая будет передана в [AbstractTextLogger.write]
     */
    @OptIn(ExperimentalContracts::class)
    private suspend inline fun buildWrite(builder: AbstractTextLogger.ColoredBuilder.() -> Unit) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }
        val b = this.ColoredBuilder()
        b.apply(builder)
        this.write(b.exportString())
    }

    /**
     * [Генератор цветов][Coloring] используемый этим [логгером][AbstractTextLogger]
     */
    @Suppress("SpellCheckingInspection")
    protected abstract val coloring: Coloring

    override suspend fun debug(message: String): Unit = this.buildWrite {
        write(Clock.System.now().toString())
        color(Coloring.Color.GREY)
        write(" [DEBUG] ")
        reset()
        write("$message\n")
    }

    override suspend fun <T> debug(obj: T, serializer: KSerializer<T>): Unit = this.buildWrite {
        write(Clock.System.now().toString())
        color(Coloring.Color.GREY)
        write(" [DEBUG] ")
        reset()
        val builder = StringBuilder()
        serializer.serialize(TextViewEncoder(builder, 1u), obj)
        write(builder.toString())
    }

    override suspend fun info(message: String): Unit = this.buildWrite {
        write(Clock.System.now().toString())
        color(Coloring.Color.BLUE)
        write(" [INFO] ")
        reset()
        write("$message\n")
    }

    override suspend fun <T> info(obj: T, serializer: KSerializer<T>): Unit = this.buildWrite {
        write(Clock.System.now().toString())
        color(Coloring.Color.BLUE)
        write(" [INFO] ")
        reset()
        val builder = StringBuilder()
        serializer.serialize(TextViewEncoder(builder, 1u), obj)
        write(builder.toString())
    }

    override suspend fun warning(message: String): Unit = this.buildWrite {
        write(Clock.System.now().toString())
        color(Coloring.Color.YELLOW)
        write(" [WARNING] ")
        reset()
        write("$message\n")
    }

    override suspend fun <T> warning(obj: T, serializer: KSerializer<T>): Unit = this.buildWrite {
        write(Clock.System.now().toString())
        color(Coloring.Color.YELLOW)
        write(" [WARNING] ")
        reset()
        val builder = StringBuilder()
        serializer.serialize(TextViewEncoder(builder, 1u), obj)
        write(builder.toString())
    }

    override suspend fun error(message: String): Unit = this.buildWrite {
        write(Clock.System.now().toString())
        color(Coloring.Color.RED)
        write(" [ERROR] ")
        reset()
        write("$message\n")
    }

    override suspend fun <T> error(obj: T, serializer: KSerializer<T>): Unit = this.buildWrite {
        write(Clock.System.now().toString())
        color(Coloring.Color.RED)
        write(" [ERROR] ")
        reset()
        val builder = StringBuilder()
        serializer.serialize(TextViewEncoder(builder, 1u), obj)
        write(builder.toString())
    }

    override suspend fun fatal(message: String): Unit = this.buildWrite {
        write(Clock.System.now().toString())
        write(" ")
        color(Coloring.Color.RED)
        decorate(Coloring.Decoration.UNDERLINE)
        write("[FATAL]")
        reset()
        write(" $message\n")
    }

    override suspend fun <T> fatal(obj: T, serializer: KSerializer<T>): Unit = this.buildWrite {
        write(Clock.System.now().toString())
        write(" ")
        color(Coloring.Color.RED)
        decorate(Coloring.Decoration.UNDERLINE)
        write("[FATAL]")
        reset()
        write(" ")
        val builder = StringBuilder()
        serializer.serialize(TextViewEncoder(builder, 1u), obj)
        write(builder.toString())
    }
}