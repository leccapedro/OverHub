package studio.overmine.overhub;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import studio.overmine.overhub.commands.main.OverHubCommand;
import studio.overmine.overhub.commands.parkour.ParkourCommand;
import studio.overmine.overhub.commands.pvp.PvpCommand;
import studio.overmine.overhub.commands.spawn.SetSpawnCommand;
import studio.overmine.overhub.commands.spawn.SpawnCommand;
import studio.overmine.overhub.controllers.*;
import studio.overmine.overhub.listeners.*;
import studio.overmine.overhub.models.resources.types.ConfigResource;
import studio.overmine.overhub.models.resources.types.ScoreboardResource;
import studio.overmine.overhub.models.scoreboard.FastBoardProvider;
import studio.overmine.overhub.utilities.BukkitUtil;
import studio.overmine.overhub.utilities.FileConfig;

@Getter
public class OverHub extends JavaPlugin {

    private static final String[] STARTUP_BANNER = {
            "                              ",
            "&9      ___           ___     ",
            "&9     /\\  \\         /\\__\\    ",
            "&9    /::\\  \\       /:/  /    ",
            "&9   /:/\\:\\  \\     /:/__/       &fOverHub &7made by &fOverMine Studios",
            "&9  /:/  \\:\\  \\   /::\\  \\ ___   ",
            "&9 /:/__/ \\:\\__\\ /:/\\:\\  /\\__\\  &fForked &7by &frhylow &7(&f@leccapedro&7)",
            "&9 \\:\\  \\ /:/  / \\/__\\:\\/:/  /  &fVersion: &71.0.3-SNAPSHOT",
            "&9  \\:\\  /:/  /       \\::/  /   &fDiscord: &7https://discord.gg/gw7UcDPfBD",
            "&9   \\:\\/:/  /        /:/  /  ",
            "&9    \\::/  /        /:/  /   ",
            "&9     \\___/         \\___/     ",
            "                              "
    };

    private Map<String, FileConfig> fileConfigs;
    private ResourceController resourceController;
    private UserController userController;
    private SpawnController spawnController;
    private HotbarController hotbarController;
    private ServerSelectorController serverSelectorController;
    private LobbySelectorController lobbySelectorController;
    private FastBoardController fastBoardController;
    private CombatController combatController;
    private BossBarController bossBarController;
    private ParkourController parkourController;
    private PvpPlaceholderExpansion pvpPlaceholderExpansion;

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        this.fileConfigs = new HashMap<>();
        this.fileConfigs.put("config", new FileConfig(this, "config.yml"));
        this.fileConfigs.put("language", new FileConfig(this, "language.yml"));
        this.fileConfigs.put("hotbar", new FileConfig(this, "hotbar.yml"));
        this.fileConfigs.put("pvp-inventory", new FileConfig(this, "data/pvp-inventory.yml"));
        this.fileConfigs.put("scoreboard", new FileConfig(this, "scoreboard.yml"));
        this.fileConfigs.put("server-selector", new FileConfig(this, "selector/server/server-selector.yml"));
        this.fileConfigs.put("lobby-selector", new FileConfig(this, "selector/lobby/lobby-selector.yml"));
        this.fileConfigs.put("parkour", new FileConfig(this, "parkour.yml"));

        this.resourceController = new ResourceController(this);
        this.userController = new UserController(this);
        this.spawnController = new SpawnController(this);
        this.combatController = new CombatController(this);
        this.hotbarController = new HotbarController(this);
        this.serverSelectorController = new ServerSelectorController(this);
        this.lobbySelectorController = new LobbySelectorController(this);

        if (ConfigResource.PARKOUR_SYSTEM_ENABLED && BukkitUtil.SERVER_VERSION >= 13) {
            this.parkourController = new ParkourController(this);

            PacketEvents.getAPI().getEventManager().registerListener(
                    new PacketEventsListener(parkourController),
                    PacketListenerPriority.NORMAL
            );
        }

        if (ScoreboardResource.SCOREBOARD_ENABLED) {
            this.fastBoardController = new FastBoardController(this);
            this.fastBoardController.setAdapter(new FastBoardProvider(this.fastBoardController));
        }
        if (ConfigResource.BOSS_BAR_SYSTEM_ENABLED && BukkitUtil.SERVER_VERSION >= 9) {
            this.bossBarController = new BossBarController(this, getFileConfig("config"));
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
        pluginManager.registerEvents(new CombatListener(this, combatController), this);

        if (ConfigResource.BOSS_BAR_SYSTEM_ENABLED && BukkitUtil.SERVER_VERSION >= 9) {
            pluginManager.registerEvents(new BossBarListener(bossBarController), this);
        }
        if (ScoreboardResource.SCOREBOARD_ENABLED) {
            pluginManager.registerEvents(new FastBoardListener(this), this);
        }
        if (this.parkourController != null) {
            pluginManager.registerEvents(new ParkourListener(this), this);
        }

        Objects.requireNonNull(this.getCommand("overhub")).setExecutor(new OverHubCommand(this));
        Objects.requireNonNull(this.getCommand("overhub")).setTabCompleter(new OverHubCommand(this));
        Objects.requireNonNull(this.getCommand("spawn")).setExecutor(new SpawnCommand(spawnController));
        Objects.requireNonNull(this.getCommand("setspawn")).setExecutor(new SetSpawnCommand(spawnController));
        PvpCommand pvpCommand = new PvpCommand(this);
        Objects.requireNonNull(this.getCommand("pvp")).setExecutor(pvpCommand);
        Objects.requireNonNull(this.getCommand("pvp")).setTabCompleter(pvpCommand);

        if (parkourController != null) {
            Objects.requireNonNull(this.getCommand("parkour")).setExecutor(new ParkourCommand(parkourController));
        }

        Bukkit.getScheduler().runTaskLater(this, () ->
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamerule keepInventory true"), 20L);

        PacketEvents.getAPI().init();

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            this.pvpPlaceholderExpansion = new PvpPlaceholderExpansion(this);
            this.pvpPlaceholderExpansion.register();
        }

        for (String bannerLine : STARTUP_BANNER) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', bannerLine));
        }

        getLogger().info("OverHub enabled successfully.");
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();

        if (this.pvpPlaceholderExpansion != null) {
            this.pvpPlaceholderExpansion.unregister();
            this.pvpPlaceholderExpansion = null;
        }

        if (this.fastBoardController != null) this.fastBoardController.onDisable();
        if (this.parkourController != null) this.parkourController.onDisable();
    }

    public void onReload() {
        this.fileConfigs.values().forEach(FileConfig::reload);
        this.resourceController.onReload();
        this.hotbarController.onReload(true);
        this.serverSelectorController.onReload();
        this.lobbySelectorController.onReload();
        if (this.bossBarController != null) this.bossBarController.onReload();
        if (this.fastBoardController != null) this.fastBoardController.onReload();
        if (this.parkourController != null) this.parkourController.loadOrRefresh();
    }

    public FileConfig getFileConfig(String name) {
        return this.fileConfigs.get(name);
    }
}