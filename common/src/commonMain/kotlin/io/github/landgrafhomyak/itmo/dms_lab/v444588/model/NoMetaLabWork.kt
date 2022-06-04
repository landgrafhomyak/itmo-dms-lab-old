package io.github.landgrafhomyak.itmo.dms_lab.v444588.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
public class NoMetaLabWork(
    override val name: String,
    override val coordinates: Coordinates,
    override val creationDate: Instant = Clock.System.now(),
    override val minimalPoint: Long,
    override val maximumPoint: Double,
    override val personalQualitiesMaximum: Int,
    override val difficulty: Difficulty,
    override val author: Person
) : AbstractLabWork

public typealias InputLabWork = NoMetaLabWork

public inline fun AbstractLabWork.clearMetaAndSave(): NoMetaLabWork =
    if (this is NoMetaLabWork) this
    else NoMetaLabWork(
        this.name,
        this.coordinates,
        this.creationDate,
        this.minimalPoint,
        this.maximumPoint,
        this.personalQualitiesMaximum,
        this.difficulty,
        this.author
    )