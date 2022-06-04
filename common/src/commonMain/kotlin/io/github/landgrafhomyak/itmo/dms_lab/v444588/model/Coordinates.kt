package io.github.landgrafhomyak.itmo.dms_lab.v444588.model

import io.github.landgrafhomyak.itmo.dms_lab.interop.DisplayName
import kotlinx.serialization.Serializable

@Serializable
public class Coordinates(
    @DisplayName("Координата X")
    public val x: Long,
    @DisplayName("Координата Y")
    @Suppress("MemberVisibilityCanBePrivate")
    public val y: Long
) {
    init {
        require(this.y <= 116) { "Координата Y не должна превышать 116" }
    }
}