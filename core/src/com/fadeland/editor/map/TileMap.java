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
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fadeland.editor.FadelandEditor;
import com.fadeland.editor.GameAssets;
import com.fadeland.editor.Utils;
import com.fadeland.editor.ui.fileMenu.Tools;
import com.fadeland.editor.ui.layerMenu.LayerMenu;
import com.fadeland.editor.ui.layerMenu.LayerTypes;
import com.fadeland.editor.ui.propertyMenu.PropertyMenu;
import com.fadeland.editor.ui.propertyMenu.PropertyToolPane;
import com.fadeland.editor.ui.tileMenu.TileMenu;
import com.fadeland.editor.ui.tileMenu.TileMenuTools;
import com.fadeland.editor.ui.tileMenu.TileTool;
import com.fadeland.editor.undoredo.Action;

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

    public static int mapWidth;
    public static int mapHeight;
    public static int tileSize;

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

    public TileMap(FadelandEditor editor, TileMapData tileMapData)
    {
        this.editor = editor;
        this.name = tileMapData.name;
        this.mapWidth = tileMapData.mapWidth;
        this.mapHeight = tileMapData.mapHeight;
        this.tileSize = tileMapData.tileSize;
        init();
        tileSize = tileMapData.tileSize; // TODO make this build the tiles. Currently doesn't
        setMapPropertiesAndObjects(tileMapData);
        this.apply = true;
    }

    public TileMap(FadelandEditor editor, String name)
    {
        this.editor = editor;
        this.name = name;
        this.mapWidth = 5;
        this.mapHeight = 5;
        this.tileSize = 64;
        init();
    }

    private void init()
    {
        this.undo = new Stack<>();
        this.redo = new Stack<>();

        this.layers = new Array<>();
        this.boxSelect = new BoxSelect(this);
        this.selectedSprites = new Array<>();
        this.selectedObjects = new Array<>();
        this.tileGroups = new Array<>();

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

        this.world = new World(new Vector2(0, 0), false);
        this.rayHandler = new RayHandler(this.world);
        this.rayHandler.setAmbientLight(.9f);
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
        b2dr = new Box2DDebugRenderer();

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
        this.editor.batch.end();
        this.rayHandler.setCombinedMatrix(camera);
        this.rayHandler.updateAndRender();
        this.editor.batch.begin();
        for(int i = 0; i < this.selectedSprites.size; i ++)
        {
            this.selectedSprites.get(i).drawRotationBox();
            this.selectedSprites.get(i).drawMoveBox();
        }
        for(int i = 0; i < this.selectedObjects.size; i ++)
            this.selectedObjects.get(i).drawMoveBox();
        this.editor.batch.end();

        this.editor.shapeRenderer.setColor(Color.BLACK);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        this.editor.shapeRenderer.begin();
        this.editor.shapeRenderer.set(ShapeRenderer.ShapeType.Line);
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
        if(selectedTile != null && selectedLayer != null && selectedLayer instanceof TileLayer && selectedLayer.tiles.contains(selectedTile, true))
        {
            this.editor.shapeRenderer.setColor(Color.GREEN);
            this.editor.shapeRenderer.rect(selectedTile.position.x, selectedTile.position.y, selectedTile.width, selectedTile.height);

            Vector3 mouseCoords = Utils.unproject(camera, Gdx.input.getX(), Gdx.input.getY());
            for(int k = 0; k < selectedTile.tool.mapObjects.size; k ++)
            {
                AttachedMapObject mapObject = selectedTile.tool.mapObjects.get(k);
                boolean selected = selectedObjects.contains(mapObject, true);
                mapObject.setPosition(selectedTile.position.x + mapObject.positionOffset.x, selectedTile.position.y + mapObject.positionOffset.y);
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

                for(int k = 0; k < mapSprite.tool.mapObjects.size; k ++)
                {
                    AttachedMapObject mapObject = mapSprite.tool.mapObjects.get(k);
                    boolean selected = selectedObjects.contains(mapObject, true);
                    mapObject.attachedTile = mapSprite;
                    mapObject.setPosition(mapSprite.position.x + mapObject.positionOffset.x, mapSprite.position.y + mapObject.positionOffset.y);
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
        else if(selectedLayer != null && selectedLayer instanceof TileLayer && editor.getFileTool() != null && (editor.getFileTool().tool == Tools.BIND || editor.getFileTool().tool == Tools.STAMP))
            ((TileLayer)this.selectedLayer).drawPossibleTileGroups();
        this.editor.shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        this.stage.act();
        this.stage.draw();

        // Move the attached object positions to the selected map sprites and tiles for MapInput, since there's only one instance of the object per many sprites and tiles
        if(selectedTile != null)
        {
            for (int i = 0; i < selectedTile.tool.mapObjects.size; i++)
            {
                AttachedMapObject mapObject = selectedTile.tool.mapObjects.get(i);
                mapObject.setPosition(selectedTile.position.x + mapObject.positionOffset.x, selectedTile.position.y + mapObject.positionOffset.y);
            }
        }
        if(selectedSprites.size == 1)
        {
            for (int i = 0; i < selectedSprites.first().tool.mapObjects.size; i++)
            {
                AttachedMapObject mapObject = selectedSprites.first().tool.mapObjects.get(i);
                mapObject.attachedTile = selectedSprites.first();
                mapObject.setPosition(selectedSprites.first().position.x + mapObject.positionOffset.x, selectedSprites.first().position.y + mapObject.positionOffset.y);
            }
        }
//        b2dr.render(this.world, camera.combined);
        if(apply)
        {
            apply = false;
            PropertyToolPane.apply(this);
            undo.clear();
            redo.clear();
        }
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
            if(editor.getTileTools().size > 0)
            {
                TileTool tile = editor.getTileTools().first();
                if (tile != null)
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
    public void performAction(Action action)
    {
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

    private void setMapPropertiesAndObjects(TileMapData tileMapData)
    {
        for(int i = 0; i < tileMapData.tileGroups.size(); i ++)
        {
            TileGroupData tileGroupData = tileMapData.tileGroups.get(i);
            TileGroup tileGroup = new TileGroup(tileGroupData.width, tileGroupData.height, tileGroupData.boundGroupIds, tileGroupData.types, this);
            tileGroups.add(tileGroup);
        }
        for(int i = tileMapData.layers.size() - 1; i >= 0; i --)
        {
            Layer layer;
            LayerTypes layerTypes = null;
            LayerData savedLayer = tileMapData.layers.get(i);

            if(savedLayer instanceof TileLayerData)
                layerTypes = LayerTypes.TILE;
            else if(savedLayer instanceof MapSpriteLayerData)
                layerTypes = LayerTypes.SPRITE;
            else if(savedLayer instanceof MapObjectLayerData)
                layerTypes = LayerTypes.OBJECT;

            if(layerTypes != null)
            {
                layer = layerMenu.newLayer(layerTypes);
                layer.layerField.layerName.setText(tileMapData.layers.get(i).name);

                if(layerTypes == LayerTypes.TILE)
                {
                    TileLayerData savedTileLayer = (TileLayerData) savedLayer;
                    for(int k = 0; k < savedTileLayer.tiles.size(); k ++)
                    {
                        int id = savedTileLayer.tiles.get(k).id;
                        TileTool tileTool = tileMenu.getTileTool(TileMenuTools.TILE, id);
                        layer.tiles.get(k).setTool(tileTool);
                    }
                }
                else if(layerTypes == LayerTypes.SPRITE)
                {
                    MapSpriteLayerData savedSpriteLayer = (MapSpriteLayerData) savedLayer;
                    for(int k = 0; k < savedSpriteLayer.tiles.size(); k ++)
                    {
                        int id = savedSpriteLayer.tiles.get(k).id;
                        TileTool tileTool = tileMenu.getTileTool(TileMenuTools.SPRITE, id);
                        MapSprite mapSprite = input.newMapSprite(this, tileTool, savedSpriteLayer.tiles.get(k).x + savedSpriteLayer.tiles.get(k).width / 2, savedSpriteLayer.tiles.get(k).y + savedSpriteLayer.tiles.get(k).height / 2);
                        Utils.setCenterOrigin(mapSprite.position.x, mapSprite.position.y);
                        mapSprite.rotate(savedSpriteLayer.tiles.get(k).rotation);
                        layer.tiles.add(mapSprite);
                    }
                }
                else if(layerTypes == LayerTypes.OBJECT)
                {
                    MapObjectLayerData savedObjectLayer = (MapObjectLayerData) savedLayer;
                    for(int k = 0; k < savedObjectLayer.tiles.size(); k ++)
                    {
                        MapObject mapObject = null;
                        if(savedObjectLayer.tiles.get(k) instanceof MapPolygonData)
                        {
                            MapPolygonData mapPolygonData = (MapPolygonData) savedObjectLayer.tiles.get(k);
                            mapObject = new MapObject(this, mapPolygonData.vertices, mapPolygonData.x, mapPolygonData.y);
                        }
                        else if(savedObjectLayer.tiles.get(k) instanceof MapPointData)
                        {
                            MapPointData mapPointData = (MapPointData) savedObjectLayer.tiles.get(k);
                            mapObject = new MapObject(this, mapPointData.x, mapPointData.y);
                        }
                        if(mapObject != null)
                        {
                            for(int s = 0; s < savedObjectLayer.tiles.get(k).propertyData.size(); s ++)
                            {
                                selectedObjects.clear();
                                selectedObjects.add(mapObject);
                                propertyMenu.newProperty(savedObjectLayer.tiles.get(k).propertyData.get(s).property, savedObjectLayer.tiles.get(k).propertyData.get(s).value);
                                selectedObjects.clear();
                            }
                            layer.tiles.add(mapObject);
                        }
                    }
                }
            }
        }

        for(int i = 0; i < tileMapData.tileTools.size(); i ++)
        {
            ToolData toolData = tileMapData.tileTools.get(i);
            TileTool tileTool = tileMenu.getTileTool(toolData.type, toolData.id);
            for(int k = 0; k < toolData.lockedPropertyData.size(); k ++)
                tileTool.getPropertyField(toolData.lockedPropertyData.get(k).property).value.setText(toolData.lockedPropertyData.get(k).value);
            for(int k = 0; k < toolData.propertyData.size(); k ++)
            {
                tileMenu.selectedTiles.clear();
                tileMenu.selectedTiles.add(tileTool);
                propertyMenu.newProperty(toolData.propertyData.get(k).property, toolData.propertyData.get(k).value);
                tileMenu.selectedTiles.clear();
            }
            for(int k = 0; k < toolData.attachedObjects.size(); k ++)
            {
                MapObjectData attachedObject = toolData.attachedObjects.get(k);
                AttachedMapObject attachedMapObject = null;
                if(attachedObject instanceof MapPolygonData)
                {
                    MapPolygonData polygonData = (MapPolygonData) attachedObject;
                    attachedMapObject = new AttachedMapObject(this, null, polygonData.vertices, polygonData.xOffset, polygonData.yOffset, polygonData.width, polygonData.height, polygonData.x, polygonData.y);
                }
                else if(attachedObject instanceof MapPointData)
                {
                    MapPointData pointData = (MapPointData) attachedObject;
                    attachedMapObject = new AttachedMapObject(this, null, pointData.xOffset, pointData.yOffset, pointData.x, pointData.y);
                }

                for(int s = 0; s < toolData.attachedObjects.get(k).propertyData.size(); s ++)
                {
                    selectedObjects.clear();
                    selectedObjects.add(attachedMapObject);
                    propertyMenu.newProperty(toolData.attachedObjects.get(k).propertyData.get(s).property, toolData.attachedObjects.get(k).propertyData.get(s).value);
                    selectedObjects.clear();
                }
                if(attachedMapObject != null)
                    tileTool.mapObjects.add(attachedMapObject);
            }
        }
        for(int i = 0; i < tileMapData.spriteTools.size(); i ++)
        {
            ToolData toolData = tileMapData.spriteTools.get(i);
            TileTool tileTool = tileMenu.getTileTool(toolData.type, toolData.id);
            for(int k = 0; k < toolData.lockedPropertyData.size(); k ++)
                tileTool.getPropertyField(toolData.lockedPropertyData.get(k).property).value.setText(toolData.lockedPropertyData.get(k).value);
            for(int k = 0; k < toolData.propertyData.size(); k ++)
            {
                tileMenu.selectedTiles.clear();
                tileMenu.selectedTiles.add(tileTool);
                propertyMenu.newProperty(toolData.propertyData.get(k).property, toolData.propertyData.get(k).value);
                tileMenu.selectedTiles.clear();
            }
            for(int k = 0; k < toolData.attachedObjects.size(); k ++)
            {
                MapObjectData attachedObject = toolData.attachedObjects.get(k);
                AttachedMapObject attachedMapObject = null;
                if(attachedObject instanceof MapPolygonData)
                {
                    MapPolygonData polygonData = (MapPolygonData) attachedObject;
                    attachedMapObject = new AttachedMapObject(this, null, polygonData.vertices, polygonData.xOffset, polygonData.yOffset, polygonData.width, polygonData.height, polygonData.x, polygonData.y);
                }
                else if(attachedObject instanceof MapPointData)
                {
                    MapPointData pointData = (MapPointData) attachedObject;
                    attachedMapObject = new AttachedMapObject(this, null, pointData.xOffset, pointData.yOffset, pointData.x, pointData.y);
                }

                for(int s = 0; s < toolData.attachedObjects.get(k).propertyData.size(); s ++)
                {
                    selectedObjects.clear();
                    selectedObjects.add(attachedMapObject);
                    propertyMenu.newProperty(toolData.attachedObjects.get(k).propertyData.get(s).property, toolData.attachedObjects.get(k).propertyData.get(s).value);
                    selectedObjects.clear();
                }
                if(attachedMapObject != null)
                    tileTool.mapObjects.add(attachedMapObject);
            }
        }
        propertyMenu.rebuild();
        PropertyToolPane.apply(this);
        findAllTilesToBeGrouped();
        undo.clear();
        redo.clear();
    }
}
