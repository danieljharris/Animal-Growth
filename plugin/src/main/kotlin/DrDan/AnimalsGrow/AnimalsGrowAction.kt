package DrDan.AnimalsGrow

import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent
import com.hypixel.hytale.server.npc.NPCPlugin
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import com.hypixel.hytale.server.core.universe.world.World
import com.hypixel.hytale.server.core.universe.Universe
import com.hypixel.hytale.component.CommandBuffer
import com.hypixel.hytale.component.ComponentType
import com.hypixel.hytale.component.Store
import com.hypixel.hytale.component.Ref
import com.hypixel.hytale.component.RemoveReason
import com.hypixel.hytale.server.npc.entities.NPCEntity
import com.hypixel.hytale.math.vector.Vector3d
import com.hypixel.hytale.math.vector.Vector3f
import DrDan.AnimalsGrow.config.GrowthEntry
import DrDan.AnimalsGrow.grow_ecs.GrowthSpawn

object AnimalsGrowAction {
    private lateinit var config: List<GrowthEntry>
    private var world: World? = null
    
    fun initialize(growthConfig: List<GrowthEntry>) { config = growthConfig }
    
    fun setWorld(w: World) { world = w }
    
    fun getConfig(): List<GrowthEntry> = config

    fun grow(
        ref: Ref<EntityStore>,
        store: Store<EntityStore>,
        commandBuffer: CommandBuffer<EntityStore>
    ) {
        val npcComponentType = NPCEntity.getComponentType() as? ComponentType<EntityStore, NPCEntity> ?: return
        val npcEntity = store.getComponent(ref, npcComponentType) ?: return
        val npcName: String = npcEntity.roleName ?: return

        for (growthEntry in config) {
            if (growthEntry.baby == null || npcName != growthEntry.baby) continue
            val adultName = growthEntry.adult ?: continue

            val transformComponentType = TransformComponent.getComponentType() as? ComponentType<EntityStore, TransformComponent> ?: return
            val transform = store.getComponent(ref, transformComponentType) ?: return
            
            // Log the transformation
            println("Animal growing: $npcName -> $adultName at ${transform.position}")
            
            // Remove the baby entity
            commandBuffer.removeEntity(ref, RemoveReason.REMOVE)
            
            // Get world from cache or fall back to Universe default
            val w = world ?: Universe.get().defaultWorld
            if (w != null) {
                w.execute {
                    NPCPlugin.get().spawnNPC(store, adultName, null, transform.position, transform.rotation)
                }
            } else {
                println("ERROR: Could not get world from cache or Universe!")
            }
            break
        }
    }
}
