package ru.znay.znay.he.model.builds.building;

import ru.znay.znay.he.gfx.helper.BitmapHelper;

/**
 * Created by IntelliJ IDEA.
 * User: Денис Сергеевич
 * Date: 17.04.12
 * Time: 14:07
 * To change this template use File | Settings | File Templates.
 */
public class House extends Building{
    public House(int x, int y)
    {
        super(x,y,0,0);

        this.sprite = BitmapHelper.loadBitmapFromResources("/buildings/House.png");
    }
}
