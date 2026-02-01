package DrDan.AnimalsGrow;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.util.Config;

import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import DrDan.AnimalsGrow.command.AnimalsGrowCommand;
import DrDan.AnimalsGrow.config.AnimalsGrowConfig;
import DrDan.AnimalsGrow.event.AnimalsGrowEvent;

public class AnimalsGrow extends JavaPlugin {
    private static final String PLUGIN_NAME = "AnimalsGrow";
    private static final Logger LOGGER = LoggerFactory.getLogger(AnimalsGrow.class);
    private final Config<AnimalsGrowConfig> config = this.withConfig(PLUGIN_NAME, AnimalsGrowConfig.CODEC);
    public AnimalsGrow(@Nonnull JavaPluginInit init) { super(init); }

    @Override
    protected void setup() {
        LOGGER.info("Registering " + PLUGIN_NAME + "!");
        config.save();
    }

    @Override
    public void start() {
        LOGGER.info("Starting " + PLUGIN_NAME + "!");
        
        AnimalsGrowAction.initialize(config.get().getGrowsUpInto()); // Initialize singleton
        
        this.getCommandRegistry().registerCommand(new AnimalsGrowCommand());
        this.getEntityStoreRegistry().registerSystem(new AnimalsGrowEvent());
    }
}