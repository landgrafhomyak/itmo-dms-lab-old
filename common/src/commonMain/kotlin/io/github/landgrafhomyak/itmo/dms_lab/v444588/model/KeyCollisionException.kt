package io.github.landgrafhomyak.itmo.dms_lab.v444588.model

// TODO replace with io.github.landgrafhomyak.itmo.dms_lab.KeyCollisionException
public class KeyCollisionException(
    @Suppress("CanBeParameter", "MemberVisibilityCanBePrivate")
    public val key: LabWorkId
) : RuntimeException("Identifier collision: $key") {
    override val message: String = super.message!!
}