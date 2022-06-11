@file:OptIn(ExperimentalSerializationApi::class)

package io.github.landgrafhomyak.itmo.dms_lab.interop

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialInfo
import kotlinx.serialization.descriptors.SerialDescriptor

/**
 * Поле должно игнорироваться при вводе
 * @see getElementInputIgnored
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.BINARY)
@SerialInfo
public annotation class IgnoreInput

/**
 * Возвращает `true` если поле должно игнорироваться при вводе
 * @see IgnoreInput
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun SerialDescriptor.getElementInputIgnored(index: Int): Boolean =
    this.getElementAnnotations(index).find { a -> a is IgnoreInput }?.let { true } ?: false
