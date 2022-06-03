package io.github.landgrafhomyak.itmo.dms_lab.io

/**
 * [ANSI последовательности](https://en.wikipedia.org/wiki/ANSI_escape_code)
 */
public object AnsiColoring : Coloring {
    override fun reset(): String = "\u001b[0m"

    override fun color(color: Coloring.Color): String = "\u001B[${
        when (color) {
            Coloring.Color.BLUE   -> 34
            Coloring.Color.GREEN  -> 32
            Coloring.Color.RED    -> 31
            Coloring.Color.GREY   -> 37
            Coloring.Color.YELLOW -> 33
        }
    }m"

    override fun decorate(vararg decorations: Coloring.Decoration): String =
        decorations.joinToString(separator = "") { decoration ->
            "\u001B[${
                when (decoration) {
                    Coloring.Decoration.UNDERLINE -> 4
                }
            }m"
        }
}
