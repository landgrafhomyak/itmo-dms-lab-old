package io.github.landgrafhomyak.itmo.dms_lab.v444588.model

import io.github.landgrafhomyak.itmo.dms_lab.interop.DisplayName
import kotlinx.serialization.Serializable

@Serializable
public enum class Country {
    @DisplayName("Россия")
    RUSSIA,

    @DisplayName("Германия")
    GERMANY,

    @DisplayName("Китай")
    CHINA,

    @DisplayName("Северная Корея")
    NORTH_KOREA,

    @DisplayName("Япония")
    JAPAN
}
