package tc.oc.occ.database;

import co.aikar.commands.BukkitCommandManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Database extends JavaPlugin {

  private static Database plugin;

  private DatabaseConfig globalConfig;
  private ConnectionPool globalPool;

  private DatabaseConfig secondaryConfig;
  private ConnectionPool secondaryPool;

  private BukkitCommandManager commands;

  @Override
  public void onEnable() {
    plugin = this;

    this.saveDefaultConfig();
    this.reloadConfig();

    this.globalConfig = new DatabaseConfig("database", getConfig());
    this.globalPool = new ConnectionPool("primary", globalConfig);

    this.secondaryConfig = new DatabaseConfig("secondary-database", getConfig());
    this.secondaryPool = new ConnectionPool("secondary", secondaryConfig);

    this.commands = new BukkitCommandManager(this);
    commands.registerCommand(new DatabaseCommand());
  }

  public ConnectionPool getConnectionPool() {
    return globalPool;
  }

  public ConnectionPool getSecondaryPool() {
    return secondaryPool;
  }

  public static Database get() {
    return plugin;
  }

  public void reload() {
    this.reloadConfig();
    this.globalConfig.reload(getConfig());
    this.globalPool.reload(globalConfig);
    this.secondaryConfig.reload(getConfig());
  }
}
