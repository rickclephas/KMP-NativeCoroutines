package com.rickclephas.kmp.nativecoroutines.sample.issues

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * This class made the compiler fail with a recursion error
 * when both KMP-NativeCoroutines and `kotlinx.serialization` are applied to the project.
 * The recursion is triggered by the [descriptor] property,
 * to work around this error the return type of [descriptor] can be specified explicitly:
 * ```
 * override val descriptor: SerialDescriptor = ...
 * ```
 */
@Suppress("unused")
@OptIn(ExperimentalSerializationApi::class)
@Serializer(ByteArray::class)
object ByteArraySerializer : KSerializer<ByteArray> {

    override val descriptor =
        PrimitiveSerialDescriptor(ByteArraySerializer::class.qualifiedName!!, PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: ByteArray) =
        encoder.encodeString(value.decodeToString())

    override fun deserialize(decoder: Decoder) =
       decoder.decodeString().encodeToByteArray()
}