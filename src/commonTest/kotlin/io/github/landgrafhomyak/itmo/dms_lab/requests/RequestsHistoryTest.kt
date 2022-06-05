package io.github.landgrafhomyak.itmo.dms_lab.requests

import io.github.landgrafhomyak.itmo.dms_lab.AbstractRecordsCollection
import io.github.landgrafhomyak.itmo.dms_lab.lifecycle.ExecutionContext
import kotlin.test.Test
import kotlin.test.assertContentEquals

/**
 * Набор тестов для проверки [истории запросов][RequestsHistory]
 */
internal class RequestsHistoryTest {
    /**
     * Минимальный объект [запроса][BoundRequest] для [тестов][RequestsHistoryTest]
     */
    @Suppress("EqualsOrHashCode")
    private class SimpleRequest(val index: Int) : BoundRequest<AbstractRecordsCollection<Any>, Any>, Comparable<SimpleRequest> {
        override val meta: RequestMeta get() = SimpleRequest

        companion object : RequestMeta {
            override val description: String get() = "Simple command for tests"
        }

        override fun compareTo(other: SimpleRequest): Int = this.index.compareTo(other.index)

        override fun equals(other: Any?): Boolean {
            if (other !is SimpleRequest) return false
            return this.index == other.index
        }

        override suspend fun ExecutionContext<AbstractRecordsCollection<Any>, Any>.execute() {}
    }

    /**
     * Проверка работоспособности [история][RequestsHistory] при неполном заполнении
     */
    @Test
    fun testNotFull() {
        buildRequestsHistory<SimpleRequest>(10u) history@{
            push(SimpleRequest(90))
            push(SimpleRequest(-7))
            push(SimpleRequest(0))
            assertContentEquals(
                listOf(SimpleRequest(90), SimpleRequest(-7), SimpleRequest(0)),
                this@history
            )
        }
    }

    /**
     * Проверка работоспособности [истории][RequestsHistory] при превышении [вместимость][RequestsHistory.capacity]
     */
    @Test
    fun testOverflow() {
        buildRequestsHistory<SimpleRequest>(2u) history@{
            push(SimpleRequest(90))
            push(SimpleRequest(-7))
            push(SimpleRequest(0))
            assertContentEquals(
                listOf(SimpleRequest(-7), SimpleRequest(0)),
                this@history
            )
        }
    }

    /**
     * Проверка работоспособности [истории][RequestsHistory] при нулевой
     * [вместимости][RequestsHistory.capacity]
     */
    @Test
    fun testZeroCapacity() {
        buildRequestsHistory<SimpleRequest>(0u) history@{
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