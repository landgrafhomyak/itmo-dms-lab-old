package com.github.landgrafhomyak.itmo.dms_lab.interop

import kotlin.jvm.JvmInline

/**
 * Считает все аргументы командной строки ключами
 */
@JvmInline
public value class PlainArgvParsed private constructor(private val original: Map<String, Nothing?>) : ArgvParsed {
    public constructor(argv: Array<String>) : this(argv.associateWith { null })


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