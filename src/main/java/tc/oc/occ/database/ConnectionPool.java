package tc.oc.occ.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ConnectionPool {

  private String connectionName;
  private HikariDataSource dataSource;

  public ConnectionPool(String name, DatabaseConfig configuration) {
    this.connectionName = name;
    reload(configuration);
  }

  public boolean isEnabled() {
    return getPool() != null;
  }

  @Nullable
  public HikariDataSource getPool() {
    return dataSource;
  }

  public void close() {
    if (this.dataSource != null) {
      this.dataSource.close();
    }
    this.dataSource = null;
  }

  public void reload(DatabaseConfig configuration) {
    close();

    if (!configuration.isEnabled()) return;

    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(configuration.getDatabaseURL());
    config.setUsername(configuration.getDatabaseUsername());
    config.setPassword(configuration.getDatabasePassword());
    config.setMaximumPoolSize(configuration.getDatabaseMaxConnections());
    config.addDataSourceProperty("serverTimezone", configuration.getDatabaseTimezone());
    this.dataSource = new HikariDataSource(config);
  }

  public void sendStatus(CommandSender sender) {
    CompletableFuture.runAsync(
        () -> {
          if (!isEnabled()) {
            sender.sendMessage(
                ChatColor.AQUA + connectionName + " database" + ChatColor.RED + " is not enabled!");
            return;
          }

          try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(5000)) {
              DatabaseMetaData metaData = connection.getMetaData();
              String productName = metaData.getDatabaseProductName();
              String productVersion = metaData.getDatabaseProductVersion();
              String driverName = metaData.getDriverName();
              String driverVersion = metaData.getDriverVersion();
              int maxConnections = dataSource.getMaximumPoolSize();
              int idleConnections = dataSource.getHikariPoolMXBean().getIdleConnections();
              int activeConnections = dataSource.getHikariPoolMXBean().getActiveConnections();
              int totalConnections = dataSource.getHikariPoolMXBean().getTotalConnections();
              long connectionTimeout = dataSource.getConnectionTimeout();
              long validationTimeout = dataSource.getValidationTimeout();

              sender.sendMessage(ChatColor.GREEN + "=== Database Info ===");
              sender.sendMessage(
                  ChatColor.YELLOW + "Product Name: " + ChatColor.GOLD + productName);
              sender.sendMessage(
                  ChatColor.YELLOW + "Product Version: " + ChatColor.GOLD + productVersion);
              sender.sendMessage(ChatColor.YELLOW + "Driver Name: " + ChatColor.GOLD + driverName);
              sender.sendMessage(
                  ChatColor.YELLOW + "Driver Version: " + ChatColor.GOLD + driverVersion);
              sender.sendMessage(ChatColor.DARK_GREEN + "=== Connection Status ===");
              sender.sendMessage(
                  ChatColor.DARK_AQUA + "Max Connections: " + ChatColor.AQUA + maxConnections);
              sender.sendMessage(
                  ChatColor.DARK_AQUA + "Idle Connections: " + ChatColor.AQUA + idleConnections);
              sender.sendMessage(
                  ChatColor.DARK_AQUA
                      + "Active Connections: "
                      + ChatColor.AQUA
                      + activeConnections);
              sender.sendMessage(
                  ChatColor.DARK_AQUA + "Total Connections: " + ChatColor.AQUA + totalConnections);
              sender.sendMessage(
                  ChatColor.DARK_AQUA
                      + "Connection Timeout: "
                      + ChatColor.AQUA
                      + connectionTimeout
                      + "ms");
              sender.sendMessage(
                  ChatColor.DARK_AQUA
                      + "Validation Timeout: "
                      + ChatColor.AQUA
                      + validationTimeout
                      + "ms");
            } else {
              sender.sendMessage(ChatColor.RED + "Database status: Disconnected");
            }
          } catch (SQLException e) {
            sender.sendMessage(ChatColor.RED + "Error checking database status: " + e.getMessage());
          }
        });
  }
}
