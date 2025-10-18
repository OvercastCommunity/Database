package tc.oc.occ.database;

import co.aikar.commands.BukkitCommandManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;
import tc.oc.occ.database.redis.RedisConfig;
import tc.oc.occ.database.redis.RedisPool;
import com.zaxxer.hikari.HikariDataSource;

public class Database extends JavaPlugin {

  private static Database plugin;

  // Dynamic map of database pools
  private final Map<String, DatabaseConfig> databaseConfigs = new LinkedHashMap<>();
  private final Map<String, ConnectionPool> databasePools = new LinkedHashMap<>();

  private RedisConfig redisConfig;
  private RedisPool redisPool;

  private BukkitCommandManager commands;

  @Override
  public void onEnable() {
    plugin = this;

    this.saveDefaultConfig();
    this.reloadConfig();

    // Load all MySQL databases
    loadDatabases();

    // Load Redis
    this.redisConfig = new RedisConfig(getConfig());
    this.redisPool = new RedisPool(redisConfig);

    this.commands = new BukkitCommandManager(this);
    commands.registerCommand(new DatabaseCommand());
  }

  private void loadDatabases() {
    ConfigurationSection dbSection = getConfig().getConfigurationSection("databases");

    if (dbSection == null) {
      getLogger().warning("No databases configured!");
      return;
    }

    databaseConfigs.clear();
    databasePools.values().forEach(ConnectionPool::close);
    databasePools.clear();

    for (String dbId : dbSection.getKeys(false)) {
      ConfigurationSection dbConfig = dbSection.getConfigurationSection(dbId);
      if (dbConfig == null) continue;

      DatabaseConfig config = new DatabaseConfig(dbId, dbConfig);
      ConnectionPool pool = new ConnectionPool(dbId, config);

      databaseConfigs.put(dbId, config);
      databasePools.put(dbId, pool);

      if (config.isEnabled()) {
        getLogger().info("Loaded database: " + dbId);
      } else {
        getLogger().info("Database '" + dbId + "' is disabled");
      }
    }
  }

  @Override
  public void onDisable() {
    if (commands != null) {
      commands.unregisterCommands();
    }

    // Close all database pools
    databasePools.values().forEach(ConnectionPool::close);

    if (redisPool != null) {
      redisPool.shutdown();
    }
  }

  // NEW CONNECTION API - MySQL
  public Optional<ConnectionPool> getPool(String name) {
    ConnectionPool pool = databasePools.get(name);
    if (pool != null && pool.isEnabled()) {
      return Optional.of(pool);
    }
    return Optional.empty();
  }

  public Optional<ConnectionPool> getPool() {
    return databasePools.values().stream().filter(ConnectionPool::isEnabled).findFirst();
  }

  public Optional<Connection> getConnection(String name) {
    return getPool(name)
        .flatMap(
            pool -> {
              try {
                HikariDataSource ds = pool.getPool();
                return ds != null ? Optional.ofNullable(ds.getConnection()) : Optional.empty();
              } catch (SQLException e) {
                getLogger()
                    .warning("Failed to get connection from '" + name + "': " + e.getMessage());
                return Optional.empty();
              }
            });
  }

  public Optional<Connection> getConnection() {
    return getPool()
        .flatMap(
            pool -> {
              try {
                HikariDataSource ds = pool.getPool();
                return ds != null ? Optional.ofNullable(ds.getConnection()) : Optional.empty();
              } catch (SQLException e) {
                getLogger().warning("Failed to get default connection: " + e.getMessage());
                return Optional.empty();
              }
            });
  }

  public Map<String, ConnectionPool> getAllPools() {
    return Collections.unmodifiableMap(databasePools);
  }

  public Set<String> getPoolNames() {
    return Collections.unmodifiableSet(databasePools.keySet());
  }

  public boolean hasPool(String name) {
    return databasePools.containsKey(name);
  }

  public boolean isPoolEnabled(String name) {
    return getPool(name).isPresent();
  }

  // REDIS - API
  public RedisPool getRedisPool() {
    return redisPool;
  }

  public Optional<Jedis> getRedisConnection() {
    if (redisPool == null) {
      return Optional.empty();
    }
    return Optional.ofNullable(redisPool.getConnection());
  }

  // DEPRECATED API (for backwards compatibility)

  @Deprecated
  public ConnectionPool getConnectionPool() {
    return getPool("primary").orElse(null);
  }

  @Deprecated
  public ConnectionPool getSecondaryPool() {
    return getPool("secondary").orElse(null);
  }

  // UTILS
  public static Database get() {
    return plugin;
  }

  public void reload() {
    this.reloadConfig();
    loadDatabases();
    this.redisConfig.reload(getConfig());
    this.redisPool.reload(redisConfig);
  }
}
