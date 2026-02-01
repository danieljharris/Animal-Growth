package DrDan.AnimalsGrow.grow_ecs;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.SystemGroup;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageCause;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageModule;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageSystems;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import DrDan.AnimalsGrow.grow_ecs.AnimalsGrowComponent;

public class AnimalsGrowSystem extends EntityTickingSystem<EntityStore> {
  private final ComponentType<EntityStore, AnimalsGrowComponent> animalsGrowComponentType;
  public AnimalsGrowSystem(ComponentType<EntityStore, AnimalsGrowComponent> animalsGrowComponentType) {
    this.animalsGrowComponentType = animalsGrowComponentType;
  }
  @Override
  public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk,
      @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
    // You could also just call AnimalsGrowComponent.getComponentType() instead of taking in the passed in variable here.
    AnimalsGrowComponent animalsGrow = archetypeChunk.getComponent(index, animalsGrowComponentType);
    Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
    animalsGrow.addElapsedTime(dt);
    if (animalsGrow.getElapsedTime() >= animalsGrow.getTickInterval()) {
      animalsGrow.resetElapsedTime();
      animalsGrow.decrementRemainingTicks();
    }
    if (animalsGrow.isExpired()) {
      commandBuffer.removeComponent(ref, animalsGrowComponentType);
    }
  }
  @Nullable
  @Override
  public SystemGroup<EntityStore> getGroup() {
    return DamageModule.get().getGatherDamageGroup();
  }
  @Nonnull
  @Override
  public Query<EntityStore> getQuery() {
    return Query.and(this.animalsGrowComponentType);
  }
}