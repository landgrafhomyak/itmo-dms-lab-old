package io.github.landgrafhomyak.itmo.dms_lab.v444588.model

import io.github.landgrafhomyak.itmo.dms_lab.CollectionOverflowException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

public class InMemoryLabWorkCollection(
    override val creationDate: Instant = Clock.System.now(),
) : AbstractLabWorkCollection {
    private val data = mutableMapOf<LabWorkId, NoMetaLabWork>()

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

    override suspend fun add(lw: AbstractLabWork) {
        this.data[this.generateId()] = lw.clearMetaAndSave()
    }


    override suspend fun update(id: Long, lw: AbstractLabWork): AbstractLabWork? {
        val old = this.data[id] ?: return null
        this.data[id] = lw.clearMetaAndSave()
        return old
    }

    override suspend fun removeById(id: Long): AbstractLabWork? {
        return this.data.remove(id)
    }

    override suspend fun clear() {
        this.data.clear()
    }

    override suspend fun addIfMax(lw: AbstractLabWork): AbstractLabWorkCollection.AddIfMaxResult {
        val biggest = this.data.values.maxOrNull()
        @Suppress("LiftReturnOrAssignment")
        if (biggest == null || lw > biggest) {
            this.add(lw)
            return if (biggest == null)
                AbstractLabWorkCollection.AddIfMaxResult.EMPTY_COLLECTION
            else
                AbstractLabWorkCollection.AddIfMaxResult.GREATEST
        } else return AbstractLabWorkCollection.AddIfMaxResult.NOT_GREATEST
    }

    override suspend fun filterByDifficulty(difficulty: Difficulty): Iterator<AbstractLabWorkWithId> =
        this.data
            .asSequence()
            .filter { (_, v) -> v.difficulty == difficulty }
            .map { (k, v) -> InMemoryLabWork(k, v) }
            .iterator()


    override suspend fun sortDescending(): Iterator<AbstractLabWorkWithId> =
        this.data
            .asIterable()
            .toMutableList()
            .apply { sortedByDescending { (_, v) -> v } }
            .map { (k, v) -> InMemoryLabWork(k, v) }
            .iterator()

    override suspend fun sortFieldDescendingMaximumPoint(): Iterator<AbstractLabWorkWithId> =
        this.data
            .asIterable()
            .toMutableList()
            .apply { sortedByDescending { (_, v) -> v.maximumPoint } }
            .map { (k, v) -> InMemoryLabWork(k, v) }
            .iterator()

    override val description: String
        get() = "${super.description} [in-memory]"

    override val size: UInt
        get() = this.data.size.toUInt()

    override fun iterator(): Iterator<AbstractLabWorkWithId> =
        this.data
            .asSequence()
            .map { (k, v) -> InMemoryLabWork(k, v) }
            .iterator()
}

internal class InMemoryLabWork(
    override val id: Long,
    private val lw: NoMetaLabWork
) : AbstractLabWorkWithId, AbstractLabWork by lw {
    init {
        this.lw.validate()
    }
}