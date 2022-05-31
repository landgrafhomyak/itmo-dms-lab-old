@file:OptIn(ExperimentalSerializationApi::class)

package com.github.landgrafhomyak.itmo.dms_lab.io

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.serializer

/**
 * Название поля или структуры которое будет отображаться при выводе пользователю.
 * @see SerialName
 * @see displayName
 * @see displayOrSerialName
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.BINARY)
@SerialInfo
public annotation class DisplayName(val title: String)

/**
 * Доступ к аннотации [DisplayName] извне
 */
public inline val KSerializer<*>.displayName: String?
    get() = this.descriptor.displayName

/**
 * Доступ к аннотации [DisplayName] извне
 */
public inline val KSerializer<*>.displayOrSerialName: String
    get() = this.descriptor.displayOrSerialName

/**
 * Доступ к аннотации [DisplayName] у членов [перечисления][Enum] извне
 */
@OptIn(InternalSerializationApi::class)
public inline val <reified T : Enum<*>> T.displayName: String?
    get() = T::class.serializer().descriptor.getElementDisplayName(this.ordinal)

/**
 * Доступ к аннотации [DisplayName] у членов [перечисления][Enum] извне
 */
@OptIn(InternalSerializationApi::class)
public inline val <reified T : Enum<*>> T.displayOrSerialName: String
    get() = T::class.serializer().descriptor.getElementDisplayOrSerialName(this.ordinal)

/**
 * Ищет в коллекции аннотацию [DisplayName] и возвращает её значение или `null`, если она не найдена
 */
public inline val <T : Annotation> Iterable<T>.displayName: String?
    get() = this.firstNotNullOfOrNull { ann -> ann as? DisplayName }?.title

/**
 * Возвращает значение аннотации [DisplayName] в [дескрипторе][SerialDescriptor] или `null`, если она не объявлена
 */
public inline val SerialDescriptor.displayName: String?
    get() = this.annotations.displayName

/**
 * Возвращает значение аннотации [DisplayName] в [дескрипторе][SerialDescriptor] или [SerialDescriptor.serialName], если она не объявлена
 */
public inline val SerialDescriptor.displayOrSerialName: String
    get() = this.displayName ?: this.serialName

/**
 * Возвращает значение аннотации [DisplayName] в [дескрипторе][SerialDescriptor] элемента или `null`, если она не объявлена
 */
public inline fun SerialDescriptor.getElementDisplayName(index: Int): String? =
    this.getElementAnnotations(index).displayName

/**
 * Возвращает значение аннотации [DisplayName] в [дескрипторе][SerialDescriptor] в элементе или [SerialDescriptor.getElementDisplayName], если она не объявлена
 */
public inline fun SerialDescriptor.getElementDisplayOrSerialName(index: Int): String =
    this.getElementDisplayName(index) ?: this.getElementName(index)

