package com.fadeland.editor.ui.tileMenu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class TileTool extends TileMenuTool
{

    public TileProperties properties;

    public TileTool(TileMenuTools tool, Image image, TileMenuToolPane tileMenuToolPane, Skin skin)
    {
        super(tool, image, tileMenuToolPane, skin);
        this.properties = new TileProperties();
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
}
