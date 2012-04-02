package ru.znay.znay.he.model.builds.tree;

import ru.znay.znay.he.gfx.helper.BitmapHelper;
import ru.znay.znay.he.gfx.helper.PaletteHelper;
import ru.znay.znay.he.gfx.model.Screen;
import ru.znay.znay.he.gfx.sprite.SpriteCollector;
import ru.znay.znay.he.gfx.sprite.SpriteWrapper;
import ru.znay.znay.he.model.Entity;
import ru.znay.znay.he.model.builds.Tree;
import ru.znay.znay.he.model.level.tile.Tile;

/**
 * Created by IntelliJ IDEA.
 * User: ASTarasov
 * Date: 28.03.12
 * Time: 10:14
 * To change this template use File | Settings | File Templates.
 */
public class FirTree extends Tree {

    public FirTree(int x, int y, SpriteCollector spriteCollector) {
        super(x, y, 10, 10);

        spriteCollector.resetWrappers();
        spriteCollector.addWrapper(new SpriteWrapper(17 * Tile.HALF_SIZE, 8 * Tile.HALF_SIZE, 4 * Tile.HALF_SIZE, 8 * Tile.HALF_SIZE, PaletteHelper.getColor(30, 20, 40, -1)));
        spriteCollector.addWrapper(new SpriteWrapper(21 * Tile.HALF_SIZE, 8 * Tile.HALF_SIZE, 4 * Tile.HALF_SIZE, 8 * Tile.HALF_SIZE, PaletteHelper.getColor(10, 20, 10, -1)));
        spriteCollector.addWrapper(new SpriteWrapper(25 * Tile.HALF_SIZE, 8 * Tile.HALF_SIZE, 4 * Tile.HALF_SIZE, 8 * Tile.HALF_SIZE, PaletteHelper.getColor(100, 210, 320, -1)));

        this.sprite = spriteCollector.mergedWrappers("tree_fir", 1, random.nextInt(2), true);
    }

    @Override
    public boolean blocks(Entity entity) {
        return true;
    }

    @Override
    public void render(Screen screen) {

        int xt = (x - xr * 2 - xr - 1) - screen.getXOffset();
        int yt = (y - (yr << 2) * 2 - (yr << 2) + 4) - screen.getYOffset();

        BitmapHelper.drawNormal(sprite, xt, yt, screen.getViewPort(), 0xFF00FF);

    }
}
