package com.toppecraft.toppecheat.playerdata;

import com.toppecraft.toppecheat.ToppeCheat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * MySQL system. I'm not very familiar with the MySQL things so I'm sorry if it's badly made now.
 *
 * @author Toppe5
 * @since 2.o
 */
public class MySQL {


    public static final String TABLE = "";
    public static final String MESSAGE_COLUMN = "message";

    private String host;
    private int port;
    private String user;
    private String password;
    private String db;
    private Connection conn;
    private ToppeCheat plugin;

    /**
     * Create a new MySQL with the given values.
     *
     * @param host
     * @param port
     * @param user
     * @param password
     * @param name
     */
    public MySQL(String host, int port, String user, String password, String name, ToppeCheat plugin) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        this.db = name;
        this.plugin = plugin;
    }

    public boolean isConnected(boolean reconnect) {
        try {
            if (conn != null && !conn.isClosed() && conn.isValid(5000)) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (reconnect) {
            connect();
        }
        return false;
    }

    public void connect() {
        if (!isConnected(false)) {
            try {
                conn = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" +
                        db, user, password);
            } catch (SQLException e) {
                Bukkit.getLogger().warning("There was an error while connecting to the MySQL...");
                e.printStackTrace();
                return;
            }
        }
    }

    /**
     * Creates the table
     */
    public void createTable() {
        if (isConnected(true)) {
            query("CREATE TABLE IF NOT EXISTS " + TABLE + " (uuid VARCHAR(100) NOT NULL, username VARCHAR(100) NOT NULL, " + MESSAGE_COLUMN
                    + " VARCHAR(1000) NOT NULL)");
        }
    }


    public void createLog(UUID uuid, String log) {
        String username = null;
        queryAsync("INSERT INTO " + TABLE + " (UUID, username, " + MESSAGE_COLUMN + ") VALUES('" + uuid + "', '" + username + "', '" + log + "')");
    }

    /**
     * Closes the connection
     */
    public void close() {
        try {
            if (isConnected(false)) {
                conn.close();
                conn = null;
            }
        } catch (SQLException localSQLException) {
        }
    }

    /**
     * Query some stuff
     *
     * @param q the query
     */
    public void query(String q) {
        query(q, true);
    }

    /**
     * Query some stuff
     *
     * @param q     the query
     * @param first true if it's first try and if it fails it will try second time
     */
    private void query(final String q, boolean first) {
        if (isConnected(true)) {
            PreparedStatement ps = null;
            try {
                ps = conn.prepareStatement(q);
                ps.execute();
            } catch (SQLException e) {
                if (first) {
                    query(q, false);
                } else {
                    e.printStackTrace();
                    if (!Bukkit.isPrimaryThread()) {
                        Bukkit.getScheduler().runTask(ToppeCheat.getInstance(), new Runnable() {

                            @Override
                            public void run() {
                                for (Player pl : Bukkit.getOnlinePlayers()) {
                                    if (pl.hasPermission("toppecheat.admin")) {
                                        pl.sendMessage(ChatColor.RED + "Failed to update data "
                                                + "(Query:" + q + ")");
                                    }
                                }
                            }
                        });
                    } else {
                        for (Player pl : Bukkit.getOnlinePlayers()) {
                            if (pl.hasPermission("toppecheat.admin")) {
                                pl.sendMessage(ChatColor.RED + "Failed to update data "
                                        + "(Query:" + q + ")");
                            }
                        }
                    }
                }
            } finally {
                if (ps != null) {
                    try {
                        ps.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Get a result
     *
     * @param q the query
     *
     * @return the ResultSet
     */
    public ResultSet getResult(final String q) {
        if (isConnected(true)) {
            Statement ps = null;
            ResultSet rs = null;
            try {
                ps = conn.createStatement();
                rs = ps.executeQuery(q);
                return rs;
            } catch (SQLException e) {
                e.printStackTrace();
                if (!Bukkit.isPrimaryThread()) {
                    Bukkit.getScheduler().runTask(ToppeCheat.getInstance(), new Runnable() {

                        @Override
                        public void run() {
                            for (Player pl : Bukkit.getOnlinePlayers()) {
                                if (pl.hasPermission("toppecheat.admin")) {
                                    pl.sendMessage(ChatColor.RED + "Failed to collect data "
                                            + "(Query: " + q + ")");
                                }
                            }
                        }
                    });
                } else {
                    for (Player pl : Bukkit.getOnlinePlayers()) {
                        if (pl.hasPermission("toppecheat.admin")) {
                            pl.sendMessage(ChatColor.RED + "Failed to update data "
                                    + "(Query:" + q + ")");
                        }
                    }
                }
            } finally {
                if (ps != null) {
                    try {
                        ps.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }

    public void get(final UUID uuid, final String s, final Callback callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {

            @Override
            public void run() {
                final Object result = get(uuid, s);
                Bukkit.getScheduler().runTask(plugin, new Runnable() {

                    @Override
                    public void run() {
                        callback.onSuccess(result);
                    }
                });
            }
        });
    }

    public String get(UUID uuid, String s) {
        ResultSet rs = plugin.getMySQL().getResult("SELECT * FROM " +
                MySQL.TABLE + " WHERE UUID='" +
                uuid + "'");
        try {
            if (rs.next()) {
                return rs.getString(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<UUID> getUUIDs() {
        List<UUID> uuids = new ArrayList<UUID>();
        ResultSet rs = plugin.getMySQL().getResult("SELECT UUID FROM " + MySQL.TABLE);
        try {
            while (rs.next()) {
                try {
                    uuids.add(UUID.fromString(rs.getString("UUID")));
                } catch (IllegalArgumentException e) {
                    Bukkit.getLogger().warning("Illegal UUID in the TAC 2.0 MySQL table: " + rs.getString("UUID"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return uuids;
    }

    public boolean exists(UUID uuid) {
        ResultSet res = plugin.getMySQL().getResult("SELECT * FROM " + MySQL.TABLE + " WHERE UUID='" + uuid + "'");
        try {
            if (res.last()) {

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void queryAsync(final String query) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {

            @Override
            public void run() {
                plugin.getMySQL().query(query);
            }
        });
    }
}

