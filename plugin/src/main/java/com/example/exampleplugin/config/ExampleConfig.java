package com.example.exampleplugin.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.schema.SchemaContext;
import com.hypixel.hytale.codec.schema.config.Schema;
import com.hypixel.hytale.codec.util.RawJsonReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import org.bson.BsonValue;

public class ExampleConfig {
    private List<GrowthEntry> growsUpInto = new ArrayList<>();

    public ExampleConfig() {
        growsUpInto.add(new GrowthEntry("Lamb", "Sheep", 600));
        growsUpInto.add(new GrowthEntry("Piglet", "Pig", 900));
    }

    private static class ListCodec implements Codec<List<GrowthEntry>> {
        private final ArrayCodec<GrowthEntry> arrayCodec = new ArrayCodec<>(GrowthEntry.CODEC, GrowthEntry[]::new);

        @Override
        public List<GrowthEntry> decode(BsonValue bsonValue, ExtraInfo extraInfo) {
            GrowthEntry[] arr = arrayCodec.decode(bsonValue, extraInfo);
            return arr != null ? Arrays.asList(arr) : new ArrayList<>();
        }

        @Override
        public BsonValue encode(List<GrowthEntry> list, ExtraInfo extraInfo) {
            GrowthEntry[] arr = list != null ? list.toArray(new GrowthEntry[0]) : new GrowthEntry[0];
            return arrayCodec.encode(arr, extraInfo);
        }

        @Override
        public List<GrowthEntry> decodeJson(RawJsonReader reader, ExtraInfo extraInfo) throws IOException {
            GrowthEntry[] arr = arrayCodec.decodeJson(reader, extraInfo);
            return arr != null ? Arrays.asList(arr) : new ArrayList<>();
        }

        @Override
        public Schema toSchema(@Nonnull SchemaContext context) {
            return arrayCodec.toSchema(context);
        }
    }

    public static final BuilderCodec<ExampleConfig> CODEC = BuilderCodec.builder(ExampleConfig.class, ExampleConfig::new)
        .append(new KeyedCodec<List<GrowthEntry>>("GrowsUpInto", new ListCodec()),
                (exConfig, val) -> exConfig.growsUpInto = val,
                exConfig -> exConfig.growsUpInto).add()
        .build();

    public List<GrowthEntry> getGrowsUpInto() { return growsUpInto; }
    public void setGrowsUpInto(List<GrowthEntry> newValue) { this.growsUpInto = newValue; }
}