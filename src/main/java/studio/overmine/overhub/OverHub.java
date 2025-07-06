package studio.overmine.overhub;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import studio.overmine.overhub.controllers.*;
import studio.overmine.overhub.listeners.*;
import studio.overmine.overhub.models.resources.types.ScoreboardResource;
import studio.overmine.overhub.models.scoreboard.FastBoardProvider;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import studio.overmine.overhub.commands.main.OverHubCommand;
import studio.overmine.overhub.commands.spawn.SetSpawnCommand;
import studio.overmine.overhub.commands.spawn.SpawnCommand;
import studio.overmine.overhub.utilities.FileConfig;
import lombok.Getter;

@Getter
public class OverHub extends JavaPlugin {

    private Map<String, FileConfig> fileConfigs;
    private ResourceController resourceController;
    private UserController userController;
    private SpawnController spawnController;
    private HotbarController hotbarController;
    private ServerSelectorController serverSelectorController;
    private LobbySelectorController lobbySelectorController;
    private FastBoardController fastBoardController;

    @Override
    public void onEnable() {
        this.fileConfigs = new HashMap<>();
        this.fileConfigs.put("config", new FileConfig(this, "config.yml"));
        this.fileConfigs.put("language", new FileConfig(this, "language.yml"));
        this.fileConfigs.put("hotbar", new FileConfig(this, "hotbar.yml"));
        this.fileConfigs.put("scoreboard", new FileConfig(this, "scoreboard.yml"));
        this.fileConfigs.put("server-selector", new FileConfig(this, "selector/server/server-selector.yml"));
        this.fileConfigs.put("lobby-selector", new FileConfig(this, "selector/lobby/lobby-selector.yml"));

        this.resourceController = new ResourceController(this);
        this.userController = new UserController(this);
        this.spawnController = new SpawnController(this);
        this.hotbarController = new HotbarController(this);
        this.serverSelectorController = new ServerSelectorController(this);
        this.lobbySelectorController = new LobbySelectorController(this);

        if (ScoreboardResource.SCOREBOARD_ENABLED) {
            this.fastBoardController = new FastBoardController(this);
            this.fastBoardController.setAdapter(new FastBoardProvider(fastBoardController));
        }

        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new UserListener(this), this);
        pluginManager.registerEvents(new MenuListener(), this);
        pluginManager.registerEvents(new SpawnListener(this), this);
        pluginManager.registerEvents(new HotbarListener(this), this);
        pluginManager.registerEvents(new PlayerListener(), this);
        pluginManager.registerEvents(new WorldListener(), this);
        pluginManager.registerEvents(new DoubleJumpListener(), this);
        pluginManager.registerEvents(new LobbySelectorListener(this), this);
        pluginManager.registerEvents(new CombatListener(), this);
        if (ScoreboardResource.SCOREBOARD_ENABLED) pluginManager.registerEvents(new FastBoardListener(this), this);

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        Objects.requireNonNull(this.getCommand("overhub")).setExecutor(new OverHubCommand(this));
        Objects.requireNonNull(this.getCommand("overhub")).setTabCompleter(new OverHubCommand(this));
        Objects.requireNonNull(this.getCommand("spawn")).setExecutor(new SpawnCommand(spawnController));
        Objects.requireNonNull(this.getCommand("setspawn")).setExecutor(new SetSpawnCommand(spawnController));
    }

    @Override
    public void onDisable() {
        if (this.fastBoardController != null) this.fastBoardController.onDisable();
    }

    public void onReload() {
        this.fileConfigs.values().forEach(FileConfig::reload);
        this.resourceController.onReload();
        this.hotbarController.onReload(true);
        this.serverSelectorController.onReload();
        this.lobbySelectorController.onReload();
        if (this.fastBoardController != null) this.fastBoardController.onReload();
    }

    public FileConfig getFileConfig(String name) {
        return this.fileConfigs.get(name);
    }
}
