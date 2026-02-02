package DrDan.AnimalsGrow.config

import com.hypixel.hytale.codec.Codec
import com.hypixel.hytale.codec.ExtraInfo
import com.hypixel.hytale.codec.KeyedCodec
import com.hypixel.hytale.codec.builder.BuilderCodec
import com.hypixel.hytale.codec.codecs.array.ArrayCodec
import com.hypixel.hytale.codec.schema.SchemaContext
import com.hypixel.hytale.codec.schema.config.Schema
import com.hypixel.hytale.codec.util.RawJsonReader
import java.io.IOException
import java.util.ArrayList
import java.util.Arrays
import org.bson.BsonValue

class AnimalsGrowConfig {
    var growsUpInto: MutableList<GrowthEntry> = ArrayList()

    constructor() {
        growsUpInto.add(GrowthEntry("Lamb", "Sheep", 5))
        growsUpInto.add(GrowthEntry("Piglet", "Pig", 5))
    }

    private class ListCodec : Codec<MutableList<GrowthEntry>> {
        private val arrayCodec = ArrayCodec(GrowthEntry.CODEC) { size -> arrayOfNulls<GrowthEntry>(size) }

        override fun decode(bsonValue: BsonValue, extraInfo: ExtraInfo): MutableList<GrowthEntry>? {
            val arr = arrayCodec.decode(bsonValue, extraInfo)
            return if (arr != null) arr.toMutableList() else ArrayList()
        }

        override fun encode(list: MutableList<GrowthEntry>, extraInfo: ExtraInfo): BsonValue {
            val arr = list.toTypedArray()
            return arrayCodec.encode(arr, extraInfo)
        }

        override fun decodeJson(reader: RawJsonReader, extraInfo: ExtraInfo): MutableList<GrowthEntry>? {
            val arr = arrayCodec.decodeJson(reader, extraInfo)
            return if (arr != null) arr.toMutableList() else ArrayList()
        }

        override fun toSchema(context: SchemaContext): Schema {
            return arrayCodec.toSchema(context)
        }
    }

    companion object {
        @JvmStatic
        val CODEC = BuilderCodec.builder(AnimalsGrowConfig::class.java, ::AnimalsGrowConfig)
            .append(
                KeyedCodec("GrowsUpInto", ListCodec()),
                { config, value -> config.growsUpInto = value },
                { it.growsUpInto }
            ).add()
            .build()!!
    }
}
