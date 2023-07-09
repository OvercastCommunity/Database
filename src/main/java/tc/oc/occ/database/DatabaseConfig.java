package tc.oc.occ.database;

import org.bukkit.configuration.Configuration;

public class DatabaseConfig {

  private final String key;

  private boolean enabled;
  private String dbURL;
  private String dbUsername;
  private String dbPassword;
  private int dbMaxConnections;
  private String dbTimezone;

  public DatabaseConfig(String key, Configuration config) {
    this.key = key;
    reload(config);
  }

  public void reload(Configuration config) {
    this.enabled = config.getBoolean(key + ".enabled");
    this.dbURL = config.getString(key + ".url");
    this.dbUsername = config.getString(key + ".username");
    this.dbPassword = config.getString(key + ".password");
    this.dbMaxConnections = config.getInt(key + ".max-connections");
    this.dbTimezone = config.getString(key + ".timezone");
  }

  public boolean isEnabled() {
    return enabled;
  }

  public String getDatabaseURL() {
    return dbURL;
  }

  public String getDatabaseUsername() {
    return dbUsername;
  }

  public String getDatabasePassword() {
    return dbPassword;
  }

  public int getDatabaseMaxConnections() {
    return dbMaxConnections;
  }

  public String getDatabaseTimezone() {
    return dbTimezone;
  }
}
