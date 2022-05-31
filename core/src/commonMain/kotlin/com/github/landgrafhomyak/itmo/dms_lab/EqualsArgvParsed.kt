package com.github.landgrafhomyak.itmo.dms_lab

import kotlin.jvm.JvmInline

/**
 * Читает аргументы командной строки после знака равно (`=`)
 */
@JvmInline
public value class EqualsArgvParsed private constructor(private val original: Map<String, String?>) : ArgvParsed {
    public constructor(argv: Array<String>) : this(argv.associate { s ->
        val kv = s.split('=', limit = 1)
        return@associate if (kv.size == 1) kv[0] to null else kv[0] to kv[1]
    })


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