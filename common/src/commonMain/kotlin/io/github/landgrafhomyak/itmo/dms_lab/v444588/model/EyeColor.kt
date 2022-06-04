package io.github.landgrafhomyak.itmo.dms_lab.v444588.model

import io.github.landgrafhomyak.itmo.dms_lab.interop.DisplayName
import kotlinx.serialization.Serializable

@Serializable
public enum class EyeColor {
    @DisplayName("Зелёные")
    GREEN,

    @DisplayName("Чёрные")
    BLACK,

    @DisplayName("Синие")
    BLUE,

    @DisplayName("Жёлтые")
    YELLOW,

    @DisplayName("Белые")
    WHITE
}
