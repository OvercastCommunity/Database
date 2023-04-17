package tc.oc.occ.database;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Dependency;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.command.CommandSender;

@CommandAlias("database|db")
@CommandPermission("occ.admin.database")
public class DatabaseCommand extends BaseCommand {

  @Dependency private Database database;

  @Subcommand("reload")
  public void reload(CommandSender sender) {
    database.reload();
  }

  @Subcommand("status")
  public void status(CommandSender sender) {
    database.getConnectionPool().sendStatus(sender);
  }
}
