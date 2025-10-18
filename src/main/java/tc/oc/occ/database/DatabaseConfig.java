package tc.oc.occ.database;

import org.bukkit.configuration.ConfigurationSection;

public class DatabaseConfig {

  private final String id;

  private boolean enabled;
  private String dbURL;
  private String dbUsername;
  private String dbPassword;
  private int dbMaxConnections;
  private String dbTimezone;

  public DatabaseConfig(String id, ConfigurationSection section) {
    this.id = id;
    reload(section);
  }

  public void reload(ConfigurationSection section) {
    this.enabled = section.getBoolean("enabled");
    this.dbURL = section.getString("url");
    this.dbUsername = section.getString("username");
    this.dbPassword = section.getString("password");
    this.dbMaxConnections = section.getInt("max-connections");
    this.dbTimezone = section.getString("timezone");
  }

  public String getId() {
    return id;
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
