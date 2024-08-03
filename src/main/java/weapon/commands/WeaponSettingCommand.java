package weapon.commands;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

/**
 * @author SmallasWater
 * Create on 2021/2/25 12:26
 * Package weapon.commands
 */
public class WeaponSettingCommand extends Command {
    public WeaponSettingCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        return false;
    }
}
