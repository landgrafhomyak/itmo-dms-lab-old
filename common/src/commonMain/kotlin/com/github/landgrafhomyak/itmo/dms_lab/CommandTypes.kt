package com.github.landgrafhomyak.itmo.dms_lab

enum class CommandTypes {
    EMPTY {
        override val keyword: String = ""
        override fun argParse(args: Array<String>): EmptyCommand = EmptyCommand
    },
    ;

    abstract val keyword: String
    abstract fun argParse(args: Array<String>): BundledCommand?

    companion object {

    }
}