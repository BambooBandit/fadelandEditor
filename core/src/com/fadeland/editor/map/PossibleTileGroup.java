package com.fadeland.editor.map;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.fadeland.editor.FadelandEditor;

import static com.fadeland.editor.ui.tileMenu.TileMenu.tileSize;

public class PossibleTileGroup
{
    public Array<TileGroup> tileGroups; // The groups that this possible group of tiles can be stamped into. If stamped, a TileGroup will be chosen at random
    public FadelandEditor editor;

    public Vector2 position; // The upper left of this possible group

    public PossibleTileGroup(FadelandEditor editor, Vector2 position)
    {
        this.tileGroups = new Array<>();
        this.position = new Vector2();
        this.editor = editor;
        this.position = new Vector2(position);
    }

    public void draw()
    {
        int groupWidth = tileGroups.get(0).width;
        int right = 0;
        int down = tileGroups.get(0).height;

        for(int i = 0; i < tileGroups.get(0).types.size; i ++)
        {
            if(tileGroups.get(0).types.get(i) != null)
                editor.shapeRenderer.rect(position.x + (right * tileSize), position.y - tileSize + (down * tileSize), tileSize, tileSize);
            right ++;
            if(right >= groupWidth)
            {
                right = 0;
                down --;
            }
        }
    }
}
