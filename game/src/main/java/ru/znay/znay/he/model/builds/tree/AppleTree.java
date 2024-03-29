package ru.znay.znay.he.model.builds.tree;

import ru.znay.znay.he.gfx.helper.BitmapHelper;
import ru.znay.znay.he.gfx.helper.PaletteHelper;
import ru.znay.znay.he.gfx.model.Screen;
import ru.znay.znay.he.gfx.sprite.SpriteWrapper;
import ru.znay.znay.he.model.Player;
import ru.znay.znay.he.model.item.Item;
import ru.znay.znay.he.model.item.ItemEntity;
import ru.znay.znay.he.model.item.resource.Resource;
import ru.znay.znay.he.model.item.resource.ResourceItem;
import ru.znay.znay.he.model.level.tile.Tile;

/**
 * Created by IntelliJ IDEA.
 * User: Денис Сергеевич
 * Date: 22.03.12
 * Time: 17:47
 * To change this template use File | Settings | File Templates.
 */
public class AppleTree extends Tree {

    private boolean apple = false;
    private long time = System.currentTimeMillis() + 45000;

    public AppleTree(int x, int y) {
        super(x, y, 16, 12);
    }

    private void wrapSprite(boolean drawAura) {

        level.getSpriteCollector().resetWrappers();
        level.getSpriteCollector().addWrapper(new SpriteWrapper(17 * Tile.HALF_SIZE, 4 * Tile.HALF_SIZE, 4 * Tile.HALF_SIZE, 4 * Tile.HALF_SIZE, PaletteHelper.getColor(20, 40, 30, -1)));
        level.getSpriteCollector().addWrapper(new SpriteWrapper(21 * Tile.HALF_SIZE, 4 * Tile.HALF_SIZE, 4 * Tile.HALF_SIZE, 4 * Tile.HALF_SIZE, PaletteHelper.getColor(10, 10, 20, -1)));
        level.getSpriteCollector().addWrapper(new SpriteWrapper(25 * Tile.HALF_SIZE, 4 * Tile.HALF_SIZE, 4 * Tile.HALF_SIZE, 4 * Tile.HALF_SIZE, PaletteHelper.getColor(100, 210, 320, -1)));
        if (apple) {
            level.getSpriteCollector().addWrapper(new SpriteWrapper(17 * Tile.HALF_SIZE, 0 * Tile.HALF_SIZE, 4 * Tile.HALF_SIZE, 4 * Tile.HALF_SIZE, PaletteHelper.getColor(310, 400, 510, -1)));
        }
        this.sprite = level.getSpriteCollector().mergedWrappers("tree" + (apple ? "_apple" : ""), 2, 0, (drawAura) ? 0x01000000 : 0);

    }


    @Override
    public boolean interact(Item item, Player player, int dir) {
        if (!apple) {
            return false;
        }


        time = System.currentTimeMillis() + 120000;

        apple = false;

        wrapSprite(true);

        for (int i = 0; i < random.nextInt(3) + 4; i++) {
            level.add(new ItemEntity(new ResourceItem(Resource.apple), x + random.nextInt(31) - 15, y + random.nextInt(31)));
        }

        return false;
    }


    @Override
    public void render(Screen screen) {

        wrapSprite(false);

        /*if (mouseMotion && apple) {
            wrapSprite(true);
        }*/

        int xt = (x - xr * 2) - screen.getXOffset();
        int yt = (y - yr * 2 - 24) - screen.getYOffset();

        BitmapHelper.drawNormal(sprite, xt, yt, screen.getViewPort(), 0xFF00FF);
    }

    @Override
    public void tick() {
        super.tick();

        if (time > System.currentTimeMillis() || apple) return;
        apple = true;
        wrapSprite(true);
    }
}
