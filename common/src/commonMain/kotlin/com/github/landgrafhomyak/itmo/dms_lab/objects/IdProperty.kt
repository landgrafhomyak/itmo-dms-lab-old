package com.github.landgrafhomyak.itmo.dms_lab.objects

import kotlin.jvm.JvmStatic
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class IdProperty : ReadOnlyProperty<LabWork, Long> {
    private var ownerWithId: Pair<LabWorksCollection, Long>? = null

    override fun getValue(thisRef: LabWork, property: KProperty<*>): Long {
        return this.ownerWithId?.second ?: throw IllegalStateException("Объект не принадлежит никакой коллекции")
    }

    companion object {
        @JvmStatic
        internal fun bound() {

        }
    }
}

