package com.github.landgrafhomyak.itmo.dms_lab.requests

import com.github.landgrafhomyak.itmo.dms_lab.AbstractRecordsCollection

interface BoundRequest<C : AbstractRecordsCollection<*>> {
    val meta: RequestMeta
}