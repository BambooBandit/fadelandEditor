package com.fadeland.editor.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fadeland.editor.FadelandEditor;
import com.fadeland.editor.GameAssets;
import com.fadeland.editor.Utils;
import com.fadeland.editor.ui.fileMenu.Tools;
import com.fadeland.editor.ui.layerMenu.LayerMenu;
import com.fadeland.editor.ui.propertyMenu.MapPropertyPanel;
import com.fadeland.editor.ui.propertyMenu.PropertyMenu;
import com.fadeland.editor.ui.tileMenu.TileMenu;
import com.fadeland.editor.ui.tileMenu.TileTool;

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

    public int mapWidth;
    public int mapHeight;

    public MapInput input;

    public Array<Layer> layers;
    public Layer selectedLayer;

    public Stage stage;
    public TileMenu tileMenu;
    public PropertyMenu propertyMenu;
    public LayerMenu layerMenu;

    public Array<MapSprite> selectedSprites;
    public Array<MapObject> selectedObjects;

    public BoxSelect boxSelect;

    public TileMap(FadelandEditor editor, String name)
    {
        this.editor = editor;
        this.name = name;

        this.layers = new Array<>();
        this.boxSelect = new BoxSelect(this);
        this.selectedSprites = new Array<>();
        this.selectedObjects = new Array<>();

        this.input = new MapInput(editor, this);

        this.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.viewport = new ScreenViewport(this.camera);
        this.viewport.apply();
        this.camera.position.x = 160;
        this.camera.position.y = 150;

        this.stage = new Stage(new ScreenViewport());

        // tileMenu
        this.tileMenu = new TileMenu(GameAssets.getUISkin(), editor, this);
        this.tileMenu.setVisible(true);
        this.stage.addActor(this.tileMenu);

        // propertyMenu
        this.propertyMenu = new PropertyMenu(GameAssets.getUISkin(), editor, this);
        this.propertyMenu.setVisible(true);
        this.stage.addActor(this.propertyMenu);

        // layerMenu
        this.layerMenu = new LayerMenu(GameAssets.getUISkin(), editor, this);
        this.layerMenu.setVisible(true);
        this.stage.addActor(this.layerMenu);

        this.mapWidth = propertyMenu.mapPropertyPanel.mapWidth;
        this.mapHeight = propertyMenu.mapPropertyPanel.mapHeight;

    }

    @Override
    public void show()
    {
        this.editor.inputMultiplexer.clear();
        this.editor.inputMultiplexer.addProcessor(this.editor.stage);
        this.editor.inputMultiplexer.addProcessor(this.stage);
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
        for(int i = 0; i < this.layers.size; i ++)
        {
            if(!(this.layers.get(i) instanceof ObjectLayer))
            {
                if (this.layers.get(i).layerField.visibleImg.isVisible())
                    this.layers.get(i).draw();
            }
        }
        for(int i = 0; i < this.selectedSprites.size; i ++)
        {
            this.selectedSprites.get(i).drawRotationBox();
            this.selectedSprites.get(i).drawMoveBox();
        }
        for(int i = 0; i < this.selectedObjects.size; i ++)
            this.selectedObjects.get(i).drawMoveBox();
        this.editor.batch.end();

        this.editor.shapeRenderer.setColor(Color.BLACK);
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

        this.editor.shapeRenderer.setColor(Color.CYAN);
        for(int i = 0; i < this.layers.size; i ++)
        {
            if(this.layers.get(i) instanceof ObjectLayer)
            {
                if (this.layers.get(i).layerField.visibleImg.isVisible())
                    this.layers.get(i).draw();
            }
            else
                this.layers.get(i).drawAttachedMapObjects();
        }
        this.editor.shapeRenderer.setColor(Color.GRAY);
        int oldIndex = 0;
        if(input.objectVertices.size >= 2)
        {
            this.editor.shapeRenderer.circle(input.objectVertices.get(0) + input.objectVerticePosition.x, input.objectVertices.get(1) + input.objectVerticePosition.y, 3);
            for (int i = 2; i < input.objectVertices.size; i += 2)
            {
                this.editor.shapeRenderer.line(input.objectVertices.get(oldIndex) + input.objectVerticePosition.x, input.objectVertices.get(oldIndex + 1) + input.objectVerticePosition.y, input.objectVertices.get(i) + input.objectVerticePosition.x, input.objectVertices.get(i + 1) + input.objectVerticePosition.y);
                oldIndex += 2;
            }
        }

        if(selectedLayer != null && selectedLayer instanceof ObjectLayer)
        {
            if (editor.getFileTool() != null && (editor.getFileTool().tool == Tools.SELECT || editor.getFileTool().tool == Tools.BOXSELECT))
            {
                Vector3 mouseCoords = Utils.unproject(camera, Gdx.input.getX(), Gdx.input.getY());
                boolean hoverDrawed = false;
                for (int i = selectedLayer.tiles.size - 1; i >= 0; i--)
                {
                    MapObject mapObject = ((MapObject) selectedLayer.tiles.get(i));
                    boolean selected = selectedObjects.contains(mapObject, true);
                    boolean hoveredOver = mapObject.polygon.contains(mouseCoords.x, mouseCoords.y);
                    if (selected && editor.getFileTool().tool == Tools.BOXSELECT)
                    {
                        this.editor.shapeRenderer.setColor(Color.GREEN);
                        mapObject.draw();
                    } else if (hoveredOver || selected)
                    {
                        if (selected && hoveredOver && !hoverDrawed)
                        {
                            this.editor.shapeRenderer.setColor(Color.YELLOW);
                            hoverDrawed = true;
                            mapObject.draw();
                        } else if (selected)
                        {
                            this.editor.shapeRenderer.setColor(Color.GREEN);
                            mapObject.draw();
                        } else if (hoveredOver && !hoverDrawed)
                        {
                            this.editor.shapeRenderer.setColor(Color.ORANGE);
                            hoverDrawed = true;
                            mapObject.draw();
                        }
                    }
                }
            }
            if (boxSelect.isDragging && editor.getFileTool() != null && editor.getFileTool().tool == Tools.BOXSELECT)
            {
                for (int i = selectedLayer.tiles.size - 1; i >= 0; i--)
                {
                    MapObject mapObject = ((MapObject) selectedLayer.tiles.get(i));
                    if (Intersector.overlapConvexPolygons(mapObject.polygon.getTransformedVertices(), boxSelect.getVertices(), null))
                    {
                        boolean selected = selectedObjects.contains(mapObject, true);
                        if (!selected)
                        {
                            this.editor.shapeRenderer.setColor(Color.YELLOW);
                            mapObject.draw();
                        }
                    }
                }
            }
        }
        if(editor.getFileTool() != null && editor.getFileTool().tool == Tools.OBJECTVERTICESELECT && selectedObjects.size == 1)
        {
            this.editor.shapeRenderer.setColor(Color.GRAY);
            selectedObjects.first().drawHoveredVertices();
            this.editor.shapeRenderer.setColor(Color.CYAN);
            selectedObjects.first().drawSelectedVertices();
        }

        if(selectedLayer != null && selectedLayer instanceof SpriteLayer && editor.getFileTool() != null && (editor.getFileTool().tool == Tools.SELECT || editor.getFileTool().tool == Tools.BOXSELECT))
        {
            Vector3 mouseCoords = Utils.unproject(camera, Gdx.input.getX(), Gdx.input.getY());
            boolean hoverDrawed = false;
            for (int i = selectedLayer.tiles.size - 1; i >= 0; i--)
            {
                MapSprite mapSprite = ((MapSprite) selectedLayer.tiles.get(i));

                for(int k = 0; k < mapSprite.tool.mapObjects.size; k ++)
                {
                    AttachedMapObject mapObject = mapSprite.tool.mapObjects.get(k);
                    boolean selected = selectedObjects.contains(mapObject, true);
                    boolean hoveredOver = mapObject.polygon.contains(mouseCoords.x, mouseCoords.y);
                    if (selected && editor.getFileTool().tool == Tools.BOXSELECT)
                    {
                        this.editor.shapeRenderer.setColor(Color.GREEN);
                        mapObject.draw();
                    } else if (hoveredOver || selected)
                    {
                        if (selected && hoveredOver && !hoverDrawed)
                        {
                            this.editor.shapeRenderer.setColor(Color.YELLOW);
                            hoverDrawed = true;
                            mapObject.draw();
                        } else if (selected)
                        {
                            this.editor.shapeRenderer.setColor(Color.GREEN);
                            mapObject.draw();
                        } else if (hoveredOver && !hoverDrawed)
                        {
                            this.editor.shapeRenderer.setColor(Color.ORANGE);
                            hoverDrawed = true;
                            mapObject.draw();
                        }
                    }
                }

                boolean selected = selectedSprites.contains(mapSprite, true);
                boolean hoveredOver = mapSprite.polygon.contains(mouseCoords.x, mouseCoords.y);
                if(selected && editor.getFileTool().tool == Tools.BOXSELECT)
                {
                    this.editor.shapeRenderer.setColor(Color.GREEN);
                    mapSprite.drawOutline();
                }
                else if (hoveredOver || selected)
                {
                    if(selected && hoveredOver && !hoverDrawed)
                    {
                        this.editor.shapeRenderer.setColor(Color.YELLOW);
                        hoverDrawed = true;
                        mapSprite.drawOutline();
                    }
                    else if(selected)
                    {
                        this.editor.shapeRenderer.setColor(Color.GREEN);
                        mapSprite.drawOutline();
                    }
                    else if(hoveredOver && !hoverDrawed)
                    {
                        this.editor.shapeRenderer.setColor(Color.ORANGE);
                        hoverDrawed = true;
                        mapSprite.drawOutline();
                    }
                }
            }
        }
        if(boxSelect.isDragging && selectedLayer != null && selectedLayer instanceof SpriteLayer && editor.getFileTool() != null && editor.getFileTool().tool == Tools.BOXSELECT)
        {
            for (int i = selectedLayer.tiles.size - 1; i >= 0; i--)
            {
                MapSprite mapSprite = ((MapSprite)selectedLayer.tiles.get(i));

                for(int k = 0; k < mapSprite.tool.mapObjects.size; k ++)
                {
                    AttachedMapObject mapObject = mapSprite.tool.mapObjects.get(k);
                    if (Intersector.overlapConvexPolygons(mapObject.polygon.getTransformedVertices(), boxSelect.getVertices(), null))
                    {
                        boolean selected = selectedObjects.contains(mapObject, true);
                        if (!selected)
                        {
                            this.editor.shapeRenderer.setColor(Color.YELLOW);
                            mapObject.draw();
                        }
                    }
                }

                if(Intersector.overlapConvexPolygons(mapSprite.polygon.getTransformedVertices(), boxSelect.getVertices(), null))
                {
                    boolean selected = selectedSprites.contains(mapSprite, true);
                    if(!selected)
                    {
                        this.editor.shapeRenderer.setColor(Color.YELLOW);
                        mapSprite.drawOutline();
                    }
                }
            }
            this.editor.shapeRenderer.setColor(Color.CYAN);
            editor.shapeRenderer.rect(boxSelect.rectangle.x, boxSelect.rectangle.y, boxSelect.rectangle.width, boxSelect.rectangle.height);
        }
        else if(selectedLayer != null && selectedLayer instanceof TileLayer && editor.getFileTool() != null && editor.getFileTool().tool == Tools.FILL)
        {
            Vector3 mouseCoords = Utils.unproject(camera, Gdx.input.getX(), Gdx.input.getY());
            for(int i = 0; i < selectedLayer.tiles.size; i ++)
                selectedLayer.tiles.get(i).hasBeenPainted = false;
            Tile clickedTile = getTile(mouseCoords.x, mouseCoords.y - tileSize);
            if(clickedTile != null)
                fillPreview(mouseCoords.x, mouseCoords.y, clickedTile.tool);
        }

        this.editor.shapeRenderer.end();

        this.stage.act();
        this.stage.draw();
    }

    @Override
    public void resize(int width, int height)
    {
        this.stage.getViewport().update(width, height, true);
        this.tileMenu.setSize(Gdx.graphics.getWidth() / 6, (Gdx.graphics.getHeight() - this.editor.fileMenu.getHeight()) / 2);
        this.tileMenu.setPosition(Gdx.graphics.getWidth() - this.tileMenu.getWidth(), 0);

        this.propertyMenu.setSize(Gdx.graphics.getWidth() / 6, Gdx.graphics.getHeight() - this.editor.fileMenu.getHeight());
        this.propertyMenu.setPosition(0, 0);

        this.layerMenu.setSize(Gdx.graphics.getWidth() / 6, (Gdx.graphics.getHeight() - this.editor.fileMenu.getHeight()) / 2);
        this.layerMenu.setPosition(Gdx.graphics.getWidth() - this.tileMenu.getWidth(), this.tileMenu.getHeight());

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
        if(selectedLayer == null || !(selectedLayer instanceof TileLayer) || x > mapWidth * tileSize || y > mapHeight * tileSize || x < 0 || y < -tileSize)
            return null;

        TileLayer selectedTileLayer = (TileLayer) selectedLayer;

        int index = 0;
        index += Math.ceil(y / tileSize) * mapWidth - 1;
        index += Math.ceil(x / tileSize);

        if(index >= selectedTileLayer.tiles.size || index < 0)
            return null;

        Tile tile = selectedTileLayer.tiles.get(index);
        return tile;
    }

    public void resizeMap(int width, int height)
    {
        this.propertyMenu.mapPropertyPanel.mapWidth = width;
        this.propertyMenu.mapPropertyPanel.mapHeight = height;
        this.mapWidth = width;
        this.mapHeight = height;
        for(int i = 0; i < this.layers.size; i ++)
            if(layers.get(i) instanceof TileLayer)
                ((TileLayer)layers.get(i)).resize(width, height, propertyMenu.mapPropertyPanel.down.isChecked(), propertyMenu.mapPropertyPanel.right.isChecked());
    }

    private void fillPreview(float x, float y, TileTool tool)
    {
        Tile tileToPaint = getTile(x, y - tileSize);
        if(tileToPaint != null && tileToPaint.tool == tool && !tileToPaint.hasBeenPainted)
        {
            TileTool tile = editor.getTileTools().first();
            if(tile != null)
            {
                tileToPaint.hasBeenPainted = true;
                editor.shapeRenderer.setColor(.2f, .85f, 1f, .5f);
                editor.shapeRenderer.rect(tileToPaint.position.x, tileToPaint.position.y, tileSize, tileSize);
                fillPreview(x + 64, y, tool);
                fillPreview(x - 64, y, tool);
                fillPreview(x, y + 64, tool);
                fillPreview(x, y - 64, tool);
            }
        }
    }
}
