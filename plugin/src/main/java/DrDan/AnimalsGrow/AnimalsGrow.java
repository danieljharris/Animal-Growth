package DrDan.AnimalsGrow;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.util.EventTitleUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.Ref;
import DrDan.AnimalsGrow.command.AnimalGrowCommand;
import DrDan.AnimalsGrow.config.AnimalGrowConfig;
import com.hypixel.hytale.server.core.util.Config;

import javax.annotation.Nonnull;

import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.jline.console.impl.Builtins.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnimalsGrow extends JavaPlugin {
    private static final String PLUGIN_NAME = "AnimalsGrow";
    private static final Logger LOGGER = LoggerFactory.getLogger(AnimalsGrow.class);
    private final Config<AnimalGrowConfig> config = this.withConfig(PLUGIN_NAME, AnimalGrowConfig.CODEC);
    public AnimalsGrow(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        LOGGER.info("Registering " + PLUGIN_NAME + "!");
        config.save();
    }

    @Override
    public void start() {
        LOGGER.info("Starting " + PLUGIN_NAME + "!");
        this.getCommandRegistry().registerCommand(new AnimalGrowCommand(config.get().getGrowsUpInto()));

        // this.config = this.withConfig("AnimalsGrow", AnimalGrowConfig.CODEC);
    }
}