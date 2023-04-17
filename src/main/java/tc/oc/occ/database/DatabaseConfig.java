package tc.oc.occ.database;

import org.bukkit.configuration.Configuration;

public class DatabaseConfig {

  private String dbURL;
  private String dbUsername;
  private String dbPassword;
  private int dbMaxConnections;
  private String dbTimezone;

  public DatabaseConfig(Configuration config) {
    reload(config);
  }

  public void reload(Configuration config) {
    this.dbURL = config.getString("database.url");
    this.dbUsername = config.getString("database.username");
    this.dbPassword = config.getString("database.password");
    this.dbMaxConnections = config.getInt("database.max-connections");
    this.dbTimezone = config.getString("database.timezone");
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
