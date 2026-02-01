package DrDan.AnimalsGrow.command;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entity.component.BoundingBox;
import com.hypixel.hytale.server.core.modules.entity.component.Interactable;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.component.PersistentModel;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId;
import com.hypixel.hytale.server.core.modules.interaction.Interactions;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.util.EventTitleUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.system.HolderSystem;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import DrDan.AnimalsGrow.config.AnimalGrowConfig;
import DrDan.AnimalsGrow.config.GrowthEntry;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.server.core.util.Config;

import java.util.List;
import java.util.UUID;
import com.hypixel.hytale.server.npc.NPCPlugin;

import javax.annotation.Nonnull;

import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.jline.console.impl.Builtins.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// public class AgeComponent implements Component<EntityStore> {
//   private float tickInterval;
//   private int remainingTicks;
//   private float elapsedTime;
//   public AgeComponent() {
//     this(1.0f, 10);
//   }
//   public AgeComponent(float tickInterval, int totalTicks) {
//     this.tickInterval = tickInterval;
//     this.remainingTicks = totalTicks;
//     this.elapsedTime = 0f;
//   }
//   public AgeComponent(AgeComponent other) {
//     this.tickInterval = other.tickInterval;
//     this.remainingTicks = other.remainingTicks;
//     this.elapsedTime = other.elapsedTime;
//   }
//   @Nullable
//   @Override
//   public Component<EntityStore> clone() {
//     return new AgeComponent(this);
//   }
//   public float getTickInterval() {
//     return tickInterval;
//   }
//   public int getRemainingTicks() {
//     return remainingTicks;
//   }
//   public float getElapsedTime() {
//     return elapsedTime;
//   }
//   public void addElapsedTime(float dt) {
//     this.elapsedTime += dt;
//   }
//   public void resetElapsedTime() {
//     this.elapsedTime = 0f;
//   }
//   public void decrementRemainingTicks() {
//     this.remainingTicks--;
//   }
//   public boolean isExpired() {
//     return this.remainingTicks <= 0;
//   }
// }

public class AnimalGrowCommand extends AbstractPlayerCommand {
    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private final List<GrowthEntry> config;

    public AnimalGrowCommand(List<GrowthEntry> config) {
        super("example", "An example command");
        this.config = config;
    }

    @Override
    protected void execute(@NonNullDecl CommandContext comandContext, @NonNullDecl Store<EntityStore> store,
        @NonNullDecl Ref<EntityStore> ref, @NonNullDecl PlayerRef playerRef, @NonNullDecl World world) {
            EventTitleUtil.showEventTitleToPlayer(playerRef, Message.raw("Dr Dan's First Plugin"), Message.raw("Great Things To Come... 3"), true);
            // EntityStore entityStore = world.getEntityStore();
            // entityStore.getWorld().getEntityStore().getStore().get

        // Store<EntityStore> entityStore = world.getEntityStore().getStore();
        LOGGER.atInfo().log("Logging all entities in the world...");
        
        final int[] entityCount = {0};

        // UUID sheepUUID = UUID.fromString("3da2ef7d-13e7-34f0-801a-3c51c1a5b9d0");
        // Ref<EntityStore> spawnedSheep = world.getEntityStore().getRefFromUUID(sheepUUID);
        // store.getComponent(spawnedSheep, null);
        
        store.forEachChunk((ArchetypeChunk<EntityStore> chunk, CommandBuffer<EntityStore> commandBuffer) -> {
            for (int i = 0; i < chunk.size(); i++) {
                entityCount[0]++;
                
                Ref<EntityStore> entityRef = chunk.getReferenceTo(i);
                ModelComponent modelComponent = chunk.getComponent(i, ModelComponent.getComponentType());
                String modelName = modelComponent != null ? modelComponent.getModel().getModelAssetId() : "N/A";
                
                // Check if the entity model matches any growth entry's baby form
                for (GrowthEntry growthEntry : config) {
                    if (modelName.equals(growthEntry.getBaby())) {
                        TransformComponent transform = chunk.getComponent(i, TransformComponent.getComponentType());
                        world.execute(() -> {
                            commandBuffer.removeEntity(entityRef, RemoveReason.REMOVE);
                            NPCPlugin.get().spawnNPC(store, growthEntry.getAdult(), null, transform.getPosition(), transform.getRotation());
                        });
                        break;
                    }
                }

                // LOGGER.atInfo().log("Entity #%d: Model=%s, UUID=%s, Position=%s", entityCount[0], modelName, uuidStr, position);
                // LOGGER.atInfo().log("Entity Model=%s", modelName);
            }
        });
        
        // LOGGER.atInfo().log("Total entities logged: %d", entityCount[0]);
        // EventTitleUtil.showEventTitleToPlayer(playerRef, Message.raw("Entities Logged"), Message.raw("Check console for details"), true);
    }
}