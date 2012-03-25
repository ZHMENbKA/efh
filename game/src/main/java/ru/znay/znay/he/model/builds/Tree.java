package ru.znay.znay.he.model.builds;

import ru.znay.znay.he.gfx.helper.PaletteHelper;
import ru.znay.znay.he.gfx.model.Screen;
import ru.znay.znay.he.model.ETeam;
import ru.znay.znay.he.model.Entity;
import ru.znay.znay.he.model.Mob;
import ru.znay.znay.he.model.level.tile.Tile;

/**
 * Created by IntelliJ IDEA.
 * User: denus
 * Date: 22.03.12
 * Time: 17:47
 * To change this template use File | Settings | File Templates.
 */
public class Tree extends Mob {

    //яблоки на потом оставлю
    private int flip = 0;

    public Tree() {
        this.xr = 16;
        this.yr = 12;
        this.team = ETeam.ENEMY_TEAM;
        this.flip = random.nextInt(1);

    }

    @Override
    public void touchedBy(Entity entity) {

    }

    @Override
    public boolean blocks(Entity entity) {
        return true;
    }

    public void render(Screen screen) {

        int xt = x - xr * 2;
        int yt = y - yr * 2 - 24;

        int col = PaletteHelper.getColor(20, 40, 30, -1);
        screen.render(2, xt, yt, 17 * Tile.HALF_SIZE, 4 * Tile.HALF_SIZE, 4 * Tile.HALF_SIZE, 4 * Tile.HALF_SIZE, col, flip);

        col = PaletteHelper.getColor(10, 10, 20, -1);
        screen.render(2, xt, yt, 21 * Tile.HALF_SIZE, 4 * Tile.HALF_SIZE, 4 * Tile.HALF_SIZE, 4 * Tile.HALF_SIZE, col, flip);

        col = PaletteHelper.getColor(100, 210, 320, -1);
        screen.render(2, xt, yt, 25 * Tile.HALF_SIZE, 4 * Tile.HALF_SIZE, 4 * Tile.HALF_SIZE, 4 * Tile.HALF_SIZE, col, flip);
    }
}
