package com.fadeland.editor.ui.tileMenu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.fadeland.editor.GameAssets;
import com.fadeland.editor.map.AttachedMapObject;
import com.fadeland.editor.ui.propertyMenu.PropertyField;

public class TileTool extends TileMenuTool implements Comparable<TileTool>
{

    public Array<PropertyField> lockedProperties; // properties such as probability. They belong to all tiles
    public Array<PropertyField> properties;

    public Array<AttachedMapObject> mapObjects; // For Tiles and MapSprites with MapObjects attached to them

    public int id, x, y;

    public TextureRegion textureRegion;

    public Sprite previewSprite;
    public Sprite topSprite;

    public TileTool(TileMenuTools tool, Image image, TextureRegion textureRegion, int id, int x, int y, TileMenuToolPane tileMenuToolPane, Skin skin)
    {
        super(tool, image, tileMenuToolPane, skin);
        this.textureRegion = textureRegion;
        this.previewSprite = new Sprite(textureRegion);
        this.lockedProperties = new Array<>();
        this.properties = new Array<>();
        this.id = id;
        this.x = x;
        this.y = y;
        this.mapObjects = new Array<>();
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

    public PropertyField getPropertyField(String propertyName)
    {
        for(int i = 0; i < this.lockedProperties.size; i ++)
        {
            if(this.lockedProperties.get(i).getProperty().equals(propertyName))
                return this.lockedProperties.get(i);
        }

        for(int i = 0; i < this.properties.size; i ++)
        {
            if(this.properties.get(i).getProperty().equals(propertyName))
                return this.properties.get(i);
        }
        return null;
    }

    public void setTopSprite(String topSpriteName)
    {
        TextureRegion textureRegion = GameAssets.getTextureRegion(topSpriteName);
        if(textureRegion == null)
        {
            this.topSprite = null;
            return;
        }
        if(this.topSprite == null)
            this.topSprite = new Sprite(textureRegion);
        else
            this.topSprite.setRegion(textureRegion);
    }
}
