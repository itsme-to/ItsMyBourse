package fr.maxlego08.bourse.zcore.utils.commands;

import java.util.function.BiConsumer;

import fr.maxlego08.bourse.BoursePlugin;
import fr.maxlego08.bourse.command.VCommand;

public class ZCommand extends VCommand {

	public ZCommand(BoursePlugin plugin) {
		super(plugin);
	}

	private BiConsumer<VCommand, BoursePlugin> command;

	@Override
	public CommandType perform(BoursePlugin main) {
		
		if (command != null){
			command.accept(this, main);
		}

		return CommandType.SUCCESS;
	}

	public VCommand setCommand(BiConsumer<VCommand, BoursePlugin> command) {
		this.command = command;
		return this;
	}

	public VCommand sendHelp(String command) {
		this.command = (cmd, main) -> main.getCommandManager().sendHelp(command, cmd.getSender());
		return this;
	}

}
