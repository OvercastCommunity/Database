package tc.oc.occ.database;

import co.aikar.commands.BukkitCommandManager;
import org.bukkit.plugin.java.JavaPlugin;
import tc.oc.occ.database.redis.RedisConfig;
import tc.oc.occ.database.redis.RedisPool;

public class Database extends JavaPlugin {

  private static Database plugin;

  // Global MySQL
  private DatabaseConfig globalConfig;
  private ConnectionPool globalPool;

  // Secondary MySQL
  private DatabaseConfig secondaryConfig;
  private ConnectionPool secondaryPool;

  private RedisConfig redisConfig;
  private RedisPool redisPool;

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

    this.redisConfig = new RedisConfig(getConfig());
    this.redisPool = new RedisPool(redisConfig);

    this.commands = new BukkitCommandManager(this);
    commands.registerCommand(new DatabaseCommand());
  }

  public ConnectionPool getConnectionPool() {
    return globalPool;
  }

  public ConnectionPool getSecondaryPool() {
    return secondaryPool;
  }

  public RedisPool getRedisPool() {
    return redisPool;
  }

  public static Database get() {
    return plugin;
  }

  public void reload() {
    this.reloadConfig();
    this.globalConfig.reload(getConfig());
    this.globalPool.reload(globalConfig);
    this.secondaryConfig.reload(getConfig());
    this.secondaryPool.reload(secondaryConfig);
    this.redisConfig.reload(getConfig());
    this.redisPool.reload(redisConfig);
  }
}
