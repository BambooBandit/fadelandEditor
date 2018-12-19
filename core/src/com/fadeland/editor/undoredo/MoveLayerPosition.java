package com.fadeland.editor.undoredo;

import com.fadeland.editor.map.Layer;
import com.fadeland.editor.map.TileMap;

public class MoveLayerPosition extends PerformableAction
{
    public Layer layer;
    public float oldX;
    public float oldY;
    public float newX;
    public float newY;

    public MoveLayerPosition(TileMap map, Layer layer)
    {
        super(map);
        this.layer = layer;
        this.oldX = layer.x;
        this.oldY = layer.y;
    }

    public void addNewPosition()
    {
        this.newX = this.layer.x;
        this.newY = this.layer.y;
    }

    @Override
    public void undo()
    {
        super.undo();
        this.layer.setPosition(oldX, oldY);
    }

    @Override
    public void redo()
    {
        super.redo();
        this.layer.setPosition(newX, newY);
    }
}
