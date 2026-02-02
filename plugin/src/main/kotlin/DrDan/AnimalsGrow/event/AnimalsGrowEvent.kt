package DrDan.AnimalsGrow.event

import com.hypixel.hytale.component.AddReason
import com.hypixel.hytale.component.CommandBuffer
import com.hypixel.hytale.component.ComponentType
import com.hypixel.hytale.component.Ref
import com.hypixel.hytale.component.RemoveReason
import com.hypixel.hytale.component.Store
import com.hypixel.hytale.component.query.Query
import com.hypixel.hytale.component.system.RefSystem
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import com.hypixel.hytale.server.npc.entities.NPCEntity
import DrDan.AnimalsGrow.AnimalsGrowAction
import DrDan.AnimalsGrow.config.GrowthEntry
import DrDan.AnimalsGrow.grow_ecs.AnimalsGrowComponent

class AnimalsGrowEvent(
    private val config: List<GrowthEntry>,
    private val animalsGrowComponentType: ComponentType<EntityStore, AnimalsGrowComponent>
) : RefSystem<EntityStore>() {

    override fun getQuery(): Query<EntityStore> = Query.or(NPCEntity.getComponentType())

    override fun onEntityAdded(
        ref: Ref<EntityStore>,
        reason: AddReason,
        store: Store<EntityStore>,
        commandBuffer: CommandBuffer<EntityStore>
    ) {
        if (reason != AddReason.SPAWN) return

        // Try to get world from store and cache it in AnimalsGrowAction
        try {
            val storeClass = store.javaClass
            val worldField = storeClass.getDeclaredField("world")
            worldField.isAccessible = true
            val world = worldField.get(store)
            if (world != null) {
                @Suppress("UNCHECKED_CAST")
                AnimalsGrowAction.setWorld(world as com.hypixel.hytale.server.core.universe.world.World)
            }
        } catch (e: Exception) {
            // Field might not exist or might be named differently
        }

        val npcComponentType = NPCEntity.getComponentType() as? ComponentType<EntityStore, NPCEntity> ?: return
        val npc = store.getComponent(ref, npcComponentType) ?: return
        val npcName = npc.roleName ?: return

        println("Entity spawned: $npcName")

        for (growthEntry in config) {
            if (growthEntry.baby == null || npcName != growthEntry.baby) continue

            val seconds = growthEntry.timeToGrowUpSeconds ?: continue
            val tickInterval = 1.0f
            val totalTicks = (seconds / tickInterval).toInt()
            
            val animalsGrowComponent = AnimalsGrowComponent(tickInterval, totalTicks)
            commandBuffer.addComponent(ref, animalsGrowComponentType, animalsGrowComponent)
            break
        }
    }

    override fun onEntityRemove(
        ref: Ref<EntityStore>,
        reason: RemoveReason,
        store: Store<EntityStore>,
        commandBuffer: CommandBuffer<EntityStore>
    ) {
        // Handle entity removal if needed
    }
}
