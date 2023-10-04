package tc.oc.occ.database.redis;

import org.bukkit.configuration.Configuration;

public class RedisConfig {

  private static final String KEY = "redis";

  private boolean enabled;
  private String host;
  private String password;
  private int port;
  private int database;
  private int timeout;
  private int maxConnections;

  public RedisConfig(Configuration config) {
    reload(config);
  }

  public void reload(Configuration config) {
    this.enabled = config.getBoolean(KEY + ".enabled");
    this.host = config.getString(KEY + ".host");
    this.password = config.getString(KEY + ".password");
    this.port = config.getInt(KEY + ".port");
    this.database = config.getInt(KEY + ".database");
    this.timeout = config.getInt(KEY + ".timeout");
    this.maxConnections = config.getInt(KEY + ".max-connections");
  }

  public boolean isEnabled() {
    return enabled;
  }

  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }

  public String getPassword() {
    return password;
  }

  public int getDatabase() {
    return database;
  }

  public int getTimeout() {
    return timeout;
  }

  public int getMaxConnections() {
    return maxConnections;
  }
}
