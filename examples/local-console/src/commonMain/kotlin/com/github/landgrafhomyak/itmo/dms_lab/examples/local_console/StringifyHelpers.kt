package com.github.landgrafhomyak.itmo.dms_lab.examples.local_console

import com.github.landgrafhomyak.itmo.dms_lab.objects.LabWork
import com.github.landgrafhomyak.itmo.dms_lab.requests.Add
import com.github.landgrafhomyak.itmo.dms_lab.requests.AddIfMax
import com.github.landgrafhomyak.itmo.dms_lab.requests.BoundRequest
import com.github.landgrafhomyak.itmo.dms_lab.requests.Clear
import com.github.landgrafhomyak.itmo.dms_lab.requests.Empty
import com.github.landgrafhomyak.itmo.dms_lab.requests.ExecuteScript
import com.github.landgrafhomyak.itmo.dms_lab.requests.Exit
import com.github.landgrafhomyak.itmo.dms_lab.requests.FilterByDifficulty
import com.github.landgrafhomyak.itmo.dms_lab.requests.Help
import com.github.landgrafhomyak.itmo.dms_lab.requests.History
import com.github.landgrafhomyak.itmo.dms_lab.requests.Info
import com.github.landgrafhomyak.itmo.dms_lab.requests.PrintDescendingByCoordinateX
import com.github.landgrafhomyak.itmo.dms_lab.requests.PrintFieldDescendingMaximumPoint
import com.github.landgrafhomyak.itmo.dms_lab.requests.RemoveById
import com.github.landgrafhomyak.itmo.dms_lab.requests.RemoveGreater
import com.github.landgrafhomyak.itmo.dms_lab.requests.Save
import com.github.landgrafhomyak.itmo.dms_lab.requests.Show
import com.github.landgrafhomyak.itmo.dms_lab.requests.UnexpectedRequestException
import com.github.landgrafhomyak.itmo.dms_lab.requests.Update
import kotlin.jvm.JvmStatic

object StringifyHelpers {
    @JvmStatic
    fun stringifyRequest(request: BoundRequest): String {
        return when (request) {
            is Clear, is Empty, is Exit, is Help, is History,
            is Info, is PrintDescendingByCoordinateX,
            is PrintFieldDescendingMaximumPoint, is Show -> request.meta.id
            is Add                                       -> "${request.meta.id} ${this.stringifyLabWork(request.factory.build())}"
            is AddIfMax                                  -> "${request.meta.id} ${this.stringifyLabWork(request.factory.build())}"
            is ExecuteScript                             -> TODO()
            is FilterByDifficulty                        -> "${request.meta.id} ${request.difficulty.id}"
            is RemoveById                                -> "${request.meta.id} ${request.id}"
            is RemoveGreater                             -> "${request.meta.id} ${request.key}"
            is Save                                      -> TODO()
            is Update                                    -> "${request.meta.id} ${request.id} ${this.stringifyLabWork(request.factory.build())}"
            else                                         -> throw UnexpectedRequestException(request.meta.id)
        }
    }

    @JvmStatic
    fun stringifyLabWork(obj: LabWork): String = "name=\"${
        obj.name.replace("\\", "\\\\").replace("\"", "\\\"")
    }\" coordinates={ x=${
        obj.coordinates.x
    } y=${
        obj.coordinates.y
    } } minimal_point=${
        obj.minimalPoint
    } maximum_point=${
        obj.maximumPoint
    } personal_qualities_maximum=${
        obj.personalQualitiesMaximum
    } difficulty=${
        obj.difficulty.id
    } author={ name=\"${
        obj.author.name.replace("\\", "\\\\").replace("\"", "\\\"")
    }\" weight=${
        obj.author.weight
    } eye_color=${
        obj.author.eyeColor
    } hair_color=${
        obj.author.hairColor
    } nationality=${
        obj.author.nationality
    } }"
}