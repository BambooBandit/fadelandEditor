package com.fadeland.editor.undoredo;

import com.badlogic.gdx.utils.Array;
import com.fadeland.editor.map.MapSprite;
import com.fadeland.editor.map.TileMap;

public class SelectSprite extends PerformableAction
{
    public Array<MapSprite> selectedSprites;
    public Array<MapSprite> selectedOld;
    public Array<MapSprite> selectedNew;

    public SelectSprite(TileMap map, Array<MapSprite> selectedSprites)
    {
        super(map);
        this.selectedSprites = selectedSprites;
        this.selectedOld = new Array<>(selectedSprites);
    }

    public void addSelected()
    {
        this.selectedNew = new Array<>(selectedSprites);
    }

    @Override
    public void undo()
    {
        super.undo();
        this.selectedSprites.clear();
        this.selectedSprites.addAll(this.selectedOld);
        this.map.propertyMenu.rebuild();
    }

    @Override
    public void redo()
    {
        super.redo();
        this.selectedSprites.clear();
        this.selectedSprites.addAll(this.selectedNew);
        this.map.propertyMenu.rebuild();
    }
}
