package io.github.landgrafhomyak.itmo.dms_lab.v444588.model

import io.github.landgrafhomyak.itmo.dms_lab.CollectionOverflowException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

public class InMemoryLabWorkCollection(
    override val creationDate: Instant = Clock.System.now(),
) : AbstractLabWorkCollection {
    private val data = mutableMapOf<LabWorkId, LabWork>()

    private suspend fun generateId(): LabWorkId {
        return withContext(Dispatchers.Unconfined) {
            val existingKeys = this@InMemoryLabWorkCollection.data.keys.sorted().iterator()
            var p = LabWorkId.MIN_VALUE
            while (existingKeys.hasNext()) {
                val k = existingKeys.next()
                when {
                    p > k  -> {}
                    p == k -> {
                        if (p == LabWorkId.MAX_VALUE)
                            throw CollectionOverflowException()
                        p++
                    }
                    p < k  -> return@withContext p
                }
            }
            return@withContext p
        }
    }

    /*
    init {
        for (lw in initialData) {
            if (lw is AbstractLabWorkWithId) {
                if (lw.id in this.data)
                    throw KeyCollisionException(lw.id)
                this.data[lw.id] = lw.clearMetaAndSave()
            } else {
                this.data[this.generateId()] = lw.clearMetaAndSave()
            }
        }
    }
    */

    override suspend fun add(lw: LabWork): LabWorkId {
        val newId = this.generateId()
        this.data[newId] = lw
        return newId
    }


    override suspend fun update(id: LabWorkId, lw: LabWork): LabWork? {
        val old = this.data[id] ?: return null
        this.data[id] = lw
        return old
    }

    override suspend fun removeById(id: LabWorkId): LabWork? {
        return this.data.remove(id)
    }

    override suspend fun clear() {
        this.data.clear()
    }

    override suspend fun addIfMax(lw: LabWork): AbstractLabWorkCollection.AddIfMaxResult {
        val biggest = this.data.values.maxOrNull()
        @Suppress("LiftReturnOrAssignment")
        if (biggest == null || lw > biggest) {
            val id = this.add(lw)
            return if (biggest == null)
                AbstractLabWorkCollection.AddIfMaxResult.EMPTY_COLLECTION(id)
            else
                AbstractLabWorkCollection.AddIfMaxResult.GREATEST(id)
        } else return AbstractLabWorkCollection.AddIfMaxResult.NOT_GREATEST
    }

    override suspend fun filterByDifficulty(difficulty: Difficulty): Iterator<LabWork> =
        this.data
            .asSequence()
            .filter { (_, v) -> v.difficulty == difficulty }
            .map { (_, v) -> v }
            .iterator()


    override suspend fun sortDescending(): Iterator<LabWork> =
        this.data
            .asIterable()
            .toMutableList()
            .apply { sortedByDescending { (_, v) -> v } }
            .map { (_, v) -> v }
            .iterator()

    override suspend fun sortFieldDescendingMaximumPoint(): Iterator<LabWork> =
        this.data
            .asIterable()
            .toMutableList()
            .apply { sortedByDescending { (_, v) -> v.maximumPoint } }
            .map { (_, v) -> v }
            .iterator()

    override val description: String
        get() = "${super.description} [in-memory]"

    override val size: UInt
        get() = this.data.size.toUInt()

    override fun iterator(): Iterator<LabWork> =
        this.data
            .asSequence()
            .map { (_, v) -> v }
            .iterator()
}