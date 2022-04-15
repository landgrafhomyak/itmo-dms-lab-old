package com.github.landgrafhomyak.itmo.dms_lab.io

import com.github.landgrafhomyak.itmo.dms_lab.requests.BoundRequest
import com.github.landgrafhomyak.itmo.dms_lab.objects.LabWork

/**
 * Сообщение лога
 * @see Logger.send
 * @sample Logger.send
 */
sealed class Message {
    /**
     * @see Logger.info
     */
    class Info(val message: String) : Message()

    /**
     * @see Logger.info
     */
    class Warning(val message: String) : Message()

    /**
     * @see Logger.error
     */
    class Error(val message: String) : Message()

    /**
     * @see Logger.request
     */
    class Request(val level: UInt, val command: BoundRequest) : Message()

    /**
     * @see Logger.sendObject
     */
    class Object(val work: LabWork) : Message()
}
