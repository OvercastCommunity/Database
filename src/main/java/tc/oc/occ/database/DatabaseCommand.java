package tc.oc.occ.database;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Dependency;
import co.aikar.commands.annotation.Subcommand;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import tc.oc.occ.database.redis.RedisPool;

@CommandAlias("database|db")
@CommandPermission("occ.admin.database")
public class DatabaseCommand extends BaseCommand {

  @Dependency private Database database;

  @Default
  @Subcommand("status")
  public void status(CommandSender sender) {
    sender.sendMessage(ChatColor.GREEN + "=== Database Status ===");

    // Show all MySQL databases
    Map<String, ConnectionPool> pools = database.getAllPools();
    if (pools.isEmpty()) {
      sender.sendMessage(ChatColor.RED + "No databases configured!");
    } else {
      for (Map.Entry<String, ConnectionPool> entry : pools.entrySet()) {
        String name = entry.getKey();
        ConnectionPool pool = entry.getValue();

        sender.sendMessage(
            ChatColor.YELLOW + "Database: " + ChatColor.GOLD + name + ChatColor.RESET);
        pool.sendStatus(sender);
        sender.sendMessage(""); // blank line
      }
    }

    // Show Redis
    sender.sendMessage(ChatColor.YELLOW + "Redis:");
    RedisPool redis = database.getRedisPool();
    if (redis != null && redis.getConnection() != null) {
      sender.sendMessage(ChatColor.GREEN + "  Status: Connected");
      try (redis.clients.jedis.Jedis jedis = redis.getConnection()) {
        sender.sendMessage(ChatColor.AQUA + "  Ping: " + jedis.ping());
      }
    } else {
      sender.sendMessage(ChatColor.RED + "  Status: Disabled");
    }
  }

  @Subcommand("reload")
  public void reload(CommandSender sender) {
    sender.sendMessage(ChatColor.YELLOW + "Reloading database configuration...");
    database.reload();
    sender.sendMessage(ChatColor.GREEN + "Database configuration reloaded!");
  }

  @Subcommand("list")
  public void list(CommandSender sender) {
    sender.sendMessage(ChatColor.GREEN + "=== Configured Databases ===");

    Map<String, ConnectionPool> pools = database.getAllPools();
    if (pools.isEmpty()) {
      sender.sendMessage(ChatColor.RED + "No databases configured!");
      return;
    }

    for (Map.Entry<String, ConnectionPool> entry : pools.entrySet()) {
      String name = entry.getKey();
      ConnectionPool pool = entry.getValue();

      String status = pool.isEnabled() ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED";
      sender.sendMessage(ChatColor.YELLOW + "  - " + name + ": " + status);
    }
  }
}
