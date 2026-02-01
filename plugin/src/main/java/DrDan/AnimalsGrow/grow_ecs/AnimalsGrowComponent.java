package DrDan.AnimalsGrow.grow_ecs;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nullable;

public class AnimalsGrowComponent implements Component<EntityStore> {
  private float tickInterval;
  private int remainingTicks;
  private float elapsedTime;

  public AnimalsGrowComponent() {
    this(1.0f, 10);
  }

  public AnimalsGrowComponent(float tickInterval, int totalTicks) {
    this.tickInterval = tickInterval;
    this.remainingTicks = totalTicks;
    this.elapsedTime = 0f;
  }

  public AnimalsGrowComponent(AnimalsGrowComponent other) {
    this.tickInterval = other.tickInterval;
    this.remainingTicks = other.remainingTicks;
    this.elapsedTime = other.elapsedTime;
  }

  @Nullable
  @Override
  public Component<EntityStore> clone()                   { return new AnimalsGrowComponent(this); }
  public float                  getTickInterval()         { return tickInterval;                   }
  public int                    getRemainingTicks()       { return remainingTicks;                 }
  public float                  getElapsedTime()          { return elapsedTime;                    }
  public void                   addElapsedTime(float dt)  { this.elapsedTime += dt;                }
  public void                   resetElapsedTime()        { this.elapsedTime = 0f;                 }
  public void                   decrementRemainingTicks() { this.remainingTicks--;                 }
  public boolean                isExpired()               { return this.remainingTicks <= 0;       }
}