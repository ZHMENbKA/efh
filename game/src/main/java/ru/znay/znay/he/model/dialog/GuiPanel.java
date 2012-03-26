package ru.znay.znay.he.model.dialog;


import ru.znay.znay.he.gfx.helper.BitmapHelper;
import ru.znay.znay.he.gfx.helper.PaletteHelper;
import ru.znay.znay.he.gfx.model.Bitmap;
import ru.znay.znay.he.gfx.model.Screen;
import ru.znay.znay.he.model.level.tile.Tile;

/**
 * Created by IntelliJ IDEA.
 * User: denus
 * Date: 26.03.12
 * Time: 12:23
 * To change this template use File | Settings | File Templates.
 */
public class GuiPanel {
    protected boolean visible;
    protected int x = 0;
    protected int y = 0;
    protected int sizeX = 2;
    protected int sizeY = 2;
    private Bitmap image;
    protected boolean changed = true;
    protected boolean closed = false;

    public GuiPanel(int posX, int posY, int sizeX, int sizeY) {
        x = posX;
        y = posY;
        this.sizeX = sizeX + 2;
        this.sizeY = sizeY + 2;

        image = new Bitmap(this.sizeX * Tile.HALF_SIZE, this.sizeY * Tile.HALF_SIZE);

        changed = true;
        visible = true;
    }

    public void Show() {
        if (!visible) {
            visible = true;
            changed = true;
        }
    }

    public void Hide() {
        if (visible) {
            visible = false;
        }
    }

    public boolean GetVisible() {
        return visible;
    }

    public void SetVisible(boolean v) {
        if (visible != v) {
            visible = v;
            if (visible) {
                changed = true;
            }
        }
    }

    public void Put(int x, int y) {
        this.x = x;
        this.y = y;

        changed = true;
    }

    public void SetSize(int newX, int newY) {
        this.x = (newX < 1) ? 1 : newX;
        this.y = (newY < 1) ? 1 : newY;
        changed = true;
    }

    protected void SetChanged() {
        if (!changed) {
            changed = true;
        }
    }

    public void close() {
        closed = true;
        visible = false;
        changed = true;
    }

    public void Paint(Screen screen) {
        if (!changed) {
            return;
        }

        PaintF(screen);
    }

    public void PaintF(Screen screen) {
        Bitmap temp = new Bitmap(sizeX * Tile.HALF_SIZE, sizeY * Tile.HALF_SIZE);
        int col = PaletteHelper.getColor(-1, 1, 5, 445);
        int tx = (sizeX - 1) * Tile.HALF_SIZE;
        int ty = (sizeY - 1) * Tile.HALF_SIZE;

        BitmapHelper.fill(temp, 0xFFFFFF);
        //top left
        BitmapHelper.drawHalfTile(screen.getSprites(), 0, 0, 0, 13 * Tile.HALF_SIZE, col, 0, temp);
        //top right
        BitmapHelper.drawHalfTile(screen.getSprites(), tx, 0, 0, 13 * Tile.HALF_SIZE, col, 1, temp);
        //bottom left
        BitmapHelper.drawHalfTile(screen.getSprites(), 0, ty, 0, 13 * Tile.HALF_SIZE, col, 2, temp);
        //bottom right
        BitmapHelper.drawHalfTile(screen.getSprites(), tx, ty, 0, 13 * Tile.HALF_SIZE, col, 3, temp);

        for (int x = 1; x < sizeX - 1; x++) {
            //top
            BitmapHelper.drawHalfTile(screen.getSprites(), x * Tile.HALF_SIZE, 0, Tile.HALF_SIZE, 13 * Tile.HALF_SIZE, col, 0, temp);
            //bottom
            BitmapHelper.drawHalfTile(screen.getSprites(), x * Tile.HALF_SIZE, ty, Tile.HALF_SIZE, 13 * Tile.HALF_SIZE, col, 2, temp);
        }
        for (int y = 1; y < sizeY - 1; y++) {
            //left
            BitmapHelper.drawHalfTile(screen.getSprites(), 0, y * Tile.HALF_SIZE, 2 * Tile.HALF_SIZE, 13 * Tile.HALF_SIZE, col, 0, temp);
            //right
            BitmapHelper.drawHalfTile(screen.getSprites(), tx, y * Tile.HALF_SIZE, 2 * Tile.HALF_SIZE, 13 * Tile.HALF_SIZE, col, 1, temp);
        }

        for (int x = 1; x < sizeX - 1; x++) {
            for (int y = 1; y < sizeY - 1; y++) {
                BitmapHelper.drawHalfTile(screen.getSprites(), x * Tile.HALF_SIZE, y * Tile.HALF_SIZE, 3 * Tile.HALF_SIZE, 13 * Tile.HALF_SIZE, col, 0, temp);
            }
        }

        this.image = null;
        this.image = temp;
        changed = false;
    }

    public void render(Screen screen) {
        if (!visible) {
            return;
        }

        this.Paint(screen);

        BitmapHelper.copy(this.image, 0, 0, x, y, sizeX * Tile.HALF_SIZE, sizeY * Tile.HALF_SIZE, screen.getViewPort());
    }
}