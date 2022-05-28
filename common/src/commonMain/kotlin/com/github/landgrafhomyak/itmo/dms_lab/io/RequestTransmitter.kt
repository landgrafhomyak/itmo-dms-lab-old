package com.github.landgrafhomyak.itmo.dms_lab.io

import com.github.landgrafhomyak.itmo.dms_lab.AbstractRecordsCollection
import com.github.landgrafhomyak.itmo.dms_lab.requests.BoundRequest

interface RequestTransmitter<C : AbstractRecordsCollection<*>> {
    suspend fun request(data: BoundRequest<C>)
}