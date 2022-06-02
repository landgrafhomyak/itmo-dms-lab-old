package com.github.landgrafhomyak.itmo.dms_lab.interop

import kotlin.jvm.JvmInline

/**
 * Читает аргументы из URL адреса
 */
@JvmInline
public value class UrlQueryArgvParsed private constructor(private val original: Map<String, String?>) : ArgvParsed {
    public constructor(url: String) : this(
        url
            .run { split('?', limit = 1).getOrNull(1) ?: String() }
            .split("&")
            .map { s -> s.split('=', limit = 1) }
            .associate { p -> p[0] to p.getOrNull(1) }
    )


    override val entries: Set<Map.Entry<String, String?>>
        get() = this.original.entries
    override val keys: Set<String>
        get() = this.original.keys
    override val size: Int
        get() = this.original.size
    override val values: Collection<String?>
        get() = this.original.values

    override fun containsKey(key: String): Boolean = this.original.containsKey(key)

    override fun containsValue(value: String?): Boolean = this.original.containsValue(value)

    override fun get(key: String): String? = this.original[key]

    override fun isEmpty(): Boolean = this.original.isEmpty()
}