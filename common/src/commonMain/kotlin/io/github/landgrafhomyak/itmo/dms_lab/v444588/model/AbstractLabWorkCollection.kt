package io.github.landgrafhomyak.itmo.dms_lab.v444588.model

import io.github.landgrafhomyak.itmo.dms_lab.AbstractRecordsCollection

public interface AbstractLabWorkCollection : AbstractRecordsCollection<AbstractLabWorkWithId> {

    override val description: String
        get() = "Коллекция лабораторных работ"

    public suspend fun add(lw: AbstractLabWork): LabWorkId

    public suspend fun update(id: Long, lw: AbstractLabWork): AbstractLabWork?

    public suspend fun removeById(id: Long): AbstractLabWork?

    public suspend fun clear()

    public enum class AddIfMaxResult(public val isSuccessful: Boolean) {
        GREATEST(true),
        EMPTY_COLLECTION(true),
        NOT_GREATEST(false)
    }

    public suspend fun addIfMax(lw: AbstractLabWork): AddIfMaxResult

    public suspend fun filterByDifficulty(difficulty: Difficulty): Iterator<AbstractLabWorkWithId>

    public suspend fun sortDescending(): Iterator<AbstractLabWorkWithId>

    public suspend fun sortFieldDescendingMaximumPoint(): Iterator<AbstractLabWorkWithId>
}