package com.github.landgrafhomyak.itmo.dms_lab.requests

import com.github.landgrafhomyak.itmo.dms_lab.AbstractRecordsCollection
import kotlin.test.Test
import kotlin.test.assertContentEquals

internal class RequestsHistoryTest {
    @Suppress("EqualsOrHashCode")
    private class SimpleRequest(val index: Int) : BoundRequest<AbstractRecordsCollection<*>>, Comparable<SimpleRequest> {
        override val meta: RequestMeta get() = SimpleRequest

        companion object : RequestMeta {
            override val description: String get() = "Simple command for tests"
        }

        override fun compareTo(other: SimpleRequest): Int = this.index.compareTo(other.index)

        override fun equals(other: Any?): Boolean {
            if (other !is SimpleRequest) return false
            return this.index == other.index
        }
    }

    @Test
    fun testNotFull() {
        buildRequestsHistory<AbstractRecordsCollection<*>>(10u) history@{
            push(SimpleRequest(90))
            push(SimpleRequest(-7))
            push(SimpleRequest(0))
            assertContentEquals(
                listOf(SimpleRequest(90), SimpleRequest(-7), SimpleRequest(0)),
                this@history
            )
        }
    }

    @Test
    fun testOverflow() {
        buildRequestsHistory<AbstractRecordsCollection<*>>(2u) history@{
            push(SimpleRequest(90))
            push(SimpleRequest(-7))
            push(SimpleRequest(0))
            assertContentEquals(
                listOf(SimpleRequest(-7), SimpleRequest(0)),
                this@history
            )
        }
    }

    @Test
    fun testZeroCapacity() {
        buildRequestsHistory<AbstractRecordsCollection<*>>(0u) history@{
            push(SimpleRequest(90))
            push(SimpleRequest(-7))
            push(SimpleRequest(0))
            assertContentEquals(
                listOf(),
                this@history
            )

        }
    }
}