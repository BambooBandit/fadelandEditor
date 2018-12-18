package com.fadeland.editor.undoredo;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.fadeland.editor.map.MapSprite;
import com.fadeland.editor.map.Tile;
import com.fadeland.editor.map.TileMap;

public class ScaleSprite extends PerformableAction
{
    public MapSprite mapSprite;
    public Array<MapSprite> mapSprites;
    public float oldScale, newScale;

    public ObjectMap<Tile, Float> spriteToOldScale;
    public ObjectMap<Tile, Float> spriteToNewScale;

    public ScaleSprite(TileMap map)
    {
        super(map);
    }

    public void addSprite(MapSprite mapSprite)
    {
        if(this.mapSprite == null && this.mapSprites == null)
        {
            this.mapSprite = mapSprite;
            this.oldScale = mapSprite.scale;
        }
        else
        {
            if(this.mapSprites == null)
            {
                this.spriteToOldScale = new ObjectMap<>();
                this.spriteToNewScale = new ObjectMap<>();
                this.mapSprites = new Array<>();
                this.mapSprites.add(this.mapSprite);
                this.spriteToOldScale.put(this.mapSprite, this.mapSprite.scale);
                this.mapSprite = null;
            }
            this.mapSprites.add(mapSprite);
            this.spriteToOldScale.put(mapSprite, mapSprite.scale);
        }
    }

    public void addNewScale()
    {
        if (this.mapSprite != null)
            this.newScale = this.mapSprite.scale;
        else
            for(int i = 0; i < this.mapSprites.size; i ++)
                spriteToNewScale.put(this.mapSprites.get(i), this.mapSprites.get(i).scale);
    }

    @Override
    public void undo()
    {
        super.undo();
        if(this.mapSprite != null)
            this.mapSprite.setScale(oldScale);
        else
            for(int i = 0; i < mapSprites.size; i ++)
                this.mapSprites.get(i).setScale(spriteToOldScale.get(mapSprites.get(i)));
    }

    @Override
    public void redo()
    {
        super.redo();

        if(this.mapSprite != null)
            this.mapSprite.setScale(newScale);
        else
            for(int i = 0; i < mapSprites.size; i ++)
                this.mapSprites.get(i).setScale(spriteToNewScale.get(mapSprites.get(i)));
    }
}
