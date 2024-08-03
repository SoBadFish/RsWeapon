package weapon.utils;

import weapon.players.effects.BaseEffect;

import java.util.LinkedList;

/**
 * @author SmallasWater
 * 2022/2/6
 */
public class SkillManager {

    public LinkedList<BaseEffect> effect = new LinkedList<>();

    public LinkedList<BaseEffect> damages = new LinkedList<>();

    public LinkedList<BaseEffect> getDamages() {
        return damages;
    }

    public LinkedList<BaseEffect> getEffect() {
        return effect;
    }
}
