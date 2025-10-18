# Database

The **Database** plugin provides a seamless way to handle database credentials and creates a pooled connection for other plugins to utilize. With this plugin, developers can easily integrate database functionality into their own plugins without having to worry about managing connections or credentials.

## Installation

To install the Database plugin, follow these steps:

* Download the latest version of the plugin JAR file from the [releases](https://github.com/OvercastCommunity/Database/releases) section of the repository.
* Place the downloaded Database.jar file into the plugins directory of your Minecraft server.
* Edit the `Database/config.yml` with the proper credentials

## Supported Databases
* MySQL
* Redis

## Example of config.yml
```yml
# MySQL Databases
# You can define as many databases as needed with unique identifiers
databases:
  # Primary database
  primary:
    enabled: true
    url: "jdbc:mysql://localhost:3306/minecraft"
    username: "user"
    password: "password"
    max-connections: 10
    timezone: "America/Los_Angeles"

  # Optional: Add more databases as needed
  stats:
    enabled: true
    url: "jdbc:mysql://stats-server:3306/stats"
    username: "stats_user"
    password: "stats_pass"
    max-connections: 5
    timezone: "UTC"

# Redis Configuration
redis:
  enabled: true
  host: "localhost"
  port: 6379
  password: ""
  database: 0
  timeout: 2000
  max-connections: 10
```

## API Setup

To utilize the Database API in your own plugin, follow these steps:

Add the pgm.fyi repo to your Maven pom.xml
```xml
<repositories>
    <repository>
        <id>pgm-repo-snapshots</id>
        <name>PGM Repository</name>
        <url>https://repo.pgm.fyi/snapshots</url>
    </repository>
</repositories>
```

Add the Database plugin as a dependency in your Maven pom.xml
```xml
<dependency>
    <groupId>tc.oc.occ</groupId>
    <artifactId>Database</artifactId>
    <version>2.0.0-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>
```

## API Example

### Modern API (v2.0+)
Below is an example of how to utilize the new Database API:
```java
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.bukkit.plugin.java.JavaPlugin;
import tc.oc.occ.database.Database;

public class YourPlugin extends JavaPlugin {

  private ExecutorService executorService;

  @Override
  public void onEnable() {
    this.executorService = Executors.newFixedThreadPool(5);
    createTable("test", "id INT PRIMARY KEY, some_col VARCHAR(65)");
  }

  @Override
  public void onDisable() {
    if (executorService != null) {
      executorService.shutdown();
    }
  }

  private void createTable(String table, String schema) {
    CompletableFuture.runAsync(() -> {
      // Get connection from default database (first enabled database)
      Optional<Connection> connOpt = Database.get().getConnection();
      if (!connOpt.isPresent()) {
        getLogger().warning("Database is not available!");
        return;
      }

      try (Connection conn = connOpt.get();
           Statement stmt = conn.createStatement()) {
        String sql = "CREATE TABLE IF NOT EXISTS " + table + " (" + schema + ")";
        stmt.execute(sql);
        getLogger().info("Created table: " + table);
      } catch (SQLException e) {
        getLogger().warning("Error creating table " + table + ": " + e.getMessage());
      }
    }, executorService);
  }

  public CompletableFuture<String> getStringFromTestTable(int id) {
    return CompletableFuture.supplyAsync(() -> {
      // Get connection from default database
      Optional<Connection> connOpt = Database.get().getConnection();
      if (!connOpt.isPresent()) {
        return null;
      }

      final String sql = "SELECT * FROM test WHERE id = ?";
      try (Connection conn = connOpt.get();
           PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, id);

        try (ResultSet result = stmt.executeQuery()) {
          if (result.next()) {
            return result.getString("some_col");
          }
        }
      } catch (SQLException e) {
        getLogger().warning("Database issue: " + e.getMessage());
      }
      return null;
    }, executorService);
  }

  // Example: Using a specific named database
  public void saveToStatsDatabase(int playerId, int score) {
    CompletableFuture.runAsync(() -> {
      // Get connection from the "stats" database
      Optional<Connection> connOpt = Database.get().getConnection("stats");
      if (!connOpt.isPresent()) {
        getLogger().warning("Stats database is not available!");
        return;
      }

      try (Connection conn = connOpt.get();
           PreparedStatement stmt = conn.prepareStatement(
               "INSERT INTO player_stats (player_id, score) VALUES (?, ?)")) {
        stmt.setInt(1, playerId);
        stmt.setInt(2, score);
        stmt.executeUpdate();
      } catch (SQLException e) {
        getLogger().warning("Failed to save stats: " + e.getMessage());
      }
    }, executorService);
  }
}
```

### Legacy API (v1.x) - Deprecated
The old API still works for backwards compatibility but is deprecated:
```java
// Old way (deprecated)
Connection conn = Database.get().getConnectionPool().getPool().getConnection();

// New way (recommended)
Optional<Connection> conn = Database.get().getConnection();
// or for a specific database:
Optional<Connection> statsConn = Database.get().getConnection("stats");
```


## License

The **Database** plugin is open source and released under the MIT License. Please review the [LICENSE](https://github.com/OvercastCommunity/Database/blob/dev/LICENSE) file for more details.

