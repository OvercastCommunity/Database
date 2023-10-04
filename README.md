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
# Database configuration
database:
  # JDBC URL
  url: "jdbc:mysql://localhost:3306/minecraft"
  # MySQL username
  username: "user"
  # MySQL password
  password: "password"
  # Maximum number of connections in the connection pool
  max-connections: 10
  # Timezone of database
  timezone: "America/Los_Angeles"
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
    <version>1.2.0-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>
```

## API Example
Below is an example of how to utilize the Database API:
```java
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;
import tc.oc.occ.database.Database;

public class YourPlugin extends JavaPlugin {

  private Logger logger;
  private ExecutorService executorService;

  @Override
  public void onEnable() {
    this.executorService = Executors.newFixedThreadPool(5);
    createTable("test", "id INT PRIMARY KEY, some_col VARCHAR(65),");
  }

  private Connection getConnection() throws SQLException {
    return Database.get().getConnectionPool().getPool().getConnection();
  }

  private void createTable(String table, String schema) {
    CompletableFuture.runAsync(
        () -> {
          try (Connection connection = getConnection();
              Statement statement = connection.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS " + table + " (" + schema + ");";
            statement.execute(sql);
            logger.info("Created table: " + table);
          } catch (SQLException e) {
            logger.warning("Error creating table " + table + ": " + e.getMessage());
          }
        },
        executorService);
  }

  public CompletableFuture<String> getStringFromTestTable(int id) {
    return CompletableFuture.supplyAsync(
        () -> {
          final String sql = "SELECT * FROM test WHERE id = ?";
          try (Connection connection = getConnection();
              PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);

            try (ResultSet result = statement.executeQuery()) {
              if (result.next()) {
                String testString = result.getString("some_col");
                return testString;
              }
            }

          } catch (SQLException e) {
            logger.warning("Database issue while fetching string from test table");
            e.printStackTrace();
          }
          return null;
        },
        executorService);
  }
```


## License

The **Database** plugin is open source and released under the MIT License. Please review the [LICENSE](https://github.com/OvercastCommunity/Database/blob/dev/LICENSE) file for more details.

