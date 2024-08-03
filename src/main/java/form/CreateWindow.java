package form;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.form.window.FormWindowSimple;

/**
 * @author SmallasWater
 * Create on 2021/2/25 12:27
 * Package form
 */
public class CreateWindow {

    //帽子 胸甲 护腿 靴子 武器
    public static final int MENU = 0xab1001;
    //武器? 选择武器UI : 穿戴 镶嵌宝石 强化 洗练
    public static final int WEAPON_MENU = 0xab1002;

    public static void sendMenu(Player player){
        FormWindowSimple simple = new FormWindowSimple("装备设置","");
        simple.addButton(new ElementButton("帽子",new ElementButtonImageData("path","")));
    }

}
