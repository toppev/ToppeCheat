package com.toppecraft.toppecheat;

import com.toppecraft.toppecheat.alert.Alert;
import com.toppecraft.toppecheat.checksystem.CheckBypass;
import com.toppecraft.toppecheat.checksystem.CheckManager;
import com.toppecraft.toppecheat.filemanager.FileManager;
import com.toppecraft.toppecheat.lagmeter.LagMeter;
import com.toppecraft.toppecheat.nms.NMSProvider;
import com.toppecraft.toppecheat.packetlistener.channel.newer.CManagerNewer;
import com.toppecraft.toppecheat.packetlistener.listener.TACPacketListener;
import com.toppecraft.toppecheat.playerdata.MySQL;
import com.toppecraft.toppecheat.playerdata.PlayerData;
import com.toppecraft.toppecheat.playerdata.PlayerDataListener;
import com.toppecraft.toppecheat.playerreports.ReportCommand;
import com.toppecraft.toppecheat.punishments.automute.AutomuteManager;
import com.toppecraft.toppecheat.script.ScriptManager;
import com.toppecraft.toppecheat.stafftools.StaffCommandManager;
import com.toppecraft.toppecheat.utils.MathUtils;
import com.toppecraft.toppecheat.utils.MineCraftVersionManager;
import com.toppecraft.toppecheat.violations.ViolationLevels;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

/**
 * @author Toppe5
 * @since 2.0
 */
public class ToppeCheat extends JavaPlugin {

    public static final Random random = new Random();

    private static ToppeCheat instance;
    private StaffCommandManager commandManager;
    private FileManager fileManager;
    private CheckManager checkManager;
    private LagMeter lagMeter;
    private CheckBypass checkBypassManager;
    private boolean isMySQL;
    private MySQL mySQL;
    private NMSProvider nmsProvider;

    /**
     * Gets the instance of the ToppeCheat.
     *
     * @return this instance
     */
    public static ToppeCheat getInstance() {
        return instance;
    }

    @Override
    public void onDisable() {
        long st = System.currentTimeMillis();
        try {
            checkManager.disable();
            for (Player p : Bukkit.getOnlinePlayers()) {
                PlayerData.getOnlinePlayerData(p).save(p.getUniqueId());
            }
            unregisterAllPlayers();
            instance = null;
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(ChatColor.RED + "An error occurred while disabling TAC: " + e.toString() + "...\n", true);
        }
        Bukkit.getLogger().info("ToppeCheat by Toppe5 was disabled in " + (System.currentTimeMillis() - st) + " ms.");
    }

    @Override
    public void onEnable() {
        long st = System.currentTimeMillis();
        try {
            instance = this;
            ConfigurationSerialization.registerClass(PlayerData.class, "PlayerData");
            nmsProvider = new NMSProvider();
            nmsProvider.setup();
            registerCManager();
            MineCraftVersionManager.load();
            lagMeter = new LagMeter(this);
            saveDefaultConfig();
            fileManager = new FileManager(this);
            checkManager = new CheckManager();
            checkBypassManager = new CheckBypass();
            commandManager = new StaffCommandManager(this);
            getCommandManager().load();
            new TACPacketListener(this);
            LagMeter.maxPacketDelay = getConfig().getLong("max-packet-delay");
            LagMeter.maxLag = getConfig().getInt("max-lag");
            getCheckManager().loadCheckSystems(ToppeCheat.this);
            AutomuteManager.register(this);
            new ScriptManager(this).loadScriptSystem();
            PlayerDataListener pdl = new PlayerDataListener(this);
            Bukkit.getPluginManager().registerEvents(pdl, this);
            pdl.loadOnlinePlayers();
            setupMySQL();
            ViolationLevels.setDefaultExpiration(getConfig().getInt("violation-expiration"));
            getCommand("report").setExecutor(new ReportCommand(this));
            registerAllPlayers();
            MathUtils.fastMath = getConfig().getBoolean("fast-math");
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(ChatColor.RED + "An error occurred while enabling TAC: " + e.toString() + "...\n" + ChatColor.DARK_RED + "Disabling the plugin!", true);
            Bukkit.getPluginManager().disablePlugin(this);
        }
        Bukkit.getLogger().info("ToppeCheat by Toppe5 was enabled in " + (System.currentTimeMillis() - st) + " ms.");
    }

    private void registerCManager() {
		/*
		if(Bukkit.getVersion().contains("1.7")) {
			CManagerOlder.register(this);
		}
		else {
			CManagerNewer.register(this);
		}
		 */
        CManagerNewer.register(this);
    }

    private void unregisterAllPlayers() {
		/*
		if(Bukkit.getVersion().contains("1.7")) {
			CManagerOlder.getChannelManager().unregisterAllPlayers();
		}
		else {
			CManagerNewer.getChannelManager().unregisterAllPlayers();
		}
		*/
        CManagerNewer.getChannelManager().unregisterAllPlayers();
    }

    private void registerAllPlayers() {
		/*
		if(Bukkit.getVersion().contains("1.7")) {
			CManagerOlder.getChannelManager().registerAllPlayers();
		}
		else {
			CManagerNewer.getChannelManager().registerAllPlayers();
		}
		 */
        CManagerNewer.getChannelManager().registerAllPlayers();
    }

    private void setupMySQL() {
        if (getConfig().getBoolean("mysql.enabled")) {
            String host = getConfig().getString("mysql.host");
            int port = getConfig().getInt("mysql.port");
            String user = getConfig().getString("mysql.user");
            String password = getConfig().getString("mysql.password");
            String name = getConfig().getString("mysql.name");
            mySQL = new MySQL(host, port, user, password, name, this);
            mySQL.connect();
            mySQL.createTable();
            isMySQL = true;
        }
    }

    /**
     * Gets a MetadataValue of a metadatable.
     *
     * @param m   Metadatable
     * @param tag the Metadata tag.
     *
     * @return if found the MetadataValue, otherwise false.
     */
    public MetadataValue getMeta(Metadatable m, String tag) {
        for (MetadataValue mv : m.getMetadata(tag)) {
            if (mv.getOwningPlugin() != null) {
                if (mv.getOwningPlugin().equals(this)) {
                    return mv;
                }
            }
        }
        return null;
    }

    /**
     * Gets the FileManager of the ToppeCheat.
     *
     * @return the FileManager of the ToppeCheat
     */
    public FileManager getFileManager() {
        return fileManager;
    }

    /**
     * Gets the check manager.
     *
     * @return the CheckManager of the ToppeCheat.
     */
    public CheckManager getCheckManager() {
        return checkManager;
    }

    /**
     * Gets the LagMeter of ToppeCheat
     *
     * @return the LagMeter of ToppeCheat
     */
    public LagMeter getLagMeter() {
        return lagMeter;
    }

    public boolean isMySQL() {
        return isMySQL;
    }

    /**
     * Gets the MySQL system
     *
     * @return the MySQL
     */
    public MySQL getMySQL() {
        return mySQL;
    }

    public CheckBypass getCheckBypassManager() {
        return checkBypassManager;
    }

    public StaffCommandManager getCommandManager() {
        return commandManager;
    }

    public NMSProvider getNMSAccessProvider() {
        return nmsProvider;
    }
}
