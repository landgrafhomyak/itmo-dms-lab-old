package io.github.landgrafhomyak.itmo.dms_lab

import io.github.landgrafhomyak.itmo.dms_lab.interop.RequestOutput
import kotlinx.coroutines.internal.LockFreeLinkedListHead

public class RequestOutputList : Iterable<RequestOutputList.Entry> {
    public sealed class Entry {
        public abstract val fieldName: String
        public abstract val value: Any?
    }

    @Suppress("ArrayInDataClass")
    public data class EStruct(override val fieldName: String, val structureName: String, override val value: Array<Entry>) : Entry()
    public data class EBoolean(override val fieldName: String, override val value: Boolean) : Entry()
    public data class EString(override val fieldName: String, override val value: String) : Entry()
    public data class ENumber(override val fieldName: String, override val value: String) : Entry()
    public data class EFloat(override val fieldName: String, override val value: String) : Entry()
    public data class EEnum(override val fieldName: String, override val value: String) : Entry()
    public data class ENull(override val fieldName: String) : Entry() {
        override val value: Nothing? get() = null
    }

    private val data = mutableListOf<RequestOutput.Message<Entry>>()

    @Suppress("RemoveRedundantQualifierName")
    public operator fun get(index: UInt): RequestOutputList.Entry

    public val size: UInt
}