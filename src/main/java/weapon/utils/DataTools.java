package weapon.utils;

import cn.nukkit.Player;

import java.util.LinkedList;

/**
 * @author SmallasWater
 * Create on 2021/1/15 11:10
 * Package weapon.utils
 */
public class DataTools {
    public static LinkedList<Player> getAroundPlayers(Player player, int size) {
        LinkedList<Player> explodePlayer = new LinkedList<>();
        for(Player player1: player.level.getPlayers().values()){
            if(player1.getName().equalsIgnoreCase(player.getName())){
                continue;
            }
            if(player1.x < player.x + size && player1.x > player.x - size && player1.z < player.z + size && player1.z > player.z - size && player1.y < player.y + size && player1.y > player.y - size){
                explodePlayer.add(player1);
            }
        }
        return explodePlayer;
    }
}
