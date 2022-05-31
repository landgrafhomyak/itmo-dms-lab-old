package com.github.landgrafhomyak.itmo.dms_lab.io

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
        color(Coloring.Color.GREY)
        write(LogLevel.DEBUG.displayOrSerialName + ":\n")
        write("  $message\n")
        reset()
    }

    override suspend fun <T> debug(obj: T, serializer: KSerializer<T>): Unit = this.buildWrite {
        color(Coloring.Color.GREY)
        write(LogLevel.DEBUG.displayOrSerialName + ":\n")
        val builder = StringBuilder()
        serializer.serialize(TextViewEncoder(builder, 1u), obj)
        write(builder.toString())
        reset()
    }

    override suspend fun info(message: String): Unit = this.write("$message\n")

    override suspend fun <T> info(obj: T, serializer: KSerializer<T>): Unit = this.buildWrite {
        val builder = StringBuilder()
        serializer.serialize(TextViewEncoder(builder), obj)
        write(builder.toString())
    }

    override suspend fun warning(message: String): Unit = this.buildWrite {
        color(Coloring.Color.YELLOW)
        write(LogLevel.WARNING.displayOrSerialName + ":\n")
        write("  $message\n")
        reset()
    }

    override suspend fun <T> warning(obj: T, serializer: KSerializer<T>): Unit = this.buildWrite {
        color(Coloring.Color.YELLOW)
        write(LogLevel.WARNING.displayOrSerialName + ":\n")
        val builder = StringBuilder()
        serializer.serialize(TextViewEncoder(builder, 1u), obj)
        write(builder.toString())
        reset()
    }

    override suspend fun error(message: String): Unit = this.buildWrite {
        color(Coloring.Color.RED)
        write(LogLevel.ERROR.displayOrSerialName + ":\n")
        write("  $message\n")
        reset()
    }

    override suspend fun <T> error(obj: T, serializer: KSerializer<T>): Unit = this.buildWrite {
        color(Coloring.Color.RED)
        write(LogLevel.ERROR.displayOrSerialName + ":\n")
        val builder = StringBuilder()
        serializer.serialize(TextViewEncoder(builder, 1u), obj)
        write(builder.toString())
        reset()
    }

    override suspend fun fatal(message: String): Unit = this.buildWrite {
        color(Coloring.Color.RED)
        decorate(Coloring.Decoration.UNDERLINE)
        write(LogLevel.FATAL.displayOrSerialName + ":\n")
        write("  $message\n")
        reset()
    }

    override suspend fun <T> fatal(obj: T, serializer: KSerializer<T>): Unit = this.buildWrite {
        color(Coloring.Color.RED)
        decorate(Coloring.Decoration.UNDERLINE)
        write(LogLevel.FATAL.displayOrSerialName + ":\n")
        val builder = StringBuilder()
        serializer.serialize(TextViewEncoder(builder, 1u), obj)
        write(builder.toString())
        reset()
    }
}