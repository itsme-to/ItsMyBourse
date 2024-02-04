package fr.maxlego08.bourse.command.commands;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import fr.maxlego08.bourse.BoursePlugin;
import fr.maxlego08.bourse.command.VCommand;
import fr.maxlego08.bourse.zcore.enums.Message;
import fr.maxlego08.bourse.zcore.enums.Permission;
import fr.maxlego08.bourse.zcore.utils.commands.CommandType;

public class CommandBourseAddSell extends VCommand {

	public CommandBourseAddSell(BoursePlugin plugin) {
		super(plugin);
		this.setPermission(Permission.BOURSE_ADMIN);
		this.addSubCommand("addsell");
		this.addRequireArg("material", (a, b) -> plugin.getMaterials());
		this.addRequireArg("nombre");
	}

	@Override
	protected CommandType perform(BoursePlugin plugin) {

		Material material = Material.valueOf(this.argAsString(0).toUpperCase());

		if (!plugin.getMaterials().contains(material.name().toLowerCase())) {
			return CommandType.SYNTAX_ERROR;
		}

		ItemStack itemStack = new ItemStack(material);
		plugin.getManager().addAmount(itemStack, this.argAsLong(1));

		message(this.sender, Message.ITEM_ADD, "%amount%", this.argAsLong(1), "%name%", material.name().toLowerCase());

		return CommandType.SUCCESS;
	}

}
