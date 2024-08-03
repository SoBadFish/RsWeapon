package weapon.commands;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemColorArmor;
import cn.nukkit.utils.TextFormat;
import weapon.RsWeapon;
import weapon.events.PlayerGetWeaponItemEvent;
import weapon.items.Armor;
import weapon.items.BaseItem;
import weapon.items.GemStone;
import weapon.items.Weapon;
import weapon.utils.RsWeaponSkill;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class WeCommand extends Command {
    public WeCommand(String name) {
        super(name,"§c武器系统","/"+RsWeapon.getInstance().getConfig().getString("自定义指令名称","we")+" help");
        this.setPermission("op");
    }


    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        try {
            if(commandSender.isOp()){
                if(strings.length < 1) {
                    return false;
                }
                List<String> args = Arrays.asList(strings);
                switch (strings[0]){
                    case "help":
                        commandSender.sendMessage("§7=================================");
                        commandSender.sendMessage("§b/"+RsWeapon.getInstance().getConfig().getString("自定义指令名称","we")+" add weapon <武器名称> <ID:Damage> §a添加武器");
                        commandSender.sendMessage("§b/"+RsWeapon.getInstance().getConfig().getString("自定义指令名称","we")+" add armor <盔甲名称> <ID:Damage> <宝石名称(可选)>§a添加盔甲");
                        commandSender.sendMessage("§b/"+RsWeapon.getInstance().getConfig().getString("自定义指令名称","we")+" set <稀有度(整数)> §a设置手持武器/盔甲的稀有度");
                        commandSender.sendMessage("§b/"+RsWeapon.getInstance().getConfig().getString("自定义指令名称","we")+" give weapon <武器名称> <Player> <宝石名称(可选)> <是否绑定(默认true)>§a给玩家武器");
                        commandSender.sendMessage("§b/"+RsWeapon.getInstance().getConfig().getString("自定义指令名称","we")+" give armor <盔甲名称> <Player> <是否绑定(默认true)>§a给玩家盔甲");
                        commandSender.sendMessage("§b/"+RsWeapon.getInstance().getConfig().getString("自定义指令名称","we")+" addItem <宝石名称> <ID:Damage> §a添加宝石");
                        commandSender.sendMessage("§b/"+RsWeapon.getInstance().getConfig().getString("自定义指令名称","we")+" giveItem <宝石名称> <Player> §a给玩家宝石");
                        commandSender.sendMessage("§b/"+RsWeapon.getInstance().getConfig().getString("自定义指令名称","we")+" reload §a重新加载");
                        commandSender.sendMessage("§7=================================");
                        break;
                    case "set":
                        if(commandSender instanceof Player){
                            if(args.size() > 1){
                                try{
                                    int i = Integer.parseInt(args.get(1));
                                    Item item = ((Player) commandSender).getInventory().getItemInHand();
                                    BaseItem item1 = BaseItem.getBaseItem(item);
                                    if(item1 != null) {
                                        if (item1.isWeapon() || item1.isArmor()) {
                                            item1.toRarity(i);
                                            ((Player) commandSender).getInventory().setItemInHand(item1.toItem());
                                            commandSender.sendMessage("§b设置成功");

                                            return true;
                                        }
                                    }
                                    commandSender.sendMessage("§c请手持装备 或者 盔甲");

                                }catch (Exception e){
                                    commandSender.sendMessage("§c请输入正确的稀有度");

                                }
                            }else{
                                return false;
                            }

                        }else{
                            commandSender.sendMessage("§c请在游戏内执行");
                            return false;
                        }

                        break;
                    case "add":
                        String weaponName = args.get(2);
                        String id = args.get(3);
                        switch (args.get(1)){
                            case "weapon":
                                if(Weapon.inArray(weaponName)){
                                    commandSender.sendMessage(TextFormat.RED+"此武器已存在");
                                }else{
                                    Weapon.generateWeapon(weaponName,id);
                                    commandSender.sendMessage(TextFormat.GREEN+"武器创建成功");
                                }
                                break;
                            case "armor":
                                if(Armor.inArray(weaponName)){
                                    commandSender.sendMessage(TextFormat.RED+"此盔甲已存在");
                                }else{
                                    Armor.generateArmor(weaponName,id);
                                    commandSender.sendMessage(TextFormat.GREEN+"盔甲创建成功");
                                }
                                break;
                                default:return false;
                        }
                        break;
                    case "give":
                        String players = args.get(3);
                        String name = args.get(2);
                        Player player = Server.getInstance().getPlayer(players);
                        if(player == null){
                            commandSender.sendMessage(TextFormat.RED+"该玩家不在线");
                            return true;
                        }
                        switch (args.get(1)){
                            case "armor":
                                if(!Armor.inArray(name)){
                                    commandSender.sendMessage(TextFormat.RED+"此盔甲不存在");
                                    return  true;
                                }
                                boolean isMaster = false;
                                Armor armor = Armor.getInstance(name);
                                if(armor != null){
                                    if(armor.isCanUp()){
                                        armor.toRarity();
                                    }

                                    if(args.size() > 4){
                                        if("true".equalsIgnoreCase(args.get(4)) || args.size() > 5){
                                            if("true".equalsIgnoreCase(args.get(4))){
                                                isMaster = true;
                                            }
                                            if(args.size() > 5){
                                                isMaster = Boolean.valueOf(args.get(5));
                                            }
                                        }
                                        String stons = args.get(4);
                                        GemStone stone;
                                        for(String n: stons.split(",")){
                                            stone = GemStone.getInstance(n);
                                            if(stone != null){
                                                if(armor.canInlay(stone)){
                                                    armor.inlayStone(stone);
                                                }
                                            }
                                        }
                                    }

                                    PlayerGetWeaponItemEvent event = new PlayerGetWeaponItemEvent(player,armor,isMaster);
                                    Server.getInstance().getPluginManager().callEvent(event);
                                    Armor armor1 = (Armor) event.getItem();
                                    isMaster = event.isMaster();
                                    if(isMaster){
                                        event.getItem().setMaster(player.getName());
                                    }
                                    Item item = armor1.toItem();
                                    if(item instanceof ItemColorArmor){
                                        ((ItemColorArmor) item).setColor(armor1.getRgb());
                                    }
                                    player.getInventory().addItem(item);
                                    commandSender.sendMessage(TextFormat.GREEN+"给予成功");
                                }else{
                                    commandSender.sendMessage(TextFormat.RED+"给予失败");
                                }
                                break;
                            case "weapon":
                                if(!Weapon.inArray(name)){
                                    commandSender.sendMessage(TextFormat.RED+"此武器不存在");
                                    return  true;
                                }
                                isMaster = false;
                                Weapon weapon = Weapon.getInstance(name);
                                if(weapon != null){
                                    if(weapon.isCanUp()){
                                        weapon.toRarity();
                                    }
                                    if(args.size() > 4){
                                        String stons = args.get(4);
                                        GemStone stone;
                                        for(String n: stons.split(",")){
                                            stone = GemStone.getInstance(n);
                                            if(stone != null){
                                                if(weapon.canInlay(stone)){
                                                    weapon.inlayStone(stone);
                                                }
                                            }
                                        }
                                        if("true".equalsIgnoreCase(args.get(4)) || args.size() > 5){
                                            if("true".equalsIgnoreCase(args.get(4))){
                                                isMaster = true;
                                            }
                                            if(args.size() > 5){
                                                isMaster = Boolean.valueOf(args.get(5));
                                            }
                                        }
                                    }
                                    PlayerGetWeaponItemEvent event = new PlayerGetWeaponItemEvent(player,weapon,isMaster);
                                    Server.getInstance().getPluginManager().callEvent(event);
                                    Weapon weapon1 = (Weapon) event.getItem();
                                    isMaster = event.isMaster();
                                    if(isMaster){
                                        event.getItem().setMaster(player.getName());
                                    }
                                    player.getInventory().addItem(weapon1.toItem());
                                    commandSender.sendMessage(TextFormat.GREEN+"给予成功");
                                }else{
                                    commandSender.sendMessage(TextFormat.RED+"给予失败");
                                }
                                break;
                                default:return false;
                        }

                        break;
                    case "reload":
                        commandSender.sendMessage(TextFormat.GOLD+"重新加载武器盔甲中...");
                        RsWeapon.getInstance().saveDefaultConfig();
                        RsWeapon.getInstance().reloadConfig();
                        RsWeapon.getInstance().loadSkill();
                        RsWeapon.getInstance().loadRarity();
                        RsWeapon.GemStones = new LinkedHashMap<>();
                        RsWeapon.CaCheArmor = new LinkedHashMap<>();
                        RsWeapon.CaCheWeapon = new LinkedHashMap<>();
                        RsWeapon.getInstance().loadArmor();
                        RsWeapon.getInstance().loadWeapon();
                        RsWeapon.getInstance().loadGemStone();
                        RsWeapon.getInstance().loadSuit();
                        RsWeapon.levels = RsWeapon.getInstance().initLevel();
                        commandSender.sendMessage(TextFormat.GOLD+"重新加载完成");
                        break;
                    case "addItem":
                        if(GemStone.inArray(args.get(1))){
                            commandSender.sendMessage(TextFormat.RED+"此宝石已存在");
                            return  false;
                        }
                        GemStone.generateGemStone(args.get(1),args.get(2));
                        commandSender.sendMessage(TextFormat.GREEN+"宝石创建成功");
                        break;
                    case "giveItem":
                        Player player1 = Server.getInstance().getPlayer(args.get(2));
                        if(player1 == null){
                            commandSender.sendMessage(TextFormat.RED+"该玩家不在线");
                            return false;
                        }
                        String name1 = args.get(1);
                        if(!GemStone.inArray(name1)){
                            commandSender.sendMessage(TextFormat.RED+"此宝石不存在");
                            return  false;
                        }
                        GemStone stone = GemStone.getInstance(name1);
                        if(stone != null){
                            PlayerGetWeaponItemEvent event = new PlayerGetWeaponItemEvent(player1,stone,false);
                            Server.getInstance().getPluginManager().callEvent(event);
                            player1.getInventory().addItem(event.getItem().toItem());
                            commandSender.sendMessage(TextFormat.GREEN+"给予成功");
                        }else{
                            commandSender.sendMessage(TextFormat.RED+"给予失败");
                        }
                        break;
                    default:
                        break;
                }
            }
        }catch (ArrayIndexOutOfBoundsException e){
            return false;
        }
        return true;
    }
}
