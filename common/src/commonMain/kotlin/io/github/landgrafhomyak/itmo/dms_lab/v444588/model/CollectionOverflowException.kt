package io.github.landgrafhomyak.itmo.dms_lab.v444588.model

// TODO replace with io.github.landgrafhomyak.itmo.dms_lab.CollectionOverflowException
public class CollectionOverflowException : RuntimeException("Collection overflow") {
    override val message: String = super.message!!
}