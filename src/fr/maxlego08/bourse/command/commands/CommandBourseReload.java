package fr.maxlego08.bourse.command.commands;

import fr.maxlego08.bourse.BourseManager;
import fr.maxlego08.bourse.BoursePlugin;
import fr.maxlego08.bourse.command.VCommand;
import fr.maxlego08.bourse.zcore.enums.Message;
import fr.maxlego08.bourse.zcore.enums.Permission;
import fr.maxlego08.bourse.zcore.utils.commands.CommandType;

public class CommandBourseReload extends VCommand {

	public CommandBourseReload(BoursePlugin plugin) {
		super(plugin);
		this.setPermission(Permission.BOURSE_RELOAD);
		this.addSubCommand("reload", "rl");
	}

	@Override
	protected CommandType perform(BoursePlugin plugin) {

		plugin.getSavers().forEach(e -> {
			if (!(e instanceof BourseManager)) {
				e.load(plugin.getPersist());
			}
		});
		plugin.loadInventories();

		message(this.sender, Message.RELOAD);

		return CommandType.SUCCESS;
	}

}
