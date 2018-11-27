package com.fadeland.editor.undoredo;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.fadeland.editor.map.MapSprite;
import com.fadeland.editor.map.Tile;

public class MoveSprite implements Action
{
    public MapSprite mapSprite;
    public Array<MapSprite> mapSprites;
    public ObjectMap<Tile, Float> oldXofDragMap;
    public ObjectMap<Tile, Float> oldYofDragMap;
    public ObjectMap<Tile, Float> newXofDragMap;
    public ObjectMap<Tile, Float> newYofDragMap;
    public float newX, newY;

    public MoveSprite(ObjectMap<Tile, Float> oldXofDragMap, ObjectMap<Tile, Float> oldYofDragMap)
    {
        this.oldXofDragMap = oldXofDragMap;
        this.oldYofDragMap = oldYofDragMap;
    }

    public void addSprite(MapSprite mapSprite)
    {
        if(this.mapSprite == null && this.mapSprites == null)
            this.mapSprite = mapSprite;
        else
        {
            if(this.mapSprites == null)
            {
                this.mapSprites = new Array<>();
                this.mapSprites.add(this.mapSprite);
                this.mapSprite = null;
            }
            this.mapSprites.add(mapSprite);
        }
    }

    public void addNewPosition()
    {
        if (this.mapSprite != null)
        {
            this.newX = this.mapSprite.position.x;
            this.newY = this.mapSprite.position.y;
        }
        else
        {
            newXofDragMap = new ObjectMap<>();
            newYofDragMap = new ObjectMap<>();
            for(int i = 0; i < this.mapSprites.size; i ++)
            {
                newXofDragMap.put(this.mapSprites.get(i), this.mapSprites.get(i).position.x);
                newYofDragMap.put(this.mapSprites.get(i), this.mapSprites.get(i).position.y);
            }
        }
    }

    @Override
    public void undo()
    {
        if(this.mapSprite != null)
            this.mapSprite.setPosition(oldXofDragMap.get(mapSprite), oldYofDragMap.get(mapSprite));
        else
            for(int i = 0; i < mapSprites.size; i ++)
                mapSprites.get(i).setPosition(oldXofDragMap.get(mapSprites.get(i)), oldYofDragMap.get(mapSprites.get(i)));
    }

    @Override
    public void redo()
    {
        if(this.mapSprite != null)
            this.mapSprite.setPosition(newX, newY);
        else
            for(int i = 0; i < mapSprites.size; i ++)
                mapSprites.get(i).setPosition(newXofDragMap.get(mapSprites.get(i)), newYofDragMap.get(mapSprites.get(i)));
    }
}
