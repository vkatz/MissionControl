package com.vkatz.missioncontrol.common

import kotlinx.serialization.Serializable
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

private var globalOrderId = AtomicInteger()

interface ValueCommand<T : Any?> {
    var value: T
    val name: String
    var group: String
    val uuid: String
    var orderId: Int
}

@Serializable
sealed class Command {
    var orderId: Int = globalOrderId.getAndIncrement()
    val uuid: String = UUID.randomUUID().toString()

    // action commands

    @Serializable
    data class ConnectionName(val name: String) : Command()

    @Serializable
    data class PropertyRemove(val removeUUID: String) : Command()

    // value commands

    @Serializable
    data class IntPropertyUpdate(
        override var value: Int?,
        override val name: String,
        val nullable: Boolean = true,
        val min: Int? = null,
        val max: Int? = null,
        override var group: String = ""
    ) : Command(), ValueCommand<Int?>

    @Serializable
    data class FloatPropertyUpdate(
        override var value: Float?,
        override val name: String,
        val nullable: Boolean = true,
        val min: Float? = null,
        val max: Float? = null,
        override var group: String = ""
    ) : Command(), ValueCommand<Float?>

    @Serializable
    data class BooleanPropertyUpdate(
        override var value: Boolean?,
        override val name: String,
        val nullable: Boolean = true,
        override var group: String = ""
    ) : Command(), ValueCommand<Boolean?>

    @Serializable
    data class StringPropertyUpdate(
        override var value: String,
        override val name: String,
        override var group: String = ""
    ) : Command(), ValueCommand<String>

    @Serializable
    data class ColorPropertyUpdate(
        override var value: Int,
        override val name: String,
        override var group: String = ""
    ) : Command(), ValueCommand<Int>

    @Serializable
    data class OptionPropertyUpdate(
        override var value: Int,
        override val name: String,
        val options: List<String>,
        override var group: String = ""
    ) : Command(), ValueCommand<Int>

    @Serializable
    data class ActionPropertyUpdate(
        override val name: String,
        val description: String,
        val actionButton: String,
        override var group: String = ""
    ) : Command(), ValueCommand<Long> {
        override var value: Long = 0L
    }

    @Serializable
    data class InfoPropertyUpdate(
        override var value: String,
        override val name: String,
        override var group: String = ""
    ) : Command(), ValueCommand<String>
}