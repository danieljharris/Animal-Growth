package DrDan.AnimalsGrow.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class GrowthEntry {
    private String baby;
    private String adult;
    private Integer timeToGrowUpSeconds;

    public GrowthEntry() {}

    public GrowthEntry(String baby, String adult, Integer timeToGrowUpSeconds) {
        this.baby = baby;
        this.adult = adult;
        this.timeToGrowUpSeconds = timeToGrowUpSeconds;
    }

    public static final BuilderCodec<GrowthEntry> CODEC = BuilderCodec.builder(GrowthEntry.class, GrowthEntry::new)
        .append(new KeyedCodec<String>("Baby", Codec.STRING),
                (entry, val) -> entry.baby = val,
                entry -> entry.baby).add()
        .append(new KeyedCodec<String>("Adult", Codec.STRING),
                (entry, val) -> entry.adult = val,
                entry -> entry.adult).add()
        .append(new KeyedCodec<Integer>("TimeToGrowUpSeconds", Codec.INTEGER),
                (entry, val) -> entry.timeToGrowUpSeconds = val,
                entry -> entry.timeToGrowUpSeconds).add()
        .build();

    public String getBaby() { return baby; }
    public void setBaby(String newValue) { this.baby = newValue; }

    public String getAdult() { return adult; }
    public void setAdult(String newValue) { this.adult = newValue; }

    public Integer getTimeToGrowUpSeconds() { return timeToGrowUpSeconds; }
    public void setTimeToGrowUpSeconds(Integer newValue) { this.timeToGrowUpSeconds = newValue; }
}