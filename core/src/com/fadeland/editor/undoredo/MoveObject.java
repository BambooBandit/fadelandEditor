package com.fadeland.editor.undoredo;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.fadeland.editor.map.MapObject;
import com.fadeland.editor.map.Tile;

public class MoveObject implements Action
{
    public MapObject mapObject;
    public Array<MapObject> mapObjects;
    public ObjectMap<Tile, Float> oldXofDragMap;
    public ObjectMap<Tile, Float> oldYofDragMap;
    public ObjectMap<Tile, Float> newXofDragMap;
    public ObjectMap<Tile, Float> newYofDragMap;
    public float newX, newY;

    public MoveObject(ObjectMap<Tile, Float> oldXofDragMap, ObjectMap<Tile, Float> oldYofDragMap)
    {
        this.oldXofDragMap = oldXofDragMap;
        this.oldYofDragMap = oldYofDragMap;
    }

    public void addObject(MapObject mapObject)
    {
        if(this.mapObject == null && this.mapObjects == null)
            this.mapObject = mapObject;
        else
        {
            if(this.mapObjects == null)
            {
                this.mapObjects = new Array<>();
                this.mapObjects.add(this.mapObject);
                this.mapObject = null;
            }
            this.mapObjects.add(mapObject);
        }
    }

    public void addNewPosition()
    {
        if (this.mapObject != null)
        {
            this.newX = this.mapObject.position.x;
            this.newY = this.mapObject.position.y;
        }
        else
        {
            newXofDragMap = new ObjectMap<>();
            newYofDragMap = new ObjectMap<>();
            for(int i = 0; i < this.mapObjects.size; i ++)
            {
                newXofDragMap.put(this.mapObjects.get(i), this.mapObjects.get(i).position.x);
                newYofDragMap.put(this.mapObjects.get(i), this.mapObjects.get(i).position.y);
            }
        }
    }

    @Override
    public void undo()
    {
        if(this.mapObject != null)
            this.mapObject.setPosition(oldXofDragMap.get(mapObject), oldYofDragMap.get(mapObject));
        else
            for(int i = 0; i < mapObjects.size; i ++)
                mapObjects.get(i).setPosition(oldXofDragMap.get(mapObjects.get(i)), oldYofDragMap.get(mapObjects.get(i)));
    }

    @Override
    public void redo()
    {
        if(this.mapObject != null)
            this.mapObject.setPosition(newX, newY);
        else
            for(int i = 0; i < mapObjects.size; i ++)
                mapObjects.get(i).setPosition(newXofDragMap.get(mapObjects.get(i)), newYofDragMap.get(mapObjects.get(i)));
    }
}
