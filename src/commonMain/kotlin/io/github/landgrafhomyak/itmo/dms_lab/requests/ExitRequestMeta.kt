package io.github.landgrafhomyak.itmo.dms_lab.requests

public object ExitRequestMeta : RequestMeta {
    override val description: String
        get() = "Прекращает выполнение скрипта"

    override val consoleName: String
        get() = "exit"
}