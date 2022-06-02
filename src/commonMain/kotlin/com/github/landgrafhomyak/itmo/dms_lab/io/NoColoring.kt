package com.github.landgrafhomyak.itmo.dms_lab.io

/**
 * Заглушка для бесцветных текстов
 */
public object NoColoring : Coloring {
    override fun reset(): String = ""

    override fun color(color: Coloring.Color): String = ""

    override fun decorate(vararg decorations: Coloring.Decoration): String = ""
}