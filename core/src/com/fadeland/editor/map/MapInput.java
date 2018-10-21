package com.fadeland.editor.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector3;
import com.fadeland.editor.FadelandEditor;
import com.fadeland.editor.Utils;
import com.fadeland.editor.ui.fileMenu.Tools;

import static com.fadeland.editor.ui.tileMenu.TileMenu.tileSize;

public class MapInput implements InputProcessor
{
    private FadelandEditor editor;
    private TileMap map;

    public MapInput(FadelandEditor editor, TileMap map)
    {
        this.editor = editor;
        this.map = map;
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
        editor.stage.unfocus(editor.tileMenu.scrollPane);
        Vector3 coords = Utils.unproject(map.camera, screenX, screenY);
        Tile clickedTile = map.getTile(coords.x, coords.y - tileSize);
        if(editor.getFileTool() != null && editor.getTileTool() != null && clickedTile != null)
        {
            if (editor.getFileTool().tool == Tools.BRUSH)
                clickedTile.setTool(editor.getTileTool());
            else if (editor.getFileTool().tool == Tools.ERASER)
                clickedTile.setTool(null);
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
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY)
    {
        return false;
    }

    @Override
    public boolean scrolled(int amount)
    {
        if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
            this.map.camera.position.x += amount * 10;
        else if(Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT))
            this.map.camera.position.y += amount * 10;
        else
        {
            this.map.camera.zoom += amount / 3f;
            if(this.map.camera.zoom < .1f)
                this.map.camera.zoom = .1f;
        }

        return false;
    }
}
