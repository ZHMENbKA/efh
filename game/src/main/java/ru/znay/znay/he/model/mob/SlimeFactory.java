package ru.znay.znay.he.model.mob;

import ru.znay.znay.he.gfx.helper.PaletteHelper;
import ru.znay.znay.he.gfx.model.Screen;
import ru.znay.znay.he.model.ETeam;
import ru.znay.znay.he.model.Entity;
import ru.znay.znay.he.model.Mob;
import ru.znay.znay.he.model.Player;
import ru.znay.znay.he.model.item.ItemEntity;
import ru.znay.znay.he.model.item.resource.Resource;
import ru.znay.znay.he.model.item.resource.ResourceItem;
import ru.znay.znay.he.model.level.tile.Tile;

/**
 * Created by IntelliJ IDEA.
 * User: Александр Сергеевич
 * Date: 04.03.12
 * Time: 15:40
 * To change this template use File | Settings | File Templates.
 */
public class SlimeFactory extends Mob {

    private final static int MIN_SPAWN_TIME = 300;

    private int spawnTime;

    public SlimeFactory() {
        this.xr = 6;
        this.yr = 4;
        this.health = 50;
        this.spawnTime = MIN_SPAWN_TIME + random.nextInt(MIN_SPAWN_TIME);
        this.team = ETeam.ENEMY_TEAM;
    }

    @Override
    public void tick() {
        super.tick();

        if (hurtTime <= 0) spawnTime -= level.getNumber() / 5 + 1;

        if (spawnTime <= 0 && level.getMobCount() < 500) {
            spawnTime = MIN_SPAWN_TIME + random.nextInt(MIN_SPAWN_TIME);
            level.add(new Slime(x, y));
        }
    }

    @Override
    public boolean blocks(Entity entity) {
        return true;
    }

    @Override
    public void die() {
        super.die();
        int count = random.nextInt((level.getNumber() + 1) * 2) + 1;

        for (int i = 0; i < count; i++) {
            this.level.add(new ItemEntity(new ResourceItem(Resource.bigCoin), x + random.nextInt(11) - 5, y + random.nextInt(11) - 5));
        }
    }

    @Override
    public void touchedBy(Entity entity) {
        if (entity instanceof Player) {
            entity.hurt(this, 2 * (level.getNumber() + 1), dir);
        }
        super.touchedBy(entity);
    }

    @Override
    public void render(Screen screen) {

        int xt = 0;
        int yt = 6;

        int xo = x - 8;
        int yo = y - 11;

        int col = PaletteHelper.getColor(-1, 10, 242 + level.getNumber() * 10, 555);
        if (hurtTime > 0)
            col = PaletteHelper.getColor(-1, 555, 555, 555);

        screen.render(2, xo, yo, xt * Tile.HALF_SIZE, yt * Tile.HALF_SIZE, col, 0);
    }
}
