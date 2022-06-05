package io.github.landgrafhomyak.itmo.dms_lab.io

/**
 * Набор функций генерирующих цветовые последовательности для консоли
 */
public interface Coloring {
    /**
     * Полностью очищает форматирование
     */
    public fun reset(): String

    /**
     * Консольные цвета
     */
    public enum class Color {
        BLUE,
        GREEN,
        RED,
        GREY,
        YELLOW
    }

    /**
     * Задаёт [цвет][Coloring.Color] для текста
     */
    @Suppress("RemoveRedundantQualifierName")
    public fun color(color: Coloring.Color): String

    /**
     * Консольные стили
     */
    public enum class Decoration {
        UNDERLINE
    }

    /**
     * Задаёт [стиль][Coloring.Decoration] текста
     */
    @Suppress("RemoveRedundantQualifierName")
    public fun decorate(vararg decorations: Coloring.Decoration): String
}