package com.fadeland.editor.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.fadeland.editor.FadelandEditor;
import com.fadeland.editor.Utils;
import com.fadeland.editor.ui.fileMenu.Tools;
import com.fadeland.editor.ui.tileMenu.TileTool;

import static com.fadeland.editor.ui.tileMenu.TileMenu.tileSize;

public class MapInput implements InputProcessor
{
    private FadelandEditor editor;
    private TileMap map;

    private Vector2 dragOrigin;
    private Vector3 pos;

    public MapInput(FadelandEditor editor, TileMap map)
    {
        this.editor = editor;
        this.map = map;
        this.dragOrigin = new Vector2();
        this.pos = new Vector3();
    }

    @Override
    public boolean keyDown(int keycode)
    {
        return false;
    }

    @Override
    public boolean keyUp(int keycode)
    {
        return false;
    }

    @Override
    public boolean keyTyped(char character)
    {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button)
    {
        editor.stage.unfocus(map.tileMenu.tileScrollPane);
        editor.stage.unfocus(map.tileMenu.spriteScrollPane);
        Vector3 coords = Utils.unproject(map.camera, screenX, screenY);
        this.dragOrigin.set(coords.x, coords.y);
        if(map.selectedLayer instanceof TileLayer)
        {
            if(editor.getTileTools().size > 1 && editor.getTileTools().first() instanceof TileTool && editor.getFileTool() != null && editor.fileMenu.toolPane.random.selected)
            {
                // Randomly pick a tile from the selected tiles based on weighted probabilities
                TileTool randomTile = null;
                Tile clickedTile = map.getTile(coords.x, coords.y - tileSize);
                float totalSum = 0;
                float partialSum = 0;
                for(int i = 0; i < editor.getTileTools().size; i ++)
                    totalSum += Float.parseFloat(editor.getTileTools().get(i).getPropertyField("Probability").value.getText());
                float random = Utils.randomFloat(0, totalSum);
                for(int i = 0; i < editor.getTileTools().size; i ++)
                {
                    partialSum += Float.parseFloat(editor.getTileTools().get(i).getPropertyField("Probability").value.getText());
                    if(partialSum >= random)
                    {
                        System.out.println(i);
                        randomTile = editor.getTileTools().get(i);
                        break;
                    }
                }
                if(randomTile != null && editor.getFileTool().tool == Tools.BRUSH)
                    clickedTile.setTool(randomTile);
            }
            else
            {
                for (int i = 0; i < editor.getTileTools().size; i++)
                {
                    int xOffset = editor.getTileTools().first().x - editor.getTileTools().get(i).x;
                    int yOffset = editor.getTileTools().first().y - editor.getTileTools().get(i).y;
                    Tile clickedTile = map.getTile(coords.x + xOffset, coords.y + yOffset - tileSize);
                    if (editor.getFileTool() != null && clickedTile != null)
                    {
                        if (editor.getFileTool().tool == Tools.BRUSH)
                            clickedTile.setTool(editor.getTileTools().get(i));
                        else if (editor.getFileTool().tool == Tools.ERASER)
                            clickedTile.setTool(null);
                    }
                }
            }
        }
        else if(map.selectedLayer instanceof SpriteLayer)
        {
            if (editor.getFileTool() != null && editor.getSpriteTool() != null
                    && coords.x > 0 && coords.y > 0 && coords.x < map.mapWidth * tileSize && coords.y < map.mapHeight * tileSize)
            {
                if (editor.getFileTool().tool == Tools.BRUSH)
                    ((SpriteLayer) map.selectedLayer).sprites.add(new MapSprite(map, (SpriteLayer) map.selectedLayer, editor.getSpriteTool(),
                            coords.x - editor.getSpriteTool().textureRegion.getRegionWidth() / 2, coords.y - editor.getSpriteTool().textureRegion.getRegionHeight() / 2));
//                else if (editor.getFileTool().tool == Tools.ERASER)
//                    clickedTile.setTool(null);
            }
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button)
    {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer)
    {
        editor.stage.unfocus(map.tileMenu.tileScrollPane);
        editor.stage.unfocus(map.tileMenu.spriteScrollPane);
        Vector3 coords = Utils.unproject(map.camera, screenX, screenY);
        if(editor.getFileTool() != null && editor.getFileTool().tool == Tools.GRAB)
        {
            this.pos = coords.sub(dragOrigin.x, dragOrigin.y, 0);
            map.camera.position.x -= this.pos.x / 15f;
            map.camera.position.y -= this.pos.y / 15f;
            map.camera.update();
        }
        if(map.selectedLayer instanceof TileLayer)
        {
            System.out.println(editor.getTileTools().size + ", " + editor.getTileTools().first() + ", " +  editor.getFileTool() + ", " + editor.fileMenu.toolPane.random.selected);
            if(editor.getTileTools().size > 1 && editor.getTileTools().first() instanceof TileTool && editor.getFileTool() != null && editor.fileMenu.toolPane.random.selected)
            {
                // Randomly pick a tile from the selected tiles based on weighted probabilities
                TileTool randomTile = null;
                Tile clickedTile = map.getTile(coords.x, coords.y - tileSize);
                float totalSum = 0;
                float partialSum = 0;
                for(int i = 0; i < editor.getTileTools().size; i ++)
                {
                    if(editor.getTileTools().get(i).getPropertyField("Probability") == null)
                        continue;
                    totalSum += Float.parseFloat(editor.getTileTools().get(i).getPropertyField("Probability").value.getText());
                }
                float random = Utils.randomFloat(0, totalSum);
                for(int i = 0; i < editor.getTileTools().size; i ++)
                {
                    if(editor.getTileTools().get(i).getPropertyField("Probability") == null)
                        continue;
                    partialSum += Float.parseFloat(editor.getTileTools().get(i).getPropertyField("Probability").value.getText());
                    if(partialSum >= random)
                    {
                        System.out.println(i);
                        randomTile = editor.getTileTools().get(i);
                        break;
                    }
                }
                if(clickedTile != null && randomTile != null && editor.getFileTool().tool == Tools.BRUSH)
                    clickedTile.setTool(randomTile);
            }
            else
            {
                for (int i = 0; i < editor.getTileTools().size; i++)
                {
                    int xOffset = editor.getTileTools().first().x - editor.getTileTools().get(i).x;
                    int yOffset = editor.getTileTools().first().y - editor.getTileTools().get(i).y;
                    Tile clickedTile = map.getTile(coords.x + xOffset, coords.y + yOffset - tileSize);
                    if (editor.getFileTool() != null && clickedTile != null)
                    {
                        if (editor.getFileTool().tool == Tools.BRUSH)
                            clickedTile.setTool(editor.getTileTools().get(i));
                        else if (editor.getFileTool().tool == Tools.ERASER)
                            clickedTile.setTool(null);
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY)
    {
        editor.stage.unfocus(map.tileMenu.tileScrollPane);
        editor.stage.unfocus(map.tileMenu.spriteScrollPane);
        Vector3 coords = Utils.unproject(map.camera, screenX, screenY);
        if(map.selectedLayer instanceof TileLayer)
        {
            for(int i = 0; i < editor.getTileTools().size; i ++)
            {
                int xOffset = editor.getTileTools().first().x - editor.getTileTools().get(i).x;
                int yOffset = editor.getTileTools().first().y - editor.getTileTools().get(i).y;
                Tile hoverTile = map.getTile(coords.x + xOffset, coords.y + yOffset - tileSize);

                if (editor.getFileTool() != null && hoverTile != null && editor.getFileTool().tool == Tools.BRUSH)
                    editor.getTileTools().get(i).previewSprite.setPosition(hoverTile.x, hoverTile.y);
            }
        }
        return false;
    }

    @Override
    public boolean scrolled(int amount)
    {
        if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
        {
            this.map.camera.position.x += amount * 10;
            this.map.camera.update();
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT))
        {
            this.map.camera.position.y += amount * 10;
            this.map.camera.update();
        }
        else
        {
            this.map.camera.zoom += amount / 3f;
            if(this.map.camera.zoom < .1f)
                this.map.camera.zoom = .1f;
            this.map.camera.update();
        }

        return false;
    }
}
