package DrDan.AnimalsGrow

import com.hypixel.hytale.server.core.plugin.JavaPlugin
import com.hypixel.hytale.server.core.plugin.JavaPluginInit
import com.hypixel.hytale.server.core.util.Config
import org.slf4j.LoggerFactory
import DrDan.AnimalsGrow.command.AnimalsGrowCommand
import DrDan.AnimalsGrow.config.AnimalsGrowConfig
import DrDan.AnimalsGrow.event.AnimalsGrowEvent
import DrDan.AnimalsGrow.grow_ecs.AnimalsGrowComponent
import DrDan.AnimalsGrow.grow_ecs.AnimalsGrowSystem

private const val PLUGIN_NAME = "AnimalsGrow"

class AnimalsGrow(init: JavaPluginInit) : JavaPlugin(init) {
    private val logger = LoggerFactory.getLogger(AnimalsGrow::class.java)
    private val config: Config<AnimalsGrowConfig> = this.withConfig(PLUGIN_NAME, AnimalsGrowConfig.CODEC)

    override fun setup() {
        logger.info("Registering $PLUGIN_NAME!")
        config.save()
    }

    override fun start() {
        logger.info("Starting $PLUGIN_NAME!")
        
        val growthConfig = config.get().growsUpInto
        AnimalsGrowAction.initialize(growthConfig)
        // TODO: Get actual world, for now using hack to access from server context
        // The world will be set when the system ticks with a Store<EntityStore> instance
        commandRegistry.registerCommand(AnimalsGrowCommand())
        
        // Register AnimalsGrowComponent and get its type
        val animalsGrowComponentType = entityStoreRegistry.registerComponent(
            AnimalsGrowComponent::class.java,
            java.util.function.Supplier { AnimalsGrowComponent() }
        )
        AnimalsGrowComponent.setComponentType(animalsGrowComponentType)
        
        // Register systems with the component type
        entityStoreRegistry.registerSystem(AnimalsGrowEvent(growthConfig, animalsGrowComponentType))
        entityStoreRegistry.registerSystem(AnimalsGrowSystem(animalsGrowComponentType))
    }
}
