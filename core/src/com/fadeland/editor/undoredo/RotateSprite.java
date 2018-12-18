package com.fadeland.editor.undoredo;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.fadeland.editor.Utils;
import com.fadeland.editor.map.MapSprite;
import com.fadeland.editor.map.Tile;
import com.fadeland.editor.map.TileMap;

public class RotateSprite extends PerformableAction
{
    public MapSprite mapSprite;
    public Array<MapSprite> mapSprites;
    public float oldRotation, newRotation;

    public ObjectMap<Tile, Float> spriteToOldRotation;
    public ObjectMap<Tile, Float> spriteToNewRotation;

    public RotateSprite(TileMap map)
    {
        super(map);
    }

    public void addSprite(MapSprite mapSprite)
    {
        if(this.mapSprite == null && this.mapSprites == null)
        {
            this.mapSprite = mapSprite;
            this.oldRotation = mapSprite.rotation;
        }
        else
        {
            if(this.mapSprites == null)
            {
                this.spriteToOldRotation = new ObjectMap<>();
                this.spriteToNewRotation = new ObjectMap<>();
                this.mapSprites = new Array<>();
                this.mapSprites.add(this.mapSprite);
                this.spriteToOldRotation.put(this.mapSprite, this.mapSprite.rotation);
                this.mapSprite = null;
            }
            this.mapSprites.add(mapSprite);
            this.spriteToOldRotation.put(mapSprite, mapSprite.rotation);
        }
    }

    public void addNewRotation()
    {
        if (this.mapSprite != null)
            this.newRotation = this.mapSprite.rotation;
        else
            for(int i = 0; i < this.mapSprites.size; i ++)
                spriteToNewRotation.put(this.mapSprites.get(i), this.mapSprites.get(i).rotation);
    }

    @Override
    public void undo()
    {
        super.undo();
        float xSum = 0, ySum = 0;
        for(MapSprite mapSprite : map.selectedSprites)
        {
            xSum += mapSprite.position.x;
            ySum += mapSprite.position.y;
        }
        float xAverage = xSum / map.selectedSprites.size;
        float yAverage = ySum / map.selectedSprites.size;
        Utils.setCenterOrigin(xAverage, yAverage);

        if(this.mapSprite != null)
            this.mapSprite.rotate(oldRotation - newRotation);
        else
            for(int i = 0; i < mapSprites.size; i ++)
                this.mapSprites.get(i).rotate(spriteToOldRotation.get(mapSprites.get(i)) - spriteToNewRotation.get(mapSprites.get(i)));
    }

    @Override
    public void redo()
    {
        super.redo();
        float xSum = 0, ySum = 0;
        for(MapSprite mapSprite : map.selectedSprites)
        {
            xSum += mapSprite.position.x;
            ySum += mapSprite.position.y;
        }
        float xAverage = xSum / map.selectedSprites.size;
        float yAverage = ySum / map.selectedSprites.size;
        Utils.setCenterOrigin(xAverage, yAverage);

        if(this.mapSprite != null)
            this.mapSprite.rotate(newRotation - oldRotation);
        else
            for(int i = 0; i < mapSprites.size; i ++)
                this.mapSprites.get(i).rotate(spriteToNewRotation.get(mapSprites.get(i)) - spriteToOldRotation.get(mapSprites.get(i)));
    }
}
