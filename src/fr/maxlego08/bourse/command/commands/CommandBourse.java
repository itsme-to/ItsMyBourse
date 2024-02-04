package fr.maxlego08.bourse.command.commands;

import org.bukkit.entity.Player;

import fr.maxlego08.bourse.BoursePlugin;
import fr.maxlego08.bourse.command.VCommand;
import fr.maxlego08.bourse.save.Config;
import fr.maxlego08.bourse.zcore.enums.Permission;
import fr.maxlego08.bourse.zcore.utils.commands.CommandType;

public class CommandBourse extends VCommand {

	public CommandBourse(BoursePlugin plugin) {
		super(plugin);
		this.addRequireArg("categorie", (a, b) -> Config.categories);
		this.addRequireArg("joueur");
		this.setPermission(Permission.BOURSE_ADMIN);
		this.addSubCommand(new CommandBourseReload(plugin));
		this.addSubCommand(new CommandBourseAddSell(plugin));
		this.addSubCommand(new CommandBourseGive(plugin));
	}

	@Override
	protected CommandType perform(BoursePlugin plugin) {

		String category = this.argAsString(0);
		Player player = this.argAsPlayer(1);
		plugin.getManager().openBourse(player, category);

		return CommandType.SUCCESS;
	}

}
