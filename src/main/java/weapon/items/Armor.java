package weapon.items;


import cn.nukkit.Player;
import cn.nukkit.PlayerFood;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Config;
import me.onebone.economyapi.EconomyAPI;
import weapon.RsWeapon;
import weapon.players.effects.BaseEffect;

import java.io.File;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Armor extends BaseItem{

    private static String tagName = "RsWeapon_Armor";

    private int armor;

    private String nameTag;


    private int updata;

    private BlockColor rgb;
    /**
     * 限制等级(rpg): 0
     * 限制职业(属性): ""
     * 限制评级: 0
     * */

    private int rpgLevel;

    private boolean isAutoMaster;

    private String rpgAttribute;

    private boolean canShow = false;

    private int rpgPF;

    private double dKick;

    private int health;

    private int toDamage;

    private int level;

    private boolean unBreak;


    private LinkedList<GemStone> gemStoneLinkedList = new LinkedList<>();

    private Armor(Item item){
        this.item = item;
        this.init();
    }




    private Armor(Item item, int armor ,double dKick, int health,int toDamage,int level,int count,boolean unBreak){
        this.item = item;
        this.armor = armor;
        this.level = level;
        this.count = count;
        this.unBreak = unBreak;
        this.dKick = dKick;
        this.health = health;
        this.toDamage = toDamage;

    }


    public void setNameTag(String nameTag) {
        this.nameTag = nameTag;
    }

    public String getNameTag() {
        return nameTag;
    }

    public BlockColor getRgb() {
        return rgb;
    }


    /**解析Tag*/
    private void init(){
        CompoundTag tag = item.getNamedTag();
        this.name = tag.getString(tagName+"name");
        ListTag tags = tag.getList(tagName+"Gem");
        gemStoneLinkedList = this.getGemStoneByTag(tags);
        reload(tag);
    }

    public static Armor getInstance(Item item){
        if(isArmor(item)){
            return new Armor(item);
        }
        return null;
    }

    public void setCanShow(boolean canShow) {
        this.canShow = canShow;
    }

    public boolean isCanShow() {
        return canShow;
    }

    @Override
    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public int getHealth() {
        if(item != null) {
            if(item.hasCompoundTag()) {
                int[] tag1 = item.getNamedTag().getIntArray(tagName + "levelUp");
                if (tag1.length > 1) {
                    int tup = tag1[2];
                    return this.health + tup;
                }
            }
        }
        return health;
    }

    public int getArmor() {
        if(item != null) {
            if(item.hasCompoundTag()) {
                int[] tag1 = item.getNamedTag().getIntArray(tagName + "levelUp");
                if (tag1.length > 1) {
                    int tup = tag1[1];
                    return this.armor + tup;
                }
            }
        }
        return armor;
    }

    public int getCount() {
        return count;
    }

    public double getDKick() {
        return dKick;
    }

    public int getToDamage() {
        return toDamage;
    }

    public boolean isUnBreak() {
        return unBreak;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public static boolean inArray(String name) {
        File gem = new File(RsWeapon.getInstance().getArmorFile()+"/"+name+".yml");
        return gem.exists();
    }

    @Override
    public CompoundTag getCompoundTag() {
        CompoundTag tag = item.getNamedTag();
        if(tag == null){
            tag = new CompoundTag();
        }
        tag = super.getCompoundTag(tag,unBreak,name,tagName,gemStoneLinkedList);
        if(master != null){
            tag.putString(tagName+"master",master);
        }else if(tag.contains(tagName+"master")){
            tag.remove(tagName+"master");
        }
        return tag;
    }

    @Override
    public Item toItem() {
        Item items = Item.get(this.item.getId(),this.item.getDamage());
        CompoundTag tag = getCompoundTag();
        reload(tag);
        items = getItemName(items,tag,this.nameTag,tagName);
        items.setLore(lore());
        items.setCount(1);
        return items;
    }

    private void reload(CompoundTag tag){
        Armor armor = Armor.getInstance(this.name);
        if(armor != null) {
            this.config = armor.config;
            this.level = armor.level;
            this.money = armor.money;
            this.dKick = armor.dKick;
            this.armor = armor.armor;
            this.health = armor.health;
            this.toDamage = armor.toDamage;
            this.count = armor.count;
            this.type = armor.type;
            this.levelUp = armor.levelUp;
            this.message = armor.message;
            this.canShow = armor.canShow;
            this.rgb = armor.rgb;
            this.unBreak = armor.unBreak;
            this.rpgPF = armor.rpgPF;
            this.rpgLevel = armor.rpgLevel;
            this.updata = armor.updata;
            this.rpgAttribute = armor.rpgAttribute;
            this.nameTag = armor.nameTag;
            for (GemStone stone : gemStoneLinkedList) {
                this.armor += stone.getArmor();
                this.health += stone.getHealth();
                this.dKick += stone.getDKick();
                this.toDamage += stone.getToDamage();
            }
            if(tag.contains(tagName+"master")){
                this.master = tag.getString(tagName+"master");
            }
            if(tag.contains(tagName+"levelUp")){
                int[] tag1 = tag.getIntArray(tagName+"levelUp");
                this.levelUp = tag1[0];
            }
            
            if (tag.contains(tagName + "upData")) {
                for (int level = 0; level < tag.getInt(tagName + "upData"); level++) {
                    int add1 = (int) getUpDataAttribute("强化加成.护甲值",this.armor);
                    int add2 = (int) getUpDataAttribute("强化加成.增加血量",this.health);
                    int add3 = (int) getUpDataAttribute("强化加成.反伤",this.toDamage);
                    int add4 = (int) getUpDataAttribute("强化加成.抗击退",this.dKick);
                    if (add1 > 0) {
                        this.armor += add1;
                    }
                    if(add2 > 0) {
                        this.health += add2;
                    }
                    if(add3 > 0){
                        this.toDamage += add3;
                    }
                    if(add4 > 0){
                        this.dKick += add4;
                    }

                }
            }
        }
    }

    
    

    

    @Override
    public String[] lore(){
        ArrayList<String> lore = new ArrayList<>();

        lore.add("§r§2稀有度:  §r"+RsWeapon.getInstance().getLevelUpByString(levelUp).getName());
        lore.add("§r§f═§7╞════════════╡§f═");
        if(master != null){
            lore.add("§r§6◈§a§l主人: §e"+master);
        }else{
            lore.add("§r§6◈§c§l未绑定");
        }
        lore.add("§r§6◈类型   ◈§e"+type);
        lore.add("§r§6◈耐久   ◈"+(unBreak?"§7无限耐久":"§c会损坏"));
        lore.add("§r§6◈品阶   ◈"+RsWeapon.levels.get(level).getName());
        lore.addAll(getListByRPG(rpgLevel,rpgAttribute,rpgPF,message.trim()));
        lore.add("§r§6◈§7护甲§6◈    §b+"+armor+" §a(+"+(getArmor() - armor)+")");
        lore.add("§r§6◈§7韧性§6◈    §b+"+(
                String.format("%.1f",dKick))+" §a(+"+ String.format("%.1f",(getDKick() - dKick))+")");
        lore.add("§r§6◈§7血量§6◈    §b+"+health+" §a(+"+(getHealth() - health)+")");
        lore.add("§r§6◈§7反伤§6◈    §b+"+getToDamage() +"");
        lore.add("§r§6◈§7宝石§6◈ §7(§a"+(getCount() - getGemStones().size())+"§7)§6◈ "+getStoneString(gemStoneLinkedList));
        lore.add("§r§f═§7╞════════════╡§f═");
        lore.add(skillToString(gemStoneLinkedList,false));
        return lore.toArray(new String[0]);
    }

    public static void generateArmor(String name,String id){
        if(!Armor.inArray(name)){
            RsWeapon.getInstance().saveResource("armor.yml","/Armor/"+name+".yml",false);
            Config config = new Config(RsWeapon.getInstance().getArmorFile()+"/"+name+".yml",Config.YAML);
            config.set("盔甲外形",id);
            config.save();
            RsWeapon.CaCheArmor.put(name,Armor.getInstance(name));
        }
    }

    /**
     * 盔甲没有主动技能
     * @return 被动效果
     * */
    public LinkedList<BaseEffect> getEffects(){
        LinkedList<BaseEffect> effects = new LinkedList<>(getArmorEffect());
        if(gemStoneLinkedList.size() > 0){
            for (GemStone stone:gemStoneLinkedList) {
                effects.addAll(stone.getArmorEffect());
            }
        }
        return effects;
    }


    @Override
    public String getType() {
        return type;
    }

    private void setRGB(int r, int g, int b){
        rgb = new BlockColor(r,g,b);
    }



    public static boolean isArmor(Item item){
        if(item == null){
            return false;
        }
        if(item.hasCompoundTag()){
            CompoundTag tag = item.getNamedTag();
            if(tag.contains(BaseItem.TAG_NAME)){
                return tag.getString(BaseItem.TAG_NAME).equals(tagName);
            }
        }
        return false;
    }
    private int[] getRarity(){
       return getRarity(-1);
    }

    private int[] getRarity(int i){
        if(i < 0){
            i  = new Random().nextInt(RsWeapon.rarity.size());
        }
        Armor armor = Armor.getInstance(name);
        if(armor != null) {
            Rarity rarity = RsWeapon.getInstance().getLevelUpByString(i);
            return new int[]{i
                    , rarity.getRound((armor.armor))
                    , rarity.getRound(armor.health)
                    , rarity.getRound(armor.toDamage)};
        }
        return new int[0];
    }

    @Override
    public boolean toRarity(int i) {
        CompoundTag tag = item.getNamedTag();
        if(tag == null){
            tag = new CompoundTag();
        }
        tag.putIntArray(Armor.tagName+"levelUp",getRarity(i));
        item.setNamedTag(tag);
        return true;
    }

    @Override
    public boolean toRarity() {
        return toRarity(-1);
    }

    public static Armor getInstance(String name){
        if(Armor.inArray(name)){
            if(!RsWeapon.CaCheArmor.containsKey(name)){
                RsWeapon.CaCheArmor.put(name,toArmor(name));
            }
            return RsWeapon.CaCheArmor.get(name);
        }
        return null;
    }

    public static String getArmorName(Item item){
        if(Armor.isArmor(item)){
            if(item.hasCompoundTag()){
                CompoundTag tag = item.getNamedTag();
                return tag.getString(tagName+"name");
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Armor){
            return ((Armor) obj).getName().equals(name) && ((Armor) obj).type.equals(type);
        }
        return false;
    }


    public LinkedList<GemStone> getGemStones() {
        return gemStoneLinkedList;
    }

    public static Armor toArmor(String name){
        Config config = new Config(RsWeapon.getInstance().getArmorFile()+"/"+name+".yml");
        String id = config.getString("盔甲外形");
        String type = config.getString("类型");
        Item item = BaseItem.toItemByMap(id);
        int armor = config.getInt("护甲值");
        int money = config.getInt("绑定花费",100);
        int health = config.getInt("增加血量");
        int toDamage = config.getInt("反伤");
        double dKick = config.getDouble("抗击退");
        int level = config.getInt("盔甲品阶");
        int updata = config.getInt("强化等级限制",12);
        Object enchantObject = config.get("盔甲附魔");
        if(enchantObject instanceof Map){
            int enchantId = (int) ((Map)enchantObject).get("id");
            int enchantLevel = (int) ((Map)enchantObject).get("level");
            if(enchantLevel > 0){
                Enchantment aura = Enchantment.getEnchantment(enchantId).setLevel(enchantLevel);
                item.addEnchantment(aura);
            }
        }else if(enchantObject instanceof List){
            List<Map> enchant = config.getMapList("盔甲附魔");
            ArrayList<Enchantment> enchants = BaseItem.getEnchant(enchant);
            for (Enchantment aura : enchants){
                item.addEnchantment(aura);
            }
        }
        Map rgb = (Map) config.get("盔甲染色");
        int r = (int) rgb.get("r");
        int g = (int) rgb.get("g");
        int b = (int) rgb.get("b");
        int count = config.getInt("镶嵌数量");
        String message = config.getString("介绍");
        boolean un = config.getBoolean("无限耐久");
        int rpgLevel = config.getInt("限制等级(rpg)",0);
        int rpgPF = config.getInt("限制评级",0);
        String rpgAttribute = config.getString("限制职业(属性)","");
        Armor armor1 = new Armor(item,armor,dKick,health,toDamage,level,count,un);
        armor1.loadSkill(config);
        armor1.config = config;
        armor1.setRpgLevel(rpgLevel);
        armor1.setRpgPF(rpgPF);
        armor1.setRpgAttribute(rpgAttribute);
        Object up = config.get("稀有度");
        int levelUp = 0;
        if(up != null){
            if(up instanceof String){
                if("x".equals(up.toString().toLowerCase())){
                    levelUp = new Random().nextInt(RsWeapon.rarity.size());
                    armor1.setCanUp(true);
                }
            }else if(up instanceof Integer){
                levelUp = (int) up;
            }
        }
        armor1.setLevelUp(levelUp);
        armor1.setRGB(r,g,b);
        armor1.setMessage(message);
        armor1.setType(type);
        armor1.setName(name);
        armor1.setMoney(money);
        armor1.setUpdata(updata);
        armor1.setNameTag(config.getString("名称",name));
        armor1.setCanShow(config.getBoolean("是否在创造背包显示",false));
        return armor1;
    }

    private void setUpdata(int updata) {
        this.updata = updata;
    }

    public int getUpdata() {
        return updata;
    }

    @Override
    public void setMoney(int money) {
        this.money = money;
    }

    @Override
    public int getMoney() {
        return money;
    }

    private void setRpgAttribute(String rpgAttribute) {
        this.rpgAttribute = rpgAttribute;
    }

    private void setRpgLevel(int rpgLevel) {
        this.rpgLevel = rpgLevel;
    }

    private void setRpgPF(int rpgPF) {
        this.rpgPF = rpgPF;
    }

    private void setName(String name) {
        this.name = name;
    }

    private void setMessage(String message) {
        this.message = message;
    }


    @Override
    public boolean inlayStone(GemStone stone){
        if(canInlay(stone)){
            gemStoneLinkedList.add(stone);
            return true;
        }
        return false;
    }


    @Override
    public boolean removeStone(GemStone stone){
        if(canRemove(stone)){
            gemStoneLinkedList.remove(stone);
            return true;
        }
        return false;
    }

    @Override
    public boolean canInlay(GemStone stone){
        return runCanInlay(stone,gemStoneLinkedList,level,count,rpgAttribute,rpgPF);
    }



    @Override
    public boolean canRemove(GemStone stone){
        return gemStoneLinkedList.contains(stone);
    }


    @Override
    public boolean canUpData() {
        CompoundTag tag = this.item.getNamedTag();
        if(tag.contains(tagName+"upData")){
            return tag.getInt(tagName + "upData") == getUpdata();
        }
        return false;
    }

    @Override
    public boolean upData(Player player){
        int money = (int) EconomyAPI.getInstance().myMoney(player);
        if(money < RsWeapon.getInstance().getUpDataMoney(levelUp + 1,item.getNamedTag().getInt(tagName + "upData"))){
            player.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
            player.sendMessage("§r§c抱歉 您的金钱不足 无法强化");
            player.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
        }else{
            if(canUpData()){
                player.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                player.sendMessage("§r§c抱歉 此盔甲无法强化");
                player.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
            }else{
                EconomyAPI.getInstance().reduceMoney(player,RsWeapon.getInstance().getUpDataMoney(levelUp + 1,item.getNamedTag().getInt(tagName + "upData")),true);
                int r = RsWeapon.getInstance().getMathRound(levelUp + 1,item.getNamedTag().getInt(tagName + "upData"));
                if(new Random().nextInt(100) <= r){
                    toUpData(tagName);
                    player.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                    player.sendMessage("§r§e恭喜 盔甲强化成功");
                    player.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                }else{
                    player.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                    player.sendMessage("§r§d强化失败.. 当前成功率: "+r+"％");
                    player.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                }

                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isArmor() {
        return true;
    }

    @Override
    public boolean canSetMaster() {
        return true;
    }

    @Override
    public String getMaster() {
        return master;
    }

    @Override
    public boolean setMaster(String master) {
        this.master = master;
        return true;
    }


    private void setType(String type) {
        this.type = type;
    }


    @Override
    public boolean canUse(Player player){
        return canUse(player,rpgLevel,rpgAttribute,rpgPF) && (master == null || player.getName().equals(master));
    }
}
