package com.example.exampleplugin;

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
import com.example.exampleplugin.command.ExampleCommand;
import com.example.exampleplugin.ExampleConfig;
import com.hypixel.hytale.server.core.util.Config;

import javax.annotation.Nonnull;

import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.jline.console.impl.Builtins.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExamplePlugin extends JavaPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExamplePlugin.class);
    private final Config<ExampleConfig> config = this.withConfig("ExamplePlugin", ExampleConfig.CODEC);

    public ExamplePlugin(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        LOGGER.info("Registering ExamplePlugin!");
        config.save();
    }

    @Override
    public void start() {
        LOGGER.info("Starting ExamplePlugin!");

        this.getCommandRegistry().registerCommand(new ExampleCommand(config.get().getGrowsUpInto()));

        // this.config = this.withConfig("ExamplePlugin", ExampleConfig.CODEC);
    }
}