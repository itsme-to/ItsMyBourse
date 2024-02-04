package fr.maxlego08.bourse.command.commands;

import org.bukkit.entity.Player;

import fr.maxlego08.bourse.BoursePlugin;
import fr.maxlego08.bourse.command.VCommand;
import fr.maxlego08.bourse.zcore.enums.Permission;
import fr.maxlego08.bourse.zcore.utils.commands.CommandType;

public class CommandBourseGive extends VCommand {

	public CommandBourseGive(BoursePlugin plugin) {
		super(plugin);
		this.setPermission(Permission.BOURSE_ADMIN);
		this.addSubCommand("give");
		this.addRequireArg("player");
		this.addRequireArg("name", (a, b) -> plugin.getSellManager().names());
	}

	@Override
	protected CommandType perform(BoursePlugin plugin) {

		Player player = this.argAsPlayer(0);
		plugin.getSellManager().give(this.sender, player, this.argAsString(1));

		return CommandType.SUCCESS;
	}

}
