package ru.znay.znay.he.model.level;

import ru.znay.znay.he.gfx.model.Bitmap;
import ru.znay.znay.he.gfx.model.Screen;
import ru.znay.znay.he.model.Player;
import ru.znay.znay.he.model.level.tile.Tile;

import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: Денис Сергеевич
 * Date: 20.04.12
 * Time: 18:22
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
public class NewFog {
    private boolean[] fog;
    private int h;
    private int w;
    private Bitmap black;
    private boolean[] testFog;
    int ww;
    int hh;
    Random rnd = new Random();
    private byte[] helper;

    public NewFog(Level level, Screen screen) {

        //this.w = level.getWidth();
        //this.h = level.getHeight();

        //fog = new boolean[w * h];

        //for (int i = 0; i < fog.length; i++)
        //    fog[i] = true;

        //black = new Bitmap(Tile.HALF_SIZE * 4, Tile.HALF_SIZE * 4);
        //BitmapHelper.fill(black, 0xFFFFFF);
        //BitmapHelper.scaleDraw(screen.getSprites(), 1, 0, 0, 4 * Tile.HALF_SIZE, 7 * Tile.HALF_SIZE, 20, 20, PaletteHelper.getColor(0, -1, -1, -1), 0, black);
        helper = new byte[9];
        helper[1] = 1;
        helper[2] = 2;
        helper[3] = 4;
        helper[4] = 8;
        helper[5] = 16;
        helper[6] = 32;
        helper[7] = 64;
        helper[8] = -128;


        ww = Tile.SIZE * level.getWidth();
        hh = Tile.SIZE * level.getHeight();
        //fuck memory economy
        testFog = new boolean[ww * hh];

        //System.out.println(ww * hh);

        for (int i = 0; i < ww * hh; i++)
            testFog[i] = true;
    }

    public void render(Screen screen) {
        // int localXOffset = screen.getXOffset() >> 4;
        // int localYOffset = screen.getYOffset() >> 4;

        //  int dstW = (screen.getViewPort().getWidth()/* + Tile.HALF_SIZE - 1*/) >> 4;
        // int dstH = (screen.getViewPort().getHeight()/* + Tile.HALF_SIZE - 1*/) >> 4;

        /* for (int x = localXOffset; x <= localXOffset + dstW; x++)
       for (int y = localYOffset; y <= localYOffset + dstH; y++) {
           if (fog[x + y * w]) {
               BitmapHelper.drawNormal(black, (x << 4) - screen.getXOffset() - 3, (y << 4) - screen.getYOffset() - 3, screen.getViewPort(), 0xFFFFFF);
           }
       } */

        int localXOffset = screen.getXOffset();
        int localYOffset = screen.getYOffset();
        int dstW = screen.getViewPort().getWidth();
        int dstH = screen.getViewPort().getHeight();

        for (int x = localXOffset; x < localXOffset + dstW; x++)
            for (int y = localYOffset; y < localYOffset + dstH; y++) {
                if (testFog[x + y * ww]) {
                    int tx = x - localXOffset;
                    int ty = y - localYOffset;
                    screen.getViewPort().getPixels()[tx + dstW * ty] = 0x00000000;
                }
            }
    }

    public boolean tick(Player player, int qq) {
        int r = (player.getClearFogRadius() * Tile.SIZE);// +rnd.nextInt(14)-7;
        int px = player.getX();
        int py = player.getY();
        int x = 0;
        int y = r;
        int delta = (2 - (r << 1));
        int error;

        while (y >= 0) {
            //System.out.println((px + x) + " " +(py + y));
            openFog2(px + x, py + y, px - x);
            //openFog2(px + x, py - y);
            openFog2(px + x, py - y, px - x);
            //openFog2(px - x, py - y);

            error = 2 * (delta + y) - 1;
            if (delta < 0 && error <= 0) {
                ++x;
                delta += 2 * x + 1;
                continue;
            }
            error = 2 * (delta - x) - 1;
            if (delta > 0 && error > 0) {
                --y;
                delta += 1 - 2 * y;
                continue;
            }
            ++x;
            delta += 2 * (x - y);
            --y;
        }
        return true;
    }

    public void openFog2(int x, int y, int x2) {
        if (x < 0) x = 0;
        if (x >= ww) x = ww - 1;
        if (y < 0) y = 0;
        if (y >= hh) y = hh - 1;
        if (x2 < 0) x2 = 0;
        if (x2 >= ww) x2 = ww - 1;
        for (int i = x; i >= x2; i--)
            testFog[i + y * ww] = false;
    }

    public void tick(Player player) {

        if (tick(player, 1)) return;

        int r = player.getClearFogRadius();
        int px = player.getX() >> 4;
        int py = player.getY() >> 4;

        int x = 0;
        int y = r;
        int delta = (2 - (r << 1));
        int error;

        while (y >= 0) {
            //System.out.println((px + x) + " " +(py + y));
            openFog(px + x, py + y);
            openFog(px + x, py - y);
            openFog(px - x, py + y);
            openFog(px - x, py - y);

            error = 2 * (delta + y) - 1;
            if (delta < 0 && error <= 0) {
                ++x;
                delta += 2 * x + 1;
                continue;
            }
            error = 2 * (delta - x) - 1;
            if (delta > 0 && error > 0) {
                --y;
                delta += 1 - 2 * y;
                continue;
            }
            ++x;
            delta += 2 * (x - y);
            --y;
        }
    }

    private void openFog(int x, int y) {
        if (x >= 0 && y >= 0 && x < w && y < h)
            fog[x + y * w] = false;
    }

    public boolean getFog(int x, int y) {
        return x < 0 || y < 0 || x >= this.ww || y >= this.hh || testFog[(x << 4) + (y << 4) * this.ww];
    }
}
