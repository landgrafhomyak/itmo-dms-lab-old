package com.github.landgrafhomyak.itmo.dms_lab.interactive.console

import com.github.landgrafhomyak.itmo.dms_lab.commands.Add
import com.github.landgrafhomyak.itmo.dms_lab.commands.AddIfMax
import com.github.landgrafhomyak.itmo.dms_lab.commands.BoundRequest
import com.github.landgrafhomyak.itmo.dms_lab.commands.Clear
import com.github.landgrafhomyak.itmo.dms_lab.commands.Empty
import com.github.landgrafhomyak.itmo.dms_lab.commands.ExecuteScript
import com.github.landgrafhomyak.itmo.dms_lab.commands.Exit
import com.github.landgrafhomyak.itmo.dms_lab.commands.FilterByDifficulty
import com.github.landgrafhomyak.itmo.dms_lab.commands.Help
import com.github.landgrafhomyak.itmo.dms_lab.commands.History
import com.github.landgrafhomyak.itmo.dms_lab.commands.Info
import com.github.landgrafhomyak.itmo.dms_lab.commands.PrintDescendingByCoordinateX
import com.github.landgrafhomyak.itmo.dms_lab.commands.PrintFieldDescendingMaximumPoint
import com.github.landgrafhomyak.itmo.dms_lab.commands.RemoveById
import com.github.landgrafhomyak.itmo.dms_lab.commands.RemoveGreater
import com.github.landgrafhomyak.itmo.dms_lab.commands.Save
import com.github.landgrafhomyak.itmo.dms_lab.commands.Show
import com.github.landgrafhomyak.itmo.dms_lab.commands.UnexpectedRequestException
import com.github.landgrafhomyak.itmo.dms_lab.commands.Update
import com.github.landgrafhomyak.itmo.dms_lab.objects.LabWorkId
import com.github.landgrafhomyak.itmo.dms_lab.objects.toLabWorkIdOrNull
import kotlin.jvm.JvmStatic

object StringCommandParser {
    @JvmStatic
    fun parse(string: String): BoundRequest {
        val args = string.trim().split(Regex("""\s+"""), 2)

        return when (val requestId = args.getOrNull(0) ?: return Empty) {
            Help.id                             -> Help
            Info.id                             -> Info
            Show.id                             -> Show
            Add.id                              -> TODO()
            Update.id                           -> TODO()
            RemoveById.id                       -> TODO()
            Clear.id                            -> Clear
            Save.id                             -> TODO()
            ExecuteScript.id                    -> TODO()
            Exit.id                             -> Exit
            AddIfMax.id                         -> TODO()
            RemoveGreater.id                    -> TODO()
            History.id                          -> History
            FilterByDifficulty.id               -> TODO()
            PrintDescendingByCoordinateX.id     -> PrintDescendingByCoordinateX
            PrintFieldDescendingMaximumPoint.id -> PrintFieldDescendingMaximumPoint
            else                                -> throw UnexpectedRequestException(requestId)
        }
    }

    inline fun parseId(raw: String): LabWorkId? = raw.toLabWorkIdOrNull()





}