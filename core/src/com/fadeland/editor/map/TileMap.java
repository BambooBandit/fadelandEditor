package com.fadeland.editor.map;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fadeland.editor.FadelandEditor;
import com.fadeland.editor.GameAssets;
import com.fadeland.editor.Utils;
import com.fadeland.editor.map.mapdata.*;
import com.fadeland.editor.ui.fileMenu.Tools;
import com.fadeland.editor.ui.layerMenu.LayerMenu;
import com.fadeland.editor.ui.layerMenu.LayerTypes;
import com.fadeland.editor.ui.propertyMenu.PropertyField;
import com.fadeland.editor.ui.propertyMenu.PropertyMenu;
import com.fadeland.editor.ui.propertyMenu.PropertyToolPane;
import com.fadeland.editor.ui.tileMenu.TileMenu;
import com.fadeland.editor.ui.tileMenu.TileMenuTools;
import com.fadeland.editor.ui.tileMenu.TileTool;
import com.fadeland.editor.undoredo.Action;
import com.fadeland.editor.undoredo.PerformableAction;

import java.io.File;
import java.util.ArrayList;
import java.util.Stack;

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

    public TextButton mapPaneButton;

    public boolean changed = false; // Any changes since the last save/opening/creating the file?

    public static int tileSize;

    public static int tilePadSize = 1;

    public float zoom = 1;

    public MapInput input;

    public Array<Layer> layers;
    public Layer selectedLayer;

    public Stage stage;
    public TileMenu tileMenu;
    public PropertyMenu propertyMenu;
    public LayerMenu layerMenu;

    public Array<MapSprite> selectedSprites;
    public Array<MapObject> selectedObjects;
    public Tile selectedTile; // Not an array because you need one tile selected to attach a MapObject to it

    public BoxSelect boxSelect;

    public Array<TileGroup> tileGroups;

    public Stack<Action> undo;
    public Stack<Action> redo;

    public World world;
    public Box2DDebugRenderer b2dr;
    public RayHandler rayHandler;

    private boolean apply = false;

    public File file = null;
    public Array<Body> bodies = new Array<>();

    public TileMap(FadelandEditor editor, TileMapData tileMapData)
    {
        this.editor = editor;
        this.name = tileMapData.name;
        this.tileSize = tileMapData.tileSize;
        init();
        tileSize = tileMapData.tileSize; // TODO make this build the tiles. Currently doesn't
        setMapPropertiesAndObjects(tileMapData, false);
        this.apply = true;
    }

    public TileMap(FadelandEditor editor, String name)
    {
        this.editor = editor;
        this.name = name;
        this.tileSize = 64;
        this.tilePadSize = 1;
        init();
    }

    private void init()
    {
        this.undo = new Stack<>();
        this.redo = new Stack<>();

        b2dr = new Box2DDebugRenderer();

        this.layers = new Array<>();
        this.boxSelect = new BoxSelect(this);
        this.selectedSprites = new Array<>();
        this.selectedObjects = new Array<>();
        this.tileGroups = new Array<>();

        this.input = new MapInput(editor, this);

        this.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.camera.zoom = this.zoom;
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

        this.world = new World(new Vector2(0, 0), false);
        this.rayHandler = new RayHandler(this.world);
        this.rayHandler.setAmbientLight(1);
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

        this.world.step(delta, 1, 1);

        if(!editor.fileMenu.toolPane.parallax.selected)
            this.camera.zoom = this.zoom;
        this.camera.update();
        this.editor.batch.setProjectionMatrix(camera.combined);

        this.editor.shapeRenderer.setProjectionMatrix(camera.combined);
        this.editor.shapeRenderer.setAutoShapeType(true);
        this.editor.shapeRenderer.setColor(Color.BLACK);

        boolean rayhandlerRendered = false;
        this.editor.batch.begin();
        for(int i = 0; i < this.layers.size; i ++)
        {
            if(!(this.layers.get(i) instanceof ObjectLayer))
            {
                if (this.layers.get(i).layerField.visibleImg.isVisible())
                    this.layers.get(i).draw();
            }
            if(this.layers.get(i) instanceof ObjectLayer && this.layers.get(i).layerField.layerName.getText().equals("rayhandler"))
            {
                rayhandlerRendered = true;
                this.editor.batch.end();
                this.rayHandler.setCombinedMatrix(camera);
                this.rayHandler.updateAndRender();
                this.editor.batch.begin();
            }
        }

        if(!rayhandlerRendered)
        {
            this.editor.batch.end();
            this.rayHandler.setCombinedMatrix(camera);
            this.rayHandler.updateAndRender();
            this.editor.batch.begin();
        }

        for(int i = 0; i < this.selectedSprites.size; i ++)
        {
            this.selectedSprites.get(i).drawRotationBox();
            this.selectedSprites.get(i).drawMoveBox();
            this.selectedSprites.get(i).drawScaleBox();
        }
        for(int i = 0; i < this.selectedObjects.size; i ++)
            this.selectedObjects.get(i).drawMoveBox();
        if(selectedLayer != null)
            selectedLayer.drawMoveBox();
        this.editor.batch.end();

        this.editor.shapeRenderer.setColor(Color.BLACK);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        this.editor.shapeRenderer.begin();
        this.editor.shapeRenderer.set(ShapeRenderer.ShapeType.Line);
        if(selectedLayer != null)
        {
            int layerWidth = selectedLayer.width;
            int layerHeight = selectedLayer.height;
            this.editor.shapeRenderer.line(selectedLayer.x, selectedLayer.y, selectedLayer.x, selectedLayer.y + (layerHeight * tileSize));
            this.editor.shapeRenderer.line(selectedLayer.x, selectedLayer.y, selectedLayer.x + (layerWidth * tileSize), selectedLayer.y);
            this.editor.shapeRenderer.line(selectedLayer.x, selectedLayer.y + (layerHeight * tileSize), selectedLayer.x + (layerWidth * tileSize), selectedLayer.y + (layerHeight * tileSize));
            this.editor.shapeRenderer.line(selectedLayer.x + (layerWidth * tileSize), selectedLayer.y, selectedLayer.x + (layerWidth * tileSize), selectedLayer.y + (layerHeight * tileSize));

            if (editor.fileMenu.toolPane.lines.selected)
            {
                for (int y = 1; y < layerHeight; y++)
                    this.editor.shapeRenderer.line(selectedLayer.x, selectedLayer.y + (y * tileSize), selectedLayer.x + (layerWidth * tileSize), selectedLayer.y + (y * tileSize));
                for (int x = 1; x < layerWidth; x++)
                    this.editor.shapeRenderer.line(selectedLayer.x + (x * tileSize), selectedLayer.y, selectedLayer.x + (x * tileSize), selectedLayer.y + (layerHeight * tileSize));
            }
        }

        this.editor.shapeRenderer.setColor(Color.CYAN);
        for(int i = 0; i < this.layers.size; i ++)
        {
            this.layers.get(i).setCameraZoomToThisLayer();
            if(this.layers.get(i) instanceof ObjectLayer)
            {
                if (this.layers.get(i).layerField.visibleImg.isVisible())
                    this.layers.get(i).draw();
            }
            else if(this.layers.get(i).layerField.attachedVisibleImg.isVisible())
                this.layers.get(i).drawAttachedMapObjects();
            this.layers.get(i).setCameraZoomToSelectedLayer();
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
                    boolean hoveredOver = mapObject.isHoveredOver(mouseCoords.x, mouseCoords.y);
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
                    boolean polygon = mapObject.polygon != null && Intersector.overlapConvexPolygons(mapObject.polygon.getTransformedVertices(), boxSelect.getVertices(), null);
                    boolean point = Intersector.isPointInPolygon(boxSelect.getVertices(), 0, boxSelect.getVertices().length, mapObject.position.x, mapObject.position.y);
                    if (polygon || point)
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
        if(selectedTile != null && selectedLayer != null && selectedLayer instanceof TileLayer && selectedLayer.tiles.contains(selectedTile, true))
        {
            this.editor.shapeRenderer.setColor(Color.GREEN);
            this.editor.shapeRenderer.rect(selectedTile.position.x, selectedTile.position.y, selectedTile.width, selectedTile.height);

            Vector3 mouseCoords = Utils.unproject(camera, Gdx.input.getX(), Gdx.input.getY());
            for(int k = 0; k < selectedTile.drawableAttachedMapObjects.size; k ++)
            {
                AttachedMapObject mapObject = selectedTile.drawableAttachedMapObjects.get(k);
                boolean selected = selectedObjects.contains(mapObject, true);
                boolean hoveredOver = mapObject.isHoveredOver(mouseCoords.x, mouseCoords.y);
                if (hoveredOver || selected)
                {
                    if (selected && hoveredOver)
                    {
                        this.editor.shapeRenderer.setColor(Color.YELLOW);
                        mapObject.draw();
                    } else if (selected)
                    {
                        this.editor.shapeRenderer.setColor(Color.GREEN);
                        mapObject.draw();
                    } else if (hoveredOver)
                    {
                        this.editor.shapeRenderer.setColor(Color.ORANGE);
                        mapObject.draw();
                    }
                }
            }
        }
        if(selectedLayer != null && selectedLayer instanceof TileLayer && editor.getFileTool() != null && editor.getFileTool().tool == Tools.SELECT)
        {
            Vector3 mouseCoords = Utils.unproject(camera, Gdx.input.getX(), Gdx.input.getY());
            Tile tile = getTile(mouseCoords.x, mouseCoords.y - tileSize);
            boolean selected = selectedTile == tile;
            boolean hoveredOver = (tile != null && mouseCoords.x >= tile.position.x && mouseCoords.x <= tile.width + tile.position.x &&
                    mouseCoords.y >= tile.position.y && mouseCoords.y <= tile.height + tile.position.y);
            if (tile != null && (hoveredOver || selected))
            {
                if (selected && hoveredOver)
                {
                    this.editor.shapeRenderer.setColor(Color.YELLOW);
                    this.editor.shapeRenderer.rect(tile.position.x, tile.position.y, tile.width, tile.height);
                } else if (selected)
                {
                    this.editor.shapeRenderer.setColor(Color.GREEN);
                    this.editor.shapeRenderer.rect(tile.position.x, tile.position.y, tile.width, tile.height);
                } else if (hoveredOver)
                {
                    this.editor.shapeRenderer.setColor(Color.ORANGE);
                    this.editor.shapeRenderer.rect(tile.position.x, tile.position.y, tile.width, tile.height);
                }
            }
        }
        if(selectedLayer != null && selectedLayer instanceof SpriteLayer && editor.getFileTool() != null && (editor.getFileTool().tool == Tools.SELECT || editor.getFileTool().tool == Tools.BOXSELECT))
        {
            Vector3 mouseCoords = Utils.unproject(camera, Gdx.input.getX(), Gdx.input.getY());
            boolean hoverDrawed = false;
            for (int i = selectedLayer.tiles.size - 1; i >= 0; i--)
            {
                MapSprite mapSprite = ((MapSprite) selectedLayer.tiles.get(i));

                for(int k = 0; k < mapSprite.drawableAttachedMapObjects.size; k ++)
                {
                    AttachedMapObject mapObject = mapSprite.drawableAttachedMapObjects.get(k);
                    boolean selected = selectedObjects.contains(mapObject, true);
                    mapObject.attachedTile = mapSprite;
                    boolean hoveredOver = mapObject.isHoveredOver(mouseCoords.x, mouseCoords.y);
                    if (selected && editor.getFileTool().tool == Tools.BOXSELECT)
                    {
                        this.editor.shapeRenderer.setColor(Color.GREEN);
                        mapObject.draw();
                    }
                    else if (hoveredOver || selected)
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
        if(boxSelect.isDragging && selectedLayer != null && (selectedLayer instanceof SpriteLayer || selectedLayer instanceof ObjectLayer) && editor.getFileTool() != null && editor.getFileTool().tool == Tools.BOXSELECT)
        {
            for (int i = selectedLayer.tiles.size - 1; i >= 0; i--)
            {
                if(selectedLayer.tiles.get(i) instanceof MapSprite)
                {
                    MapSprite mapSprite = ((MapSprite) selectedLayer.tiles.get(i));

                    for (int k = 0; k < mapSprite.drawableAttachedMapObjects.size; k++)
                    {
                        AttachedMapObject mapObject = mapSprite.drawableAttachedMapObjects.get(k);
                        boolean polygon = mapObject.polygon != null && Intersector.overlapConvexPolygons(mapObject.polygon.getTransformedVertices(), boxSelect.getVertices(), null);
                        boolean point = Intersector.isPointInPolygon(boxSelect.getVertices(), 0, boxSelect.getVertices().length, mapObject.position.x, mapObject.position.y);
                        if (polygon || point)
                        {
                            boolean selected = selectedObjects.contains(mapObject, true);
                            if (!selected)
                            {
                                this.editor.shapeRenderer.setColor(Color.YELLOW);
                                mapObject.draw();
                            }
                        }
                    }

                    if (Intersector.overlapConvexPolygons(mapSprite.polygon.getTransformedVertices(), boxSelect.getVertices(), null))
                    {
                        boolean selected = selectedSprites.contains(mapSprite, true);
                        if (!selected)
                        {
                            this.editor.shapeRenderer.setColor(Color.YELLOW);
                            mapSprite.drawOutline();
                        }
                    }
                }
                else if(selectedLayer.tiles.get(i) instanceof MapObject)
                {
                    MapObject mapObject = ((MapObject) selectedLayer.tiles.get(i));

                    boolean polygon = mapObject.polygon != null && Intersector.overlapConvexPolygons(mapObject.polygon.getTransformedVertices(), boxSelect.getVertices(), null);
                    boolean point = Intersector.isPointInPolygon(boxSelect.getVertices(), 0, boxSelect.getVertices().length, mapObject.position.x, mapObject.position.y);
                    if (polygon || point)
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
        else if(editor.fileMenu.toolPane.blocked.selected)
        {
            for(int i = 0; i < layers.size; i++)
            {
                if(layers.get(i) instanceof TileLayer)
                    ((TileLayer) layers.get(i)).drawBlocked();
            }
        }
        else if(selectedLayer != null && selectedLayer instanceof TileLayer && editor.getFileTool() != null && (editor.getFileTool().tool == Tools.BIND || editor.getFileTool().tool == Tools.STAMP))
            ((TileLayer)this.selectedLayer).drawPossibleTileGroups();
        this.editor.shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        if(editor.fileMenu.toolPane.b2drender.selected)
            b2dr.render(this.world, camera.combined);

        this.stage.act();
        this.stage.draw();

        if(apply)
        {
            apply = false;
            PropertyToolPane.apply(this);
            undo.clear();
            redo.clear();
        }
    }

    public void setChanged(boolean changed)
    {
        if(mapPaneButton == null)
            return;
        if(this.changed != changed)
        {
            if(changed)
                mapPaneButton.setText(name + "*");
            else
                mapPaneButton.setText(name);
        }
        this.changed = changed;
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
        if(selectedLayer == null)
            return null;
        x -= selectedLayer.x;
        y -= selectedLayer.y;
        if(!(selectedLayer instanceof TileLayer) || x > selectedLayer.width * tileSize || y > selectedLayer.height * tileSize || x < 0 || y < -tileSize)
            return null;

        TileLayer selectedTileLayer = (TileLayer) selectedLayer;

        int index = 0;
        index += Math.ceil(y / tileSize) * selectedLayer.width - 1;
        index += Math.ceil(x / tileSize);

        if(index >= selectedTileLayer.tiles.size || index < 0)
            return null;

        Tile tile = selectedTileLayer.tiles.get(index);
        return tile;
    }

    private void fillPreview(float x, float y, TileTool tool)
    {
        Tile tileToPaint = getTile(x, y);
        Stack<Tile> s = Utils.floodFillQueue;
        s.push(tileToPaint);
        if(editor.getTileTools() == null || editor.getTileTools().size == 0)
            return;
        while(s.size() > 0)
        {
            tileToPaint = s.pop();
            TileTool tile = editor.getTileTools().first();
            if (tile != null && tileToPaint != null)
            {
                tileToPaint.hasBeenPainted = true;
                editor.shapeRenderer.setColor(.2f, .85f, 1f, .5f);
                editor.shapeRenderer.rect(tileToPaint.position.x, tileToPaint.position.y, tileSize, tileSize);

                float tileToPaintX = tileToPaint.position.x + tileSize / 2;
                float tileToPaintY = tileToPaint.position.y - tileSize + tileSize / 2;

                tileToPaint = getTile(tileToPaintX + tileSize, tileToPaintY);
                if(tileToPaint != null && tileToPaint.tool == tool && !tileToPaint.hasBeenPainted)
                    s.push(tileToPaint);

                tileToPaint = getTile(tileToPaintX - tileSize, tileToPaintY);
                if(tileToPaint != null && tileToPaint.tool == tool && !tileToPaint.hasBeenPainted)
                    s.push(tileToPaint);

                tileToPaint = getTile(tileToPaintX, tileToPaintY + tileSize);
                if(tileToPaint != null && tileToPaint.tool == tool && !tileToPaint.hasBeenPainted)
                    s.push(tileToPaint);

                tileToPaint = getTile(tileToPaintX, tileToPaintY - tileSize);
                if(tileToPaint != null && tileToPaint.tool == tool && !tileToPaint.hasBeenPainted)
                    s.push(tileToPaint);
            }
        }
        s.clear();
    }


    /** Searches for all occurrences of tile groups in all the tiles of each layer.
     * Allows for easy outlining of possible groups of tiles to stamp, and stamping groups of tiles.*/
    public void findAllTilesToBeGrouped()
    {
        for(int i = 0; i < layers.size; i ++)
        {
            if(layers.get(i) instanceof TileLayer)
                ((TileLayer) layers.get(i)).findAllTilesToBeGrouped();
        }
    }

    /** Must be called before anything in the map changes. */
    public void performAction(PerformableAction action)
    {
        action.setOldChanged(changed);
        setChanged(true);
        this.undo.push(action);
        if(this.undo.size() > 75)
            this.undo.remove(0);
        this.redo.clear();

    }

    public void undo()
    {
        if(undo.size() == 0)
            return;

        Action action = this.undo.pop();
        action.undo();
        this.redo.push(action);
    }

    public void redo()
    {
        if(redo.size() == 0)
            return;

        Action action = this.redo.pop();
        action.redo();
        this.undo.push(action);
    }

    public void setMapPropertiesAndObjects(TileMapData tileMapData, boolean setDefaultsOnly)
    {
        PropertyField mapRGBAProperty = propertyMenu.mapPropertyPanel.getLockedColorField();
        ColorPropertyData savedMapRGBAProperty = Utils.getLockedColorField(tileMapData.mapLockedProperties);
        mapRGBAProperty.rValue.setText(Float.toString(savedMapRGBAProperty.r));
        mapRGBAProperty.gValue.setText(Float.toString(savedMapRGBAProperty.g));
        mapRGBAProperty.bValue.setText(Float.toString(savedMapRGBAProperty.b));
        mapRGBAProperty.aValue.setText(Float.toString(savedMapRGBAProperty.a));
        rayHandler.setAmbientLight(savedMapRGBAProperty.r, savedMapRGBAProperty.g, savedMapRGBAProperty.b, savedMapRGBAProperty.a);

        for(int i = 0; i < tileMapData.mapProperties.size(); i ++)
        {
            PropertyData property = tileMapData.mapProperties.get(i);
            if(property instanceof LightPropertyData)
            {
                LightPropertyData lightPropertyData = (LightPropertyData) property;
                propertyMenu.newProperty(lightPropertyData.r, lightPropertyData.g, lightPropertyData.b, lightPropertyData.a, lightPropertyData.distance, lightPropertyData.rayAmount);
            }
            else if(property instanceof ColorPropertyData)
            {
                ColorPropertyData colorPropertyData = (ColorPropertyData) property;
                propertyMenu.newProperty(colorPropertyData.r, colorPropertyData.g, colorPropertyData.b, colorPropertyData.a);
            }
            else
            {
                NonColorPropertyData nonColorPropertyData = (NonColorPropertyData) property;
                propertyMenu.newProperty(nonColorPropertyData.property, nonColorPropertyData.value);
            }
        }
        for(int i = 0; i < tileMapData.tileGroups.size(); i ++)
        {
            tileGroups.clear();
            TileGroupData tileGroupData = tileMapData.tileGroups.get(i);
            TileGroup tileGroup = new TileGroup(tileGroupData.width, tileGroupData.height, tileGroupData.boundGroupIds, tileGroupData.types, this);
            tileGroups.add(tileGroup);
        }
        if(!setDefaultsOnly)
        {
            for (int i = tileMapData.layers.size() - 1; i >= 0; i--)
            {
                Layer layer;
                LayerTypes layerTypes = null;
                LayerData savedLayer = tileMapData.layers.get(i);

                if (savedLayer instanceof TileLayerData)
                    layerTypes = LayerTypes.TILE;
                else if (savedLayer instanceof MapSpriteLayerData)
                    layerTypes = LayerTypes.SPRITE;
                else if (savedLayer instanceof MapObjectLayerData)
                    layerTypes = LayerTypes.OBJECT;

                if (layerTypes != null)
                {
                    layer = layerMenu.newLayer(layerTypes);
                    layer.layerField.layerName.setText(tileMapData.layers.get(i).name);
                    layer.setZ(tileMapData.layers.get(i).z);
                    layer.setPosition(tileMapData.layers.get(i).x, tileMapData.layers.get(i).y);
                    layer.resize(savedLayer.width, savedLayer.height, false, false);

                    if (layerTypes == LayerTypes.TILE)
                    {
                        TileLayerData savedTileLayer = (TileLayerData) savedLayer;
                        for (int k = 0; k < savedTileLayer.tiles.size(); k++)
                        {
                            int id = savedTileLayer.tiles.get(k).id;
                            TileTool tileTool = tileMenu.getTileTool(TileMenuTools.TILE, id);
                            layer.tiles.get(k).setTool(tileTool);
                            layer.tiles.get(k).hasBlockedObjectOnTop = savedTileLayer.tiles.get(k).blockedByObject;
                        }
                    } else if (layerTypes == LayerTypes.SPRITE)
                    {
                        MapSpriteLayerData savedSpriteLayer = (MapSpriteLayerData) savedLayer;
                        for (int k = 0; k < savedSpriteLayer.tiles.size(); k++)
                        {
                            String name = savedSpriteLayer.tiles.get(k).name;
                            TileTool tileTool = tileMenu.getSpriteTool(TileMenuTools.SPRITE, name, savedSpriteLayer.tiles.get(k).sheetName);
                            MapSprite mapSprite = input.newMapSprite(this, tileTool, layer, savedSpriteLayer.tiles.get(k).x + savedSpriteLayer.tiles.get(k).width / 2, savedSpriteLayer.tiles.get(k).y + savedSpriteLayer.tiles.get(k).height / 2);
                            Utils.setCenterOrigin(mapSprite.position.x, mapSprite.position.y);
                            mapSprite.setID(savedSpriteLayer.tiles.get(k).spriteID);
                            mapSprite.setRotation(savedSpriteLayer.tiles.get(k).rotation);
                            mapSprite.setScale(savedSpriteLayer.tiles.get(k).scale);
                            mapSprite.setZ(savedSpriteLayer.tiles.get(k).z);
                            mapSprite.setColor(savedSpriteLayer.tiles.get(k).r, savedSpriteLayer.tiles.get(k).g, savedSpriteLayer.tiles.get(k).b, savedSpriteLayer.tiles.get(k).a);
                            layer.tiles.add(mapSprite);
                        }
                    } else if (layerTypes == LayerTypes.OBJECT)
                    {
                        MapObjectLayerData savedObjectLayer = (MapObjectLayerData) savedLayer;
                        for (int k = 0; k < savedObjectLayer.tiles.size(); k++)
                        {
                            MapObject mapObject = null;
                            if (savedObjectLayer.tiles.get(k) instanceof MapPolygonData)
                            {
                                MapPolygonData mapPolygonData = (MapPolygonData) savedObjectLayer.tiles.get(k);
                                mapObject = new MapObject(this, layer, mapPolygonData.vertices, mapPolygonData.x, mapPolygonData.y);
                            } else if (savedObjectLayer.tiles.get(k) instanceof MapPointData)
                            {
                                MapPointData mapPointData = (MapPointData) savedObjectLayer.tiles.get(k);
                                mapObject = new MapObject(this, layer, mapPointData.x, mapPointData.y);
                            }
                            if (mapObject != null)
                            {
                                for (int s = 0; s < savedObjectLayer.tiles.get(k).propertyData.size(); s++)
                                {
                                    selectedObjects.clear();
                                    selectedObjects.add(mapObject);
                                    PropertyData propertyData = savedObjectLayer.tiles.get(k).propertyData.get(s);
                                    if (propertyData instanceof LightPropertyData)
                                    {
                                        LightPropertyData lightPropertyData = (LightPropertyData) propertyData;
                                        propertyMenu.newProperty(lightPropertyData.r, lightPropertyData.g, lightPropertyData.b, lightPropertyData.a, lightPropertyData.distance, lightPropertyData.rayAmount);
                                    } else if (propertyData instanceof ColorPropertyData)
                                    {
                                        ColorPropertyData colorPropertyData = (ColorPropertyData) propertyData;
                                        propertyMenu.newProperty(colorPropertyData.r, colorPropertyData.g, colorPropertyData.b, colorPropertyData.a);
                                    } else
                                    {
                                        NonColorPropertyData nonColorPropertyData = (NonColorPropertyData) propertyData;
                                        propertyMenu.newProperty(nonColorPropertyData.property, nonColorPropertyData.value);
                                    }
                                    selectedObjects.clear();
                                }
                                layer.tiles.add(mapObject);
                            }
                        }
                    }
                }
            }
        }

        for(int i = 0; i < tileMapData.sheets.size(); i ++)
        {
            if(tileMapData.sheets.get(i) instanceof TileSheetData)
            {
                TileSheetData tileSheetData = (TileSheetData) tileMapData.sheets.get(i);
                ArrayList<ToolData> tileTools = tileSheetData.tools;
                for (int k = 0; k < tileTools.size(); k++)
                {
                    ToolData toolData = tileTools.get(k);
                    TileTool tileTool = tileMenu.getTileTool(toolData.type, toolData.id, toolData.name, tileSheetData.name);
                    tileTool.mapObjects.clear();
                    for (int h = 0; h < toolData.lockedPropertyData.size(); h++)
                    {
                        PropertyData property = toolData.lockedPropertyData.get(h);
                        if (property instanceof LightPropertyData)
                        {
                            LightPropertyData lightPropertyData = (LightPropertyData) property;
                            tileTool.getLockedLightField().setRGBADR(lightPropertyData.r, lightPropertyData.g, lightPropertyData.b, lightPropertyData.a, lightPropertyData.distance, lightPropertyData.rayAmount);
                        } else if (property instanceof ColorPropertyData)
                        {
                            ColorPropertyData colorPropertyData = (ColorPropertyData) property;
                            tileTool.getLockedColorField().setRGBA(colorPropertyData.r, colorPropertyData.g, colorPropertyData.b, colorPropertyData.a);
                        } else
                        {
                            NonColorPropertyData nonColorPropertyData = (NonColorPropertyData) property;
                            tileTool.getPropertyField(nonColorPropertyData.property).value.setText(nonColorPropertyData.value);
                        }
                    }
                    for (int h = 0; h < toolData.propertyData.size(); h++)
                    {
                        tileMenu.selectedTiles.clear();
                        tileMenu.selectedTiles.add(tileTool);
                        PropertyData property = toolData.propertyData.get(h);
                        if (property instanceof LightPropertyData)
                        {
                            LightPropertyData lightPropertyData = (LightPropertyData) property;
                            propertyMenu.newProperty(lightPropertyData.r, lightPropertyData.g, lightPropertyData.b, lightPropertyData.a, lightPropertyData.distance, lightPropertyData.rayAmount);
                        } else if (property instanceof ColorPropertyData)
                        {
                            ColorPropertyData colorPropertyData = (ColorPropertyData) property;
                            propertyMenu.newProperty(colorPropertyData.r, colorPropertyData.g, colorPropertyData.b, colorPropertyData.a);
                        } else
                        {
                            NonColorPropertyData nonColorPropertyData = (NonColorPropertyData) property;
                            propertyMenu.newProperty(nonColorPropertyData.property, nonColorPropertyData.value);
                        }
                        tileMenu.selectedTiles.clear();
                    }
                    for (int h = 0; h < toolData.attachedObjects.size(); h++)
                    {
                        MapObjectData attachedObject = toolData.attachedObjects.get(h);
                        AttachedMapObject attachedMapObject = null;
                        if (attachedObject instanceof MapPolygonData)
                        {
                            MapPolygonData polygonData = (MapPolygonData) attachedObject;
                            attachedMapObject = new AttachedMapObject(this, null, null, polygonData.vertices, polygonData.xOffset, polygonData.yOffset, polygonData.width, polygonData.height, polygonData.x, polygonData.y);
                        } else if (attachedObject instanceof MapPointData)
                        {
                            MapPointData pointData = (MapPointData) attachedObject;
                            attachedMapObject = new AttachedMapObject(this, null, null, pointData.xOffset, pointData.yOffset, pointData.x, pointData.y);
                        }

                        for (int s = 0; s < toolData.attachedObjects.get(h).propertyData.size(); s++)
                        {
                            selectedObjects.clear();
                            selectedObjects.add(attachedMapObject);
                            PropertyData property = toolData.attachedObjects.get(h).propertyData.get(s);
                            if (property instanceof LightPropertyData)
                            {
                                LightPropertyData lightPropertyData = (LightPropertyData) property;
                                propertyMenu.newProperty(lightPropertyData.r, lightPropertyData.g, lightPropertyData.b, lightPropertyData.a, lightPropertyData.distance, lightPropertyData.rayAmount);
                            } else if (property instanceof ColorPropertyData)
                            {
                                ColorPropertyData colorPropertyData = (ColorPropertyData) property;
                                propertyMenu.newProperty(colorPropertyData.r, colorPropertyData.g, colorPropertyData.b, colorPropertyData.a);
                            } else
                            {
                                NonColorPropertyData nonColorPropertyData = (NonColorPropertyData) property;
                                propertyMenu.newProperty(nonColorPropertyData.property, nonColorPropertyData.value);
                            }
                            selectedObjects.clear();
                        }
                        if (attachedMapObject != null)
                            tileTool.mapObjects.add(attachedMapObject);
                    }
                }
            }
            else
            {
                SpriteSheetData spriteSheetData = (SpriteSheetData) tileMapData.sheets.get(i);
                ArrayList<ToolData> spriteTools = spriteSheetData.tools;
                for(int k = 0; k < spriteTools.size(); k ++)
                {
                    ToolData toolData = spriteTools.get(k);
                    TileTool tileTool = tileMenu.getTileTool(toolData.type, toolData.id, toolData.name, spriteSheetData.name);
                    tileTool.mapObjects.clear();
                    for(int h = 0; h < toolData.lockedPropertyData.size(); h ++)
                    {
                        NonColorPropertyData property = (NonColorPropertyData) toolData.lockedPropertyData.get(h);
                        tileTool.getPropertyField(property.property).value.setText(property.value);
                    }
                    for(int h = 0; h < toolData.propertyData.size(); h ++)
                    {
                        tileMenu.selectedTiles.clear();
                        tileMenu.selectedTiles.add(tileTool);
                        PropertyData property = toolData.propertyData.get(h);
                        if(property instanceof LightPropertyData)
                        {
                            LightPropertyData lightPropertyData = (LightPropertyData) property;
                            propertyMenu.newProperty(lightPropertyData.r, lightPropertyData.g, lightPropertyData.b, lightPropertyData.a, lightPropertyData.distance, lightPropertyData.rayAmount);
                        }
                        else if(property instanceof ColorPropertyData)
                        {
                            ColorPropertyData colorPropertyData = (ColorPropertyData) property;
                            propertyMenu.newProperty(colorPropertyData.r, colorPropertyData.g, colorPropertyData.b, colorPropertyData.a);
                        }
                        else
                        {
                            NonColorPropertyData nonColorPropertyData = (NonColorPropertyData) property;
                            propertyMenu.newProperty(nonColorPropertyData.property, nonColorPropertyData.value);
                        }
                        tileMenu.selectedTiles.clear();
                    }
                    for(int h = 0; h < toolData.attachedObjects.size(); h ++)
                    {
                        MapObjectData attachedObject = toolData.attachedObjects.get(h);
                        AttachedMapObject attachedMapObject = null;
                        if(attachedObject instanceof MapPolygonData)
                        {
                            MapPolygonData polygonData = (MapPolygonData) attachedObject;
                            attachedMapObject = new AttachedMapObject(this, null, null, polygonData.vertices, polygonData.xOffset, polygonData.yOffset, polygonData.width, polygonData.height, polygonData.x, polygonData.y);
                        }
                        else if(attachedObject instanceof MapPointData)
                        {
                            MapPointData pointData = (MapPointData) attachedObject;
                            attachedMapObject = new AttachedMapObject(this, null, null, pointData.xOffset, pointData.yOffset, pointData.x, pointData.y);
                        }

                        for(int s = 0; s < toolData.attachedObjects.get(h).propertyData.size(); s ++)
                        {
                            selectedObjects.clear();
                            selectedObjects.add(attachedMapObject);
                            PropertyData property = toolData.attachedObjects.get(h).propertyData.get(s);
                            if(property instanceof LightPropertyData)
                            {
                                LightPropertyData lightPropertyData = (LightPropertyData) property;
                                propertyMenu.newProperty(lightPropertyData.r, lightPropertyData.g, lightPropertyData.b, lightPropertyData.a, lightPropertyData.distance, lightPropertyData.rayAmount);
                            }
                            else if(property instanceof ColorPropertyData)
                            {
                                ColorPropertyData colorPropertyData = (ColorPropertyData) property;
                                propertyMenu.newProperty(colorPropertyData.r, colorPropertyData.g, colorPropertyData.b, colorPropertyData.a);
                            }
                            else
                            {
                                NonColorPropertyData nonColorPropertyData = (NonColorPropertyData) property;
                                propertyMenu.newProperty(nonColorPropertyData.property, nonColorPropertyData.value);
                            }
                            selectedObjects.clear();
                        }
                        if(attachedMapObject != null)
                            tileTool.mapObjects.add(attachedMapObject);
                    }
                }
            }
        }
        createDrawableAttachableMapObjects();
        propertyMenu.rebuild();
        PropertyToolPane.apply(this);
        findAllTilesToBeGrouped();
        undo.clear();
        redo.clear();
    }

    public void setName(String name)
    {
        this.name = name;
        mapPaneButton.setText(name);
    }

    public void searchForBlockedTiles()
    {
        for(int i = 0; i < layers.size; i ++)
        {
            if(!(layers.get(i) instanceof TileLayer))
                continue;
            tile:
            for(int k = 0; k < layers.get(i).tiles.size; k ++)
            {
                // Search every tile of every layer
                Tile tile = layers.get(i).tiles.get(k);
                tile.hasBlockedObjectOnTop = false; // reset
                // If a blocked object is on top of the center of the tile, the tile is blocked.
                float centerX = tile.position.x + tileSize / 2;
                float centerY = tile.position.y + tileSize / 2;

                for(int b = 0; b < bodies.size; b++)
                {
                    if(bodies.get(b).getFixtureList() != null)
                    {
                        if ((bodies.get(b).getFixtureList().first().testPoint(centerX, centerY) ||
                                bodies.get(b).getFixtureList().first().testPoint(centerX - tileSize / 4, centerY) ||
                                bodies.get(b).getFixtureList().first().testPoint(centerX + tileSize / 4, centerY) ||
                                bodies.get(b).getFixtureList().first().testPoint(centerX, centerY - tileSize / 4) ||
                                bodies.get(b).getFixtureList().first().testPoint(centerX, centerY + tileSize / 4)))
                        {
                            tile.hasBlockedObjectOnTop = true;
                            continue tile;
                        }
                    }
                }

//                // Search all tile tools
//                for(int s = 0; s < tileMenu.tileTable.getChildren().size; s ++)
//                {
//                    TileTool tileTool = (TileTool) tileMenu.tileTable.getChildren().get(s);
//                    for(int d = 0; d < tileTool.mapObjects.size; d ++)
//                    {
//                        AttachedMapObject attachedMapObject = tileTool.mapObjects.get(d);
//                        if(attachedMapObject.body == null && attachedMapObject.bodies != null)
//                        {
//                            for(int w = 0; w < attachedMapObject.bodies.size; w++)
//                            {
//                                if (!attachedMapObject.isPoint && (
//                                        attachedMapObject.bodies.get(w).getFixtureList().first().testPoint(centerX, centerY) ||
//                                        attachedMapObject.bodies.get(w).getFixtureList().first().testPoint(centerX - tileSize / 4, centerY) ||
//                                        attachedMapObject.bodies.get(w).getFixtureList().first().testPoint(centerX + tileSize / 4, centerY) ||
//                                        attachedMapObject.bodies.get(w).getFixtureList().first().testPoint(centerX, centerY - tileSize / 4) ||
//                                        attachedMapObject.bodies.get(w).getFixtureList().first().testPoint(centerX, centerY + tileSize / 4)))
//                                {
//                                    tile.hasBlockedObjectOnTop = true;
//                                    continue tile;
//                                }
//                            }
//                        }
//                        else if(attachedMapObject.bodies == null && attachedMapObject.body != null)
//                        {
//                            if (!attachedMapObject.isPoint && (
//                                    attachedMapObject.body.getFixtureList().first().testPoint(centerX, centerY) ||
//                                    attachedMapObject.body.getFixtureList().first().testPoint(centerX - tileSize / 4, centerY) ||
//                                    attachedMapObject.body.getFixtureList().first().testPoint(centerX + tileSize / 4, centerY) ||
//                                    attachedMapObject.body.getFixtureList().first().testPoint(centerX, centerY - tileSize / 4) ||
//                                    attachedMapObject.body.getFixtureList().first().testPoint(centerX, centerY + tileSize / 4)))
//                            {
//                                tile.hasBlockedObjectOnTop = true;
//                                continue tile;
//                            }
//                        }
//                    }
//                }
//
//                // Search all sprite tools
//                for(int s = 0; s < tileMenu.spriteTable.getChildren().size; s ++)
//                {
//                    TileTool tileTool = (TileTool) tileMenu.spriteTable.getChildren().get(s);
//                    for(int d = 0; d < tileTool.mapObjects.size; d ++)
//                    {
//                        AttachedMapObject attachedMapObject = tileTool.mapObjects.get(d);
//                        if(attachedMapObject.body == null && attachedMapObject.bodies != null)
//                        {
//                            for(int w = 0; w < attachedMapObject.bodies.size; w++)
//                            {
//                                if(((MapSprite)attachedMapObject.bodies.get(w).getUserData()).layer.z != tile.layer.z)
//                                    continue;
//                                if (!attachedMapObject.isPoint && (
//                                        attachedMapObject.bodies.get(w).getFixtureList().first().testPoint(centerX, centerY) ||
//                                        attachedMapObject.bodies.get(w).getFixtureList().first().testPoint(centerX - tileSize / 4, centerY) ||
//                                        attachedMapObject.bodies.get(w).getFixtureList().first().testPoint(centerX + tileSize / 4, centerY) ||
//                                        attachedMapObject.bodies.get(w).getFixtureList().first().testPoint(centerX, centerY - tileSize / 4) ||
//                                        attachedMapObject.bodies.get(w).getFixtureList().first().testPoint(centerX, centerY + tileSize / 4)))
//                                {
//                                    tile.hasBlockedObjectOnTop = true;
//                                    continue tile;
//                                }
//                            }
//                        }
//                        else if(attachedMapObject.bodies == null && attachedMapObject.body != null)
//                        {
//                            if (!attachedMapObject.isPoint && (
//                                    attachedMapObject.body.getFixtureList().first().testPoint(centerX, centerY) ||
//                                    attachedMapObject.body.getFixtureList().first().testPoint(centerX - tileSize / 4, centerY) ||
//                                    attachedMapObject.body.getFixtureList().first().testPoint(centerX + tileSize / 4, centerY) ||
//                                    attachedMapObject.body.getFixtureList().first().testPoint(centerX, centerY - tileSize / 4) ||
//                                    attachedMapObject.body.getFixtureList().first().testPoint(centerX, centerY + tileSize / 4)))
//                            {
//                                tile.hasBlockedObjectOnTop = true;
//                                continue tile;
//                            }
//                        }
//                    }
//                }


                // Search all object layers
                for(int s = 0; s < layers.size; s ++)
                {
                    if(!(layers.get(s) instanceof ObjectLayer))
                        continue;
                    for(int d = 0; d < layers.get(s).tiles.size; d++)
                    {
                        MapObject mapObject = (MapObject) layers.get(s).tiles.get(d);
                        if(mapObject.body == null)
                            continue;
                        if (!mapObject.isPoint && (
                                mapObject.body.getFixtureList().first().testPoint(centerX, centerY) ||
                                mapObject.body.getFixtureList().first().testPoint(centerX - tileSize / 4, centerY) ||
                                mapObject.body.getFixtureList().first().testPoint(centerX + tileSize / 4, centerY) ||
                                mapObject.body.getFixtureList().first().testPoint(centerX, centerY - tileSize / 4) ||
                                mapObject.body.getFixtureList().first().testPoint(centerX, centerY + tileSize / 4)))
                        {
                            tile.hasBlockedObjectOnTop = true;
                            continue tile;
                        }
                    }
                }
            }
        }
    }

    public void createDrawableAttachableMapObjects()
    {
        for(int i = 0; i < layers.size; i ++)
        {
            Layer layer = layers.get(i);
            if (layer instanceof ObjectLayer)
                continue;
            for (int w = 0; w < layer.tiles.size; w++)
            {
                Tile tile = layer.tiles.get(w);
                for(int s = 0; s < tile.drawableAttachedMapObjects.size; s++)
                {
                    tile.drawableAttachedMapObjects.get(s).removeBody();
                    tile.drawableAttachedMapObjects.get(s).removeLight();
                }
                tile.drawableAttachedMapObjects.clear();
                TileTool tileTool = tile.tool;
                if (tileTool == null)
                    continue;
                for (int k = 0; k < tileTool.mapObjects.size; k++)
                {
                    AttachedMapObject drawable = new AttachedMapObject(tileTool.mapObjects.get(k), tile);
                    drawable.setPosition(tile.position.x + drawable.parentAttached.positionOffset.x, tile.position.y + drawable.parentAttached.positionOffset.y);
                    tile.drawableAttachedMapObjects.add(drawable);
                }
            }
        }
    }

    public void updateAllDrawableAttachableMapObjectsPositions()
    {
        for(int i = 0; i < layers.size; i ++)
        {
            Layer layer = layers.get(i);
            if (layer instanceof ObjectLayer)
                return;
            for (int w = 0; w < layer.tiles.size; w++)
            {
                Tile tile = layer.tiles.get(w);
                TileTool tileTool = tile.tool;
                if (tileTool == null)
                    continue;
                for (int k = 0; k < tileTool.mapObjects.size; k++)
                {
                    AttachedMapObject mapObject = tileTool.mapObjects.get(k);
                    AttachedMapObject drawable;
                    for(int a = 0; a < tile.drawableAttachedMapObjects.size; a ++)
                    {
                        drawable = tile.drawableAttachedMapObjects.get(a);
                        if(tile.drawableAttachedMapObjects.get(a).id == mapObject.id)
                            drawable.setPosition(tile.position.x + drawable.parentAttached.positionOffset.x, tile.position.y + drawable.parentAttached.positionOffset.y);
                    }
                }
            }
        }
    }


    public void updateAllDrawableAttachableMapObjectsPolygons()
    {
        for(int i = 0; i < layers.size; i ++)
        {
            Layer layer = layers.get(i);
            if (layer instanceof ObjectLayer)
                return;
            for (int w = 0; w < layer.tiles.size; w++)
            {
                Tile tile = layer.tiles.get(w);
                TileTool tileTool = tile.tool;
                if (tileTool == null)
                    continue;
                for (int k = 0; k < tileTool.mapObjects.size; k++)
                {
                    AttachedMapObject mapObject = tileTool.mapObjects.get(k);
                    if(mapObject.isPoint)
                        continue;
                    AttachedMapObject drawable;
                    for(int a = 0; a < tile.drawableAttachedMapObjects.size; a ++)
                    {
                        drawable = tile.drawableAttachedMapObjects.get(a);
                        if(tile.drawableAttachedMapObjects.get(a).id == mapObject.id)
                        {
                            drawable.polygon.setVertices(mapObject.vertices);
                            if(drawable.body != null || (drawable.bodies != null && drawable.bodies.size > 0))
                            {
                                drawable.removeBody();
                                drawable.createBody();
                            }
                        }
                    }
                }
            }
        }
    }

    public void addDrawableAttachedMapObjects(TileTool tool)
    {
        for(int i = 0; i < layers.size; i ++)
        {
            Layer layer = layers.get(i);
            if (layer instanceof ObjectLayer)
                return;
            for (int w = 0; w < layer.tiles.size; w++)
            {
                Tile tile = layer.tiles.get(w);
                TileTool tileTool = tile.tool;
                if (tileTool == null || tileTool.id != tool.id)
                    continue;
                tile.drawableAttachedMapObjects.clear();
                for (int k = 0; k < tileTool.mapObjects.size; k++)
                {
                    AttachedMapObject drawable = new AttachedMapObject(tileTool.mapObjects.get(k), tile);
                    drawable.setPosition(tile.position.x + drawable.parentAttached.positionOffset.x, tile.position.y + drawable.parentAttached.positionOffset.y);
                    tile.drawableAttachedMapObjects.add(drawable);
                }
            }
        }
    }

    public void removeDrawableAttachedMapObjects(TileTool tool, int id)
    {
        for(int i = 0; i < layers.size; i ++)
        {
            Layer layer = layers.get(i);
            if (layer instanceof ObjectLayer)
                return;
            for (int w = 0; w < layer.tiles.size; w++)
            {
                Tile tile = layer.tiles.get(w);
                TileTool tileTool = tile.tool;
                if (tileTool == null || tileTool.id != tool.id)
                    continue;
                for(int a = 0; a < tile.drawableAttachedMapObjects.size; a ++)
                {
                    if(tile.drawableAttachedMapObjects.get(a).id == id)
                    {
                        tile.drawableAttachedMapObjects.get(a).removeBody();
                        tile.drawableAttachedMapObjects.get(a).removeLight();
                        tile.drawableAttachedMapObjects.removeIndex(a);
                        a--;
                    }
                }
            }
        }
    }
}
