package com.fadeland.editor.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fadeland.editor.FadelandEditor;
import com.fadeland.editor.Utils;
import com.fadeland.editor.ui.fileMenu.Tools;
import com.fadeland.editor.ui.propertyMenu.MapPropertyPanel;

import static com.fadeland.editor.ui.tileMenu.TileMenu.tileSize;

public class TileMap implements Screen
{
    public FadelandEditor editor;
    public OrthographicCamera camera;
    public Viewport viewport;
    public static int untitledCount = 0;

    public float r = Utils.randomFloat(0, 1);
    public float g = Utils.randomFloat(0, 1);
    public float b = Utils.randomFloat(0, 1);

    public String name;

    public Array<Tile> tiles;

    public int mapWidth;
    public int mapHeight;

    private MapInput input;

    public TileMap(FadelandEditor editor, String name)
    {
        this.editor = editor;
        this.name = name;

        this.input = new MapInput(editor, this);

        this.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.viewport = new ScreenViewport(this.camera);
        this.viewport.apply();
        this.camera.position.x = 160;
        this.camera.position.y = 150;

        this.tiles = new Array<>();

        this.mapWidth = MapPropertyPanel.mapWidth;
        this.mapHeight = MapPropertyPanel.mapHeight;

        for(int y = 0; y < mapHeight; y ++)
        {
            for(int x = 0; x < mapWidth; x ++)
                this.tiles.add(new Tile(this, x * 64, y * 64));
        }
    }

    @Override
    public void show()
    {
        this.editor.inputMultiplexer.clear();
        this.editor.inputMultiplexer.addProcessor(this.editor.stage);
        this.editor.inputMultiplexer.addProcessor(this.input);
        Gdx.input.setInputProcessor(this.editor.inputMultiplexer);
    }

    @Override
    public void render(float delta)
    {
        Gdx.gl.glClearColor(r, g, b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        this.camera.update();
        this.editor.batch.setProjectionMatrix(camera.combined);

        this.editor.shapeRenderer.setProjectionMatrix(camera.combined);
        this.editor.shapeRenderer.setAutoShapeType(true);
        this.editor.shapeRenderer.setColor(Color.BLACK);

        this.editor.batch.begin();
        for(int i = 0; i < tiles.size; i ++)
            this.tiles.get(i).draw();
        this.editor.batch.end();

        this.editor.shapeRenderer.begin();
        this.editor.shapeRenderer.line(0, 0, 0, mapHeight * tileSize);
        this.editor.shapeRenderer.line(0, 0, mapWidth * tileSize, 0);
        this.editor.shapeRenderer.line(0, mapHeight * tileSize, mapWidth * tileSize, mapHeight * tileSize);
        this.editor.shapeRenderer.line(mapWidth * tileSize, 0, mapWidth * tileSize, mapHeight * tileSize);

        if(editor.fileMenu.toolPane.lines.selected)
        {
            for (int y = 1; y < mapHeight; y++)
                this.editor.shapeRenderer.line(0, y * tileSize, mapWidth * tileSize, y * tileSize);
            for (int x = 1; x < mapWidth; x++)
                this.editor.shapeRenderer.line(x * tileSize, 0, x * tileSize, mapHeight * tileSize);
        }
        this.editor.shapeRenderer.end();
    }

    @Override
    public void resize(int width, int height)
    {
        this.camera.viewportWidth = width;
        this.camera.viewportHeight = height;
        this.viewport.update(width, height);
    }

    @Override
    public void pause()
    {

    }

    @Override
    public void resume()
    {

    }

    @Override
    public void hide()
    {

    }

    @Override
    public void dispose()
    {

    }

    public String getName()
    {
        return this.name;
    }

    public Tile getTile(float x, float y)
    {
        if(x > mapWidth * tileSize || y > mapHeight * tileSize)
            return null;

        int index = 0;
        index += Math.ceil(y / tileSize) * mapWidth - 1;
        index += Math.ceil(x / tileSize);

        if(index >= this.tiles.size || index < 0)
            return null;

        Tile tile = tiles.get(index);
        return tile;
    }
}
