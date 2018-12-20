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

    public Array<Sprite> previewSprites;
    public Array<Sprite> topSprites;

    public TileTool(TileMenuTools tool, Image image, TextureRegion textureRegion, int id, int x, int y, TileMenuToolPane tileMenuToolPane, Skin skin)
    {
        super(tool, image, tileMenuToolPane, skin);
        this.textureRegion = textureRegion;
        this.previewSprites = new Array();
        this.previewSprites.add(new Sprite(textureRegion));
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
        {
            tileMenuToolPane.menu.tileTable.setDebug(true);
            tileMenuToolPane.menu.spriteTable.setDebug(true);
        }
    }

    @Override
    public void unselect()
    {
        this.image.setColor(Color.WHITE);

        this.isSelected = false;

        if(this.tool == TileMenuTools.LINES)
        {
            tileMenuToolPane.menu.tileTable.setDebug(false);
            tileMenuToolPane.menu.spriteTable.setDebug(false);
        }
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
            if(this.properties.get(i).getProperty() != null && this.properties.get(i).getProperty().equals(propertyName))
                return this.properties.get(i);
        }
        return null;
    }

    public void setTopSprites(String topSpriteName)
    {
        if(this.topSprites == null)
            this.topSprites = new Array();
        this.topSprites.clear();
        if(this.previewSprites.size > 1)
            this.previewSprites.removeRange(1, this.previewSprites.size - 1);
        int digits = 0;
        for(int i = topSpriteName.length() - 1; i >= 0; i --)
        {
            if(Character.isDigit(topSpriteName.charAt(i)))
                digits++;
            else
                break;
        }
        if(digits == 0)
        {
            TextureRegion textureRegion = GameAssets.getTextureRegion(topSpriteName);
            if (textureRegion == null)
                return;
            Sprite sprite = new Sprite(textureRegion);
            this.topSprites.add(sprite);
            this.previewSprites.add(sprite);
        }
        else
        {
            String topSpriteNoDigits = topSpriteName.substring(0, topSpriteName.length() - digits);
            int number = Integer.parseInt(topSpriteName.substring(topSpriteName.length() - digits));
            TextureRegion textureRegion = GameAssets.getTextureRegion(topSpriteName);
            while(textureRegion != null)
            {
                this.topSprites.add(new Sprite(textureRegion));
                this.previewSprites.add(new Sprite(textureRegion));
                number ++;
                textureRegion = GameAssets.getTextureRegion(topSpriteNoDigits + number);
            }
        }
    }
}
