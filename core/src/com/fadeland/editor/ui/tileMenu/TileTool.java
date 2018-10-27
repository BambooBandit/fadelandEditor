package com.fadeland.editor.ui.tileMenu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.fadeland.editor.ui.propertyMenu.PropertyField;

public class TileTool extends TileMenuTool implements Comparable<TileTool>
{

//    public TileProperties properties;
    public Array<PropertyField> properties;

    public int id, x, y;

    public TextureRegion textureRegion;

    public TileTool(TileMenuTools tool, Image image, TextureRegion textureRegion, int id, int x, int y, TileMenuToolPane tileMenuToolPane, Skin skin)
    {
        super(tool, image, tileMenuToolPane, skin);
        this.textureRegion = textureRegion;
//        this.properties = new TileProperties();
        this.properties = new Array<>();
        this.id = id;
        this.x = x;
        this.y = y;
    }

    @Override
    public void select()
    {
        this.image.setColor(Color.GREEN);

        this.isSelected = true;

        if(this.tool == TileMenuTools.LINES)
            tileMenuToolPane.menu.tileTable.setDebug(true);
    }

    @Override
    public void unselect()
    {
        this.image.setColor(Color.WHITE);

        this.isSelected = false;

        if(this.tool == TileMenuTools.LINES)
            tileMenuToolPane.menu.tileTable.setDebug(false);
    }

    @Override
    public int compareTo(TileTool o)
    {
        if(id > o.id)
            return 1;
        else if(id < o.id)
            return -1;
        return 0;
    }
}
