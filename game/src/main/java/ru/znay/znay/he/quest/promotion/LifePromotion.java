package ru.znay.znay.he.quest.promotion;

import ru.znay.znay.he.model.Player;

/**
 * Created by IntelliJ IDEA.
 * User: Александр Сергеевич
 * Date: 06.04.12
 * Time: 20:20
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
public class LifePromotion implements QuestPromotion {

    private int life;

    @Override
    public void promotion(Player player) {
        player.setHealth(player.getHealth() + life);
    }

    public int getLife() {
        return life;
    }

    @Override
    public void setParam(String param) {
    }

    @Override
    public void setCount(String count) {
        this.life = Integer.decode(count);
    }
}
