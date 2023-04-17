package tc.oc.occ.database;

import co.aikar.commands.BukkitCommandManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Database extends JavaPlugin {

  private static Database plugin;

  private DatabaseConfig config;
  private ConnectionPool globalPool;
  private BukkitCommandManager commands;

  @Override
  public void onEnable() {
    plugin = this;

    this.saveDefaultConfig();
    this.reloadConfig();

    this.config = new DatabaseConfig(getConfig());
    this.globalPool = new ConnectionPool(config);

    this.commands = new BukkitCommandManager(this);
    commands.registerCommand(new DatabaseCommand());
  }

  public ConnectionPool getConnectionPool() {
    return globalPool;
  }

  public static Database get() {
    return plugin;
  }

  public void reload() {
    this.reloadConfig();
    this.config.reload(getConfig());
    this.globalPool.reload(config);
  }
}
