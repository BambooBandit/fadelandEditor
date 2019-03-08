package com.fadeland.editor.map;

import com.badlogic.gdx.utils.Array;
import com.fadeland.editor.FadelandEditor;
import com.fadeland.editor.ui.layerMenu.LayerField;
import com.fadeland.editor.ui.layerMenu.LayerTypes;

import static com.fadeland.editor.map.TileMap.tileSize;

public abstract class Layer
{
    public Array<Tile> tiles;

    public int width;
    public int height;
    public float z;

    protected FadelandEditor editor;
    public TileMap map;
    public LayerField layerField;
    public LayerTypes type;

    public float x, y;
    public MoveBox moveBox;

    public Layer(FadelandEditor editor, TileMap map, LayerTypes type, LayerField layerField)
    {
        this.width = 5;
        this.height = 5;
        this.z = 0;
        this.tiles = new Array<>();
        this.editor = editor;
        this.map = map;
        this.type = type;
        this.layerField = layerField;

        this.moveBox = new MoveBox();
        this.moveBox.setPosition(x + (this.width * tileSize), y + (this.height * tileSize));
    }

    public void setPosition(float x, float y)
    {
        float xOffset = x - this.x;
        float yOffset = y - this.y;
        this.x = x;
        this.y = y;
        this.moveBox.setPosition(x + (this.width * tileSize), y + (this.height * tileSize));
        for(int i = 0; i < tiles.size; i ++)
            tiles.get(i).setPosition(tiles.get(i).position.x + xOffset, tiles.get(i).position.y + yOffset);
    }

    public void drawMoveBox()
    {
        if(map.selectedLayer == this)
            moveBox.sprite.draw(map.editor.batch);
    }

    public abstract void draw();

    public void drawAttachedMapObjects()
    {
        for(int i = 0; i < this.tiles.size; i ++)
        {
            if(this.tiles.get(i).tool != null)
            {
                for (int k = 0; k < this.tiles.get(i).drawableAttachedMapObjects.size; k++)
                {
                    AttachedMapObject mapObject = this.tiles.get(i).drawableAttachedMapObjects.get(k);
                    mapObject.attachedTile = this.tiles.get(i);
                    mapObject.draw();
                }
            }
        }
    }

    protected void resize()
    {
        this.moveBox.setPosition(x + (this.width * tileSize), y + (this.height * tileSize));
    }

    public void resize(int width, int height, boolean down, boolean right)
    {
        int oldWidth = this.width;
        int oldHeight = this.height;
        this.width = width;
        this.height = height;
        if(width > oldWidth) // grow horizontal
        {
            if(!right) // grow left
            {
                float widthIncrease = (width - oldWidth) * tileSize;
                for(int i = 0; i < tiles.size; i ++)
                    tiles.get(i).setPosition(tiles.get(i).position.x + widthIncrease, tiles.get(i).position.y);
            }
        }
        else // shrink horizontal
        {
            if(!right) // shrink left
            {
                float widthShrink = (oldWidth - width) * tileSize;
                for(int i = 0; i < tiles.size; i ++)
                    tiles.get(i).setPosition(tiles.get(i).position.x - widthShrink, tiles.get(i).position.y);
            }
        }

        if(height > oldHeight) // grow vertical
        {
            if(down) // grow down
            {
                float heightIncrease = (height - oldHeight) * tileSize;
                for(int i = 0; i < tiles.size; i ++)
                    tiles.get(i).setPosition(tiles.get(i).position.x, tiles.get(i).position.y + heightIncrease);
            }
        }
        else // shrink vertical
        {
            if(down) // shrink down
            {
                float heightShrink = (oldHeight - height) * tileSize;
                for(int i = 0; i < tiles.size; i ++)
                    tiles.get(i).setPosition(tiles.get(i).position.x, tiles.get(i).position.y - heightShrink);
            }
        }
        resize();
    }

    public void setZ(float z)
    {
        this.z = z;
    }

    public void setCameraZoomToThisLayer()
    {
        if(editor.fileMenu.toolPane.parallax.selected)
        {
            this.map.camera.zoom = this.map.zoom - z;
            this.map.camera.update();
            this.editor.batch.setProjectionMatrix(map.camera.combined);
            this.editor.shapeRenderer.setProjectionMatrix(map.camera.combined);
        }
    }

    public void setCameraZoomToSelectedLayer()
    {
        if(this.map.selectedLayer == null)
            return;
        if(editor.fileMenu.toolPane.parallax.selected)
        {
            this.map.camera.zoom = this.map.zoom - this.map.selectedLayer.z;
            this.map.camera.update();
            this.editor.batch.setProjectionMatrix(map.camera.combined);
            this.editor.shapeRenderer.setProjectionMatrix(map.camera.combined);
        }
    }
}
