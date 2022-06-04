package io.github.landgrafhomyak.itmo.dms_lab.v444588.model

import io.github.landgrafhomyak.itmo.dms_lab.interop.DisplayName
import kotlinx.serialization.Serializable

@Serializable
public enum class Difficulty {
    @DisplayName("Сложно")
    HARD,
    @DisplayName("Безумно")
    INSANE,
    @DisplayName("Ужасно")
    TERRIBLE
}
