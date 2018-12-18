package com.fadeland.editor.undoredo;

import com.fadeland.editor.map.TileMap;

public abstract class PerformableAction implements Action
{
    public boolean oldChanged;
    public TileMap map;

    public PerformableAction(TileMap map)
    {
        this.map = map;
    }

    public void setOldChanged(boolean oldChanged)
    {
        this.oldChanged = oldChanged;
    }

    @Override
    public void undo()
    {
        map.setChanged(oldChanged);
    }

    @Override
    public void redo()
    {
        map.setChanged(true);
    }
}
