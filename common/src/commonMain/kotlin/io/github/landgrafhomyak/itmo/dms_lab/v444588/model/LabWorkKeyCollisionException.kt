package io.github.landgrafhomyak.itmo.dms_lab.v444588.model

import io.github.landgrafhomyak.itmo.dms_lab.KeyCollisionException

// TODO replace with io.github.landgrafhomyak.itmo.dms_lab.KeyCollisionException
public class LabWorkKeyCollisionException(
    @Suppress("CanBeParameter", "MemberVisibilityCanBePrivate")
    override val key: LabWorkId
) : KeyCollisionException(key)