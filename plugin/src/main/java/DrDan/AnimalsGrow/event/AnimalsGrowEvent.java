package DrDan.AnimalsGrow.event;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;

import java.util.List;
import javax.annotation.Nonnull;

import DrDan.AnimalsGrow.AnimalsGrowAction;

public class AnimalsGrowEvent extends RefSystem<EntityStore> {

    public AnimalsGrowEvent() {
    }

    @Nonnull
    @Override
    public Query<EntityStore> getQuery() { return NPCEntity.getComponentType(); }

    @Override
    public void onEntityAdded(@Nonnull Ref<EntityStore> ref, @Nonnull AddReason reason,
                               @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        if (reason != AddReason.SPAWN) { return; }

        NPCEntity npc = (NPCEntity) store.getComponent(ref, NPCEntity.getComponentType());
        String npcName = npc.getRoleName();

        System.out.println("Entity spawned: " + npcName);

        // Use singleton to handle growth logic
        AnimalsGrowAction.getInstance().Grow(ref, store, commandBuffer);
    }

    @Override
    public void onEntityRemove(@Nonnull Ref<EntityStore> ref, @Nonnull RemoveReason reason,
                                @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {}
}