package ru.znay.znay.he.model.level;

import ru.znay.znay.he.Game;
import ru.znay.znay.he.cfg.Constants;
import ru.znay.znay.he.gfx.gui.*;
import ru.znay.znay.he.gfx.helper.BitmapHelper;
import ru.znay.znay.he.gfx.helper.TextFileHelper;
import ru.znay.znay.he.gfx.model.Bitmap;
import ru.znay.znay.he.gfx.model.Screen;
import ru.znay.znay.he.gfx.sprite.SpriteCollector;
import ru.znay.znay.he.model.ETeam;
import ru.znay.znay.he.model.Entity;
import ru.znay.znay.he.model.Mob;
import ru.znay.znay.he.model.Player;
import ru.znay.znay.he.model.builds.Mushroom;
import ru.znay.znay.he.model.builds.tree.*;
import ru.znay.znay.he.model.builds.utensils.Waymark;
import ru.znay.znay.he.model.level.tile.Tile;
import ru.znay.znay.he.model.mob.Bird;
import ru.znay.znay.he.model.mob.SlimeFactory;
import ru.znay.znay.he.model.npc.Warp;
import ru.znay.znay.he.quest.AbsQuest;
import ru.znay.znay.he.quest.QuestHandler;
import ru.znay.znay.he.quest.promotion.QuestPromotion;
import ru.znay.znay.he.quest.template.KillTemplate;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Александр Сергеевич
 * Date: 03.03.12
 * Time: 18:12
 * To change this template use File | Settings | File Templates.
 */
public class Level {

    private List<String> messages = null;

    private final static int GRASS_TILE = 0xFFFFFFFF;
    private final static int WATER_TILE = 0xFF0000FF;
    private final static int DEEP_WATER_TILE = 0xFF0000CC;
    private final static int SAND_TILE = 0xFFFFFF00;
    private final static int LAVA_TILE = 0xFFFF7F00;
    private final static int ROAD_TILE = 0xFF6B6B6B;
    private final static int SWAMP_TILE = 0xFF90FF00;

    private final static int HOLE_TILE = 0xFF606060;
    private final static int APPLE_TREE = 0xFF00FF00;
    private final static int FIR_TREE = 0xFF00CC00;
    private final static int TREE_STUMP = 0xFF008800;
    private final static int PLAYER_SPAWN = 0xFFFF0000;
    private final static int SHRUBBERY = 0xFF005500;

    private int number;

    private Random random = new Random();

    private int width;
    private int height;

    private Game game;

    private Player player;

    private byte[] tiles;

    private Fog fog;
    private int monsterDensity = 4;
    private long tickTime = 0;
    private List<Entity>[] entitiesInTiles;

    private List<Entity> entities = new ArrayList<Entity>();

    private QuestHandler questHandler;

    private SpriteCollector spriteCollector;


    private Comparator<Entity> spriteSorter = new Comparator<Entity>() {
        public int compare(Entity e0, Entity e1) {
            if (e1.getY() < e0.getY()) return 1;
            if (e1.getY() > e0.getY()) return -1;
            return 0;
        }
    };

    public Level(Player pl, int lv, Game game) {
        init(pl, lv, game);
    }

    @SuppressWarnings("unchecked")
    public void init(final Player player, int level, Game game) {

        this.number = level;

        messages = TextFileHelper.LoadMessages(level);

        this.spriteCollector = new SpriteCollector(game.getScreen().getSprites());

        this.loadLevelObject(level, player);


        GuiManager.getInstance().remove("minimap");
        GuiManager.getInstance().add(new MiniMap(Constants.SCREEN_WIDTH - (this.width + Tile.HALF_SIZE * 5) / 2, Tile.HALF_SIZE / 2, this), "minimap");

        if (player.isRemoved()) player.setHealth(10);

        this.add(player);

        this.game = game;

        this.questHandler = new QuestHandler(player);

/*
        //Квест убить 3х слаймов.. по окончанию игроку заплотят 1000 и покажется табличка
        AbsQuest testQuest = new KillTemplate(3, SlimeFactory.class);
        testQuest.setName("злые зеленые кучи");
        testQuest.setDescription("злые зеленые кучи уже всех достали. пора бы их пришить.. Итак вы отправляетесь в путь. Вам надо найти и убить 3 зеленые кучи");
        testQuest.setQuestPromotion(new QuestPromotion() {
            @Override
            public void promotion(Player player) {
                player.setScore(player.getScore() + 1000);
            }
        });
        testQuest.accept(this.questHandler);
        //---------------------------------------------------------------------------------
*/

        fog.clearFog2(player.getX() >> 4, player.getY() >> 4, player.getClearFogRadius());

        // trySpawn();
    }

    public void trySpawn() {
        for (int i = 0; i < 10; i++) {

            Mob mob = new SlimeFactory();
            if (mob.findStartPos(this)) {
                add(mob);
            }

            mob = new Mushroom();
            if (mob.findStartPos(this)) {
                add(mob);
            }

            /*mob = new Bird();
            if (mob.findStartPos(this)) {
                add(mob);
            }   */

            /*mob = new AppleTree();
            if (mob.findStartPos(this)) {
                add(mob);
            }  */
        }

    }

    public void tick() {

        for (int i = 0; i < this.width * this.height / 50; i++) {
            int xt = random.nextInt(this.width);
            int yt = random.nextInt(this.height);
            getTile(xt, yt).tick(this, xt, yt);
        }

        /*if (tickTime % 100 == 0) {
            this.questHandler.checkAllQuest();
        }*/

        GuiManager.getInstance().tick();

        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            int xto = entity.getX() >> 4;
            int yto = entity.getY() >> 4;

            entity.tick();

            if (entity.isRemoved()) {
                entities.remove(i--);
                removeEntity(xto, yto, entity);
                if (entity instanceof Mob) {

                    this.questHandler.updateKills(((Mob) entity));
                }
            } else {
                int xt = entity.getX() >> 4;
                int yt = entity.getY() >> 4;

                if (xto != xt || yto != yt) {
                    removeEntity(xto, yto, entity);
                    insertEntity(xt, yt, entity);
                    if (entity instanceof Player) {
                        fog.clearFog2(xt, yt, ((Player) entity).getClearFogRadius());
                    }
                }
            }
        }
        tickTime++;
    }

    private void insertEntity(int x, int y, Entity entity) {
        if (x < 0 || y < 0 || x >= this.width || y >= this.height) return;
        entitiesInTiles[x + y * this.width].add(entity);
    }

    private void removeEntity(int x, int y, Entity entity) {
        if (x < 0 || y < 0 || x >= this.width || y >= this.height) return;
        entitiesInTiles[x + y * this.width].remove(entity);
    }


    private void sortAndRender(Screen screen, List<Entity> list) {
        Collections.sort(list, spriteSorter);
        for (Entity entity : list) {
            entity.render(screen);
        }
    }


    public void renderBackground(Screen screen, int xScroll, int yScroll) {
        int xo = xScroll >> 4;
        int yo = yScroll >> 4;
        int w = (screen.getViewPort().getWidth() + Tile.SIZE - 1) >> 4;
        int h = (screen.getViewPort().getHeight() + Tile.SIZE - 1) >> 4;
        screen.setOffset(xScroll, yScroll);
        for (int y = yo; y <= h + yo; y++) {
            for (int x = xo; x <= w + xo; x++) {
                getTile(x, y).render(screen, this, x, y);

            }
        }
        screen.setOffset(0, 0);
    }

    private List<Entity> rowSprites = new ArrayList<Entity>();

    public void renderSprites(Screen screen, int xScroll, int yScroll) {

        int offScreen = 4;
        int xo = Math.max((xScroll >> 4), 0);
        int yo = Math.max((yScroll >> 4), 0);
        int w = ((screen.getViewPort().getWidth() + Tile.SIZE - 1) >> 4) + offScreen;
        int h = ((screen.getViewPort().getHeight() + Tile.SIZE - 1) >> 4) + offScreen;

        screen.setOffset(xScroll, yScroll);
        for (int y = yo - offScreen; y <= h + yo; y++) {
            for (int x = xo - offScreen; x <= w + xo; x++) {
                if (x < 0 || y < 0 || x >= this.width || y >= this.height) continue;
                // if (fog.getFog(x, y)) continue;
                rowSprites.addAll(entitiesInTiles[x + y * this.width]);
            }
            if (rowSprites.size() > 0) {
                sortAndRender(screen, rowSprites);
            }
            rowSprites.clear();
        }
        screen.setOffset(0, 0);


    }

    public void renderFog(Screen screen, int xScroll, int yScroll) {
        int xo = xScroll >> 4;
        int yo = yScroll >> 4;
        int w = (screen.getViewPort().getWidth() + Tile.SIZE - 1) >> 4;
        int h = (screen.getViewPort().getHeight() + Tile.SIZE - 1) >> 4;
        screen.setOffset(xScroll, yScroll);
        for (int y = yo; y <= h + yo; y++) {
            for (int x = xo; x <= w + xo; x++) {
                //fog.render(screen, x - 1, y - 1);
                fog.render(screen, x, y);
            }
        }
        screen.setOffset(0, 0);
    }

    public void add(Entity entity) {
        if (entity instanceof Player) {
            player = (Player) entity;
        }
        entity.setRemoved(false);
        entities.add(entity);
        entity.init(this);

        insertEntity(entity.getX() >> 4, entity.getY() >> 4, entity);
    }

    public List<Entity> getEntities(int x0, int y0, int x1, int y1, ETeam team) {
        List<Entity> result = new ArrayList<Entity>();
        int xt0 = (x0 >> 4) - 1;
        int yt0 = (y0 >> 4) - 1;
        int xt1 = (x1 >> 4) + 1;
        int yt1 = (y1 >> 4) + 1;
        for (int y = yt0; y <= yt1; y++) {
            for (int x = xt0; x <= xt1; x++) {
                if (x < 0 || y < 0 || x >= this.width || y >= this.height) continue;
                List<Entity> entities = entitiesInTiles[x + y * this.width];
                for (Entity e : entities) {

                    boolean isTeam = team == null || team == e.getTeam();

                    if (isTeam) {
                        if (e.intersects(x0, y0, x1, y1)) result.add(e);
                    }
                }
            }
        }
        return result;
    }

    public Tile getTile(int x, int y) {
        if (x < 0 || y < 0 || x >= this.width || y >= this.height) return Tile.rock;
        return Tile.tiles[tiles[x + y * this.width]];
    }

    public void setTile(int x, int y, Tile tile, int dataVal) {
        if (x < 0 || y < 0 || x >= this.width || y >= this.height) return;
        tiles[x + y * this.width] = tile.getId();
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public Player getPlayer() {
        return player;
    }

    public int getMonsterDensity() {
        return monsterDensity;
    }

    public void setMonsterDensity(int monsterDensity) {
        this.monsterDensity = monsterDensity;
    }

    public Fog getFog() {
        return fog;
    }

    public QuestHandler getQuestHandler() {
        return questHandler;
    }

    private void loadLevelObject(int level, Player player) {
        Bitmap map = BitmapHelper.loadBitmapFromResources("/maps/" + level + ".bmp");

        this.width = map.getWidth();
        this.height = map.getHeight();

        this.tiles = new byte[this.width * this.height];

        this.fog = new Fog(this.width, this.height);

        this.entitiesInTiles = new ArrayList[this.width * this.height];

        loadLandscape(map, player);
        loadNPC(level, player);
    }

    private void loadNPC(int level, Player player) {
        Bitmap map = BitmapHelper.loadBitmapFromResources("/maps/" + level + "O.bmp");
        for (int j = 0; j < this.height; j++) {
            for (int i = 0; i < this.width; i++) {
                int value = map.getPixels()[i + j * this.width];
                if (value == 0xFFFFFFFF) continue;
                switch (((value >> 16) & 0xFF)) {
                    case 0xFF: {
                        if (player.getRespPoint() != null) {
                            player.moveToXY(player.getRespPoint().x, player.getRespPoint().y);
                        } else {
                            player.moveToXY((i << 4) + Tile.HALF_SIZE, (j << 4) + Tile.HALF_SIZE);
                            player.setRespPoint((i << 4) + Tile.HALF_SIZE, (j << 4) + Tile.HALF_SIZE);
                        }
                        break;
                    }
                    case 0x10:
                        add(new Warp(level, (i << 4) + Tile.HALF_SIZE, (j << 4) + Tile.HALF_SIZE, level + 1, (((value >> 8) & 0xFF) << 4) + Tile.HALF_SIZE, ((value & 0xFF) << 4) + Tile.HALF_SIZE, spriteCollector, player));
                        break;
                    case 0x11:
                        add(new Warp(level, (i << 4) + Tile.HALF_SIZE, (j << 4) + Tile.HALF_SIZE, level - 1, (((value >> 8) & 0xFF) << 4) + Tile.HALF_SIZE, ((value & 0xFF) << 4) + Tile.HALF_SIZE, spriteCollector, player));
                        break;
                    case 0x25:
                        add(new Waymark((i << 4) + Tile.HALF_SIZE, (j << 4) + Tile.HALF_SIZE,spriteCollector,getMessage(value&0xFF)));
                        break;
                }
            }
        }
    }

    private void loadLandscape(Bitmap map, Player player) {


        for (int i = 0; i < this.width * height; i++) {
            this.entitiesInTiles[i] = new ArrayList<Entity>();
        }

        for (int j = 0; j < this.height; j++) {
            for (int i = 0; i < this.width; i++) {
                int value = map.getPixels()[i + j * this.width];
                switch (value) {
                    case GRASS_TILE: {
                        setTile(i, j, Tile.grass, 0);
                        break;
                    }
                    case WATER_TILE: {
                        setTile(i, j, Tile.water, 0);
                        break;
                    }
                    case DEEP_WATER_TILE: {
                        setTile(i, j, Tile.deepWater, 0);
                        break;
                    }
                    case SAND_TILE: {
                        setTile(i, j, Tile.sand, 0);
                        break;
                    }
                    case ROAD_TILE: {
                        setTile(i, j, Tile.road, 0);
                        break;
                    }
                    case SWAMP_TILE: {
                        setTile(i, j, Tile.swamp, 0);
                        break;
                    }
                    case HOLE_TILE: {
                        setTile(i, j, Tile.hole, 0);
                        break;
                    }
                    case APPLE_TREE: {
                        add(new AppleTree((i << 4) + Tile.HALF_SIZE, (j << 4) + Tile.HALF_SIZE, this.spriteCollector));
                        break;
                    }
                    case FIR_TREE: {
                        int r = random.nextInt(100);
                        if (r < 72) break;
                        if (r < 75) {
                            add(new TreeStump((i << 4) + Tile.HALF_SIZE, (j << 4) + Tile.HALF_SIZE, this.spriteCollector));
                            break;
                        }
                        if (random.nextBoolean()) {
                            add(new FirTree((i << 4) + Tile.HALF_SIZE, (j << 4) + Tile.HALF_SIZE, this.spriteCollector));
                        } else {
                            add(new PineTree((i << 4) + Tile.HALF_SIZE, (j << 4) + Tile.HALF_SIZE, this.spriteCollector));
                        }

                        break;
                    }
                    case TREE_STUMP: {
                        add(new TreeStump((i << 4) + Tile.HALF_SIZE, (j << 4) + Tile.HALF_SIZE, this.spriteCollector));
                        break;
                    }
                    case SHRUBBERY: {
                        add(new Shrubbery((i << 4) + Tile.HALF_SIZE, (j << 4) + Tile.HALF_SIZE, this.spriteCollector));
                        break;
                    }

                    case LAVA_TILE: {
                        setTile(i, j, Tile.lava, 0);
                        break;
                    }
                }
            }
        }
    }

    public String getMessage(int index) {
        if (messages == null) return "null pointer";
        if (index < 0 || index >= messages.size()) return "index out of range";

        return messages.get(index);
    }

    public int getNumber() {
        return number;
    }
}
