package com.fadeland.editor.undoredo;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.fadeland.editor.map.AttachedMapObject;
import com.fadeland.editor.map.Tile;
import com.fadeland.editor.map.TileMap;

public class MoveAttachedObject extends PerformableAction
{
    public AttachedMapObject mapObject;
    public Array<AttachedMapObject> mapObjects;
    public ObjectMap<Tile, Float> oldXofDragMap;
    public ObjectMap<Tile, Float> oldYofDragMap;
    public ObjectMap<Tile, Float> newXofDragMap;
    public ObjectMap<Tile, Float> newYofDragMap;
    public float newX, newY;

    public MoveAttachedObject(TileMap map, ObjectMap<Tile, Float> oldXofDragMap, ObjectMap<Tile, Float> oldYofDragMap)
    {
        super(map);
        this.oldXofDragMap = new ObjectMap();
        this.oldYofDragMap = new ObjectMap();
    }

    public void addObject(AttachedMapObject mapObject)
    {
        if(this.mapObject == null && this.mapObjects == null)
        {
            this.mapObject = mapObject;
            this.oldXofDragMap.put(this.mapObject, this.mapObject.positionOffset.x);
            this.oldYofDragMap.put(this.mapObject, this.mapObject.positionOffset.y);
        }
        else
        {
            if(this.mapObjects == null)
            {
                this.mapObjects = new Array<>();
                this.mapObjects.add(this.mapObject);
                this.mapObject = null;
            }
            this.oldXofDragMap.put(mapObject, mapObject.positionOffset.x);
            this.oldYofDragMap.put(mapObject, mapObject.positionOffset.y);
            this.mapObjects.add(mapObject);
        }
    }

    public void addNewPosition()
    {
        if (this.mapObject != null)
        {
            this.newX = this.mapObject.positionOffset.x;
            this.newY = this.mapObject.positionOffset.y;
        }
        else
        {
            newXofDragMap = new ObjectMap<>();
            newYofDragMap = new ObjectMap<>();
            for(int i = 0; i < this.mapObjects.size; i ++)
            {
                newXofDragMap.put(this.mapObjects.get(i), this.mapObjects.get(i).positionOffset.x);
                newYofDragMap.put(this.mapObjects.get(i), this.mapObjects.get(i).positionOffset.y);
            }
        }
    }

    @Override
    public void undo()
    {
        super.undo();
        if(this.mapObject != null)
            this.mapObject.positionOffset.set(oldXofDragMap.get(mapObject), oldYofDragMap.get(mapObject));
        else
            for(int i = 0; i < mapObjects.size; i ++)
                mapObjects.get(i).positionOffset.set(oldXofDragMap.get(mapObjects.get(i)), oldYofDragMap.get(mapObjects.get(i)));
    }

    @Override
    public void redo()
    {
        super.redo();
        if(this.mapObject != null)
            this.mapObject.positionOffset.set(newX, newY);
        else
            for(int i = 0; i < mapObjects.size; i ++)
                mapObjects.get(i).positionOffset.set(newXofDragMap.get(mapObjects.get(i)), newYofDragMap.get(mapObjects.get(i)));
    }
}
