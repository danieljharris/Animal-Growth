package DrDan.AnimalsBreed.config

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

class AnimalsBreedConfig {
    var breedGroup: MutableList<BreedEntry> = ArrayList()

    constructor() {
        breedGroup.add(BreedEntry(listOf("Sheep", "Ram", "Mouflon"))) // Real thing, "Mouflon" can breed with "Sheep" and produce fertile offspring
        breedGroup.add(BreedEntry(listOf("Pig", "Boar", "Pig_Wild")))
        breedGroup.add(BreedEntry(listOf("Camel")))
        breedGroup.add(BreedEntry(listOf("Chicken_Desert", "Chicken", "Skrill")))
        breedGroup.add(BreedEntry(listOf("Cow", "Bison"))) // Real thing, called "Beefalo"
        breedGroup.add(BreedEntry(listOf("Goat")))
        breedGroup.add(BreedEntry(listOf("Horse")))
        breedGroup.add(BreedEntry(listOf("Rabbit")))
        breedGroup.add(BreedEntry(listOf("Turkey"))) // No real-world records of being able to breed with domestic chickens
        breedGroup.add(BreedEntry(listOf("Warthog"))) // No recorded real-world records of being able to breed with domestic pigs/boars
    }

    private class ListCodec : Codec<MutableList<BreedEntry>> {
        private val arrayCodec = ArrayCodec(BreedEntry.CODEC) { size -> arrayOfNulls<BreedEntry>(size) }

        override fun decode(bsonValue: BsonValue, extraInfo: ExtraInfo): MutableList<BreedEntry>? {
            val arr = arrayCodec.decode(bsonValue, extraInfo)
            return if (arr != null) arr.toMutableList() else ArrayList()
        }

        override fun encode(list: MutableList<BreedEntry>, extraInfo: ExtraInfo): BsonValue {
            val arr = list.toTypedArray()
            return arrayCodec.encode(arr, extraInfo)
        }

        override fun decodeJson(reader: RawJsonReader, extraInfo: ExtraInfo): MutableList<BreedEntry>? {
            val arr = arrayCodec.decodeJson(reader, extraInfo)
            return if (arr != null) arr.toMutableList() else ArrayList()
        }

        override fun toSchema(context: SchemaContext): Schema {
            return arrayCodec.toSchema(context)
        }
    }

    companion object {
        @JvmStatic
        val CODEC = BuilderCodec.builder(AnimalsBreedConfig::class.java, ::AnimalsBreedConfig)
            .append(
                KeyedCodec("BreedGroup", ListCodec()),
                { config, value -> config.breedGroup = value },
                { it.breedGroup }
            ).add()
            .build()!!
    }
}
