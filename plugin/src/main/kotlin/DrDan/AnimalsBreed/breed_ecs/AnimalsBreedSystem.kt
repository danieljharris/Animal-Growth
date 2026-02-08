package DrDan.AnimalsBreed.breed_ecs

import com.hypixel.hytale.component.Ref
import com.hypixel.hytale.component.Store
import com.hypixel.hytale.component.SystemGroup
import com.hypixel.hytale.component.query.Query
import com.hypixel.hytale.component.CommandBuffer
import com.hypixel.hytale.component.ArchetypeChunk
import com.hypixel.hytale.component.system.tick.EntityTickingSystem
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource
import com.hypixel.hytale.server.core.modules.entity.damage.DamageModule
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore

import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport

import com.hypixel.hytale.math.vector.Vector3d
import com.hypixel.hytale.math.vector.Vector3f

import DrDan.AnimalsBreed.AnimalsBreed
import DrDan.AnimalsBreed.AnimalsBreedAction

class AnimalsBreedSystem : EntityTickingSystem<EntityStore>() {
    
    // Log throttle - only log every N ticks to reduce spam
    private var tickCount = 0
    private val logInterval = 100 // Log every 100 ticks (~5 seconds at 20 TPS)

    override fun tick(
        dt: Float,
        index: Int,
        archetypeChunk: ArchetypeChunk<EntityStore>,
        store: Store<EntityStore>,
        commandBuffer: CommandBuffer<EntityStore>
    ) {
        val AnimalsBreed = archetypeChunk.getComponent(index, AnimalsBreed.getComponentType()) ?: return
        val ref = archetypeChunk.getReferenceTo(index)
        
        // Get the current in-game time from WorldTimeResource
        val worldTimeResource = store.getResource(WorldTimeResource.getResourceType()) ?: return
        val currentGameTime = worldTimeResource.gameTime
        
        // Check if animal should grow based on in-game time elapsed
        if (AnimalsBreed.shouldGrow(currentGameTime)) {
            AnimalsBreedAction.grow(ref, store, commandBuffer)
            println("AnimalsBreedSystem: Animal has grown up! (In-game time based)")
        } else {
            // Throttled progress logging
            tickCount++
            if (tickCount >= logInterval) {
                tickCount = 0
                val progress = AnimalsBreed.getGrowthProgress(currentGameTime)
                val remaining = AnimalsBreed.getRemainingSeconds(currentGameTime)
                println("AnimalsBreedSystem: Growth progress: ${(progress * 100).toInt()}%, ${remaining}s remaining (in-game time)")
            }
        }
    }

    override fun getGroup(): SystemGroup<EntityStore>? = DamageModule.get().gatherDamageGroup

    override fun getQuery(): Query<EntityStore> = Query.and(AnimalsBreed.getComponentType())
}
