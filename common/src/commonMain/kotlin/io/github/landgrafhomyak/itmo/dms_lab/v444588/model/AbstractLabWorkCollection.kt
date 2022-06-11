package io.github.landgrafhomyak.itmo.dms_lab.v444588.model

import io.github.landgrafhomyak.itmo.dms_lab.AbstractRecordsCollection

public interface AbstractLabWorkCollection : AbstractRecordsCollection<LabWork> {

    override val description: String
        get() = "Коллекция лабораторных работ"

    public suspend fun add(lw: LabWork): LabWorkId

    public suspend fun update(id: LabWorkId, lw: LabWork): LabWork?

    public suspend fun removeById(id: LabWorkId): LabWork?

    public suspend fun clear()

    @Suppress("ClassName")
    public sealed class AddIfMaxResult(public val isSuccessful: Boolean) {
        public class GREATEST(public val newId: LabWorkId) : AddIfMaxResult(true)
        public class EMPTY_COLLECTION(public val newId: LabWorkId) : AddIfMaxResult(true)
        public object NOT_GREATEST : AddIfMaxResult(false)
    }

    public suspend fun addIfMax(lw: LabWork): AddIfMaxResult

    public suspend fun filterByDifficulty(difficulty: Difficulty): Iterator<LabWork>

    public suspend fun sortDescending(): Iterator<LabWork>

    public suspend fun sortFieldDescendingMaximumPoint(): Iterator<LabWork>
}