package io.github.landgrafhomyak.itmo.dms_lab.requests

public object HelpRequestMeta : RequestMeta {
    override val description: String
        get() = "Выводит список доступных команд"

    override val consoleName: String
        get() = "help"
}