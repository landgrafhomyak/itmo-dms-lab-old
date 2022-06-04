package io.github.landgrafhomyak.itmo.dms_lab.v444588.model

import io.github.landgrafhomyak.itmo.dms_lab.interop.DisplayName
import kotlinx.serialization.Serializable

@Serializable
public class Person(
    @DisplayName("Имя")
    public val name: String,
    @DisplayName("Вес")
    public val weight: Float,
    @DisplayName("Цвет глаз")
    public val eyeColor: EyeColor,
    @DisplayName("Цвет волос")
    public val hairColor: HairColor,
    @DisplayName("Национальность")
    public val nationality: Country,
) {
    init {
        require(this.name.isNotBlank()) { "Имя автора должно быть непустым" }
        require(this.weight > 0) { "Вес автора должен быть положительным" }
    }
}
