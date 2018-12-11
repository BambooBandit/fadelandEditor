package com.fadeland.editor.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.ObjectMap;
import com.fadeland.editor.FadelandEditor;
import com.fadeland.editor.GameAssets;
import com.fadeland.editor.Utils;
import com.fadeland.editor.ui.fileMenu.Tools;
import com.fadeland.editor.ui.propertyMenu.PropertyField;
import com.fadeland.editor.ui.propertyMenu.PropertyToolPane;
import com.fadeland.editor.ui.tileMenu.TileTool;
import com.fadeland.editor.undoredo.*;

import static com.fadeland.editor.map.TileMap.tileSize;

public class MapInput implements InputProcessor
{
    private FadelandEditor editor;
    private TileMap map;

    private Vector2 dragOrigin;
    private Vector3 pos;
    private ObjectMap<Tile, Float> oldXofDragMap;
    private ObjectMap<Tile, Float> oldYofDragMap;

    private boolean draggingRotateBox = false;
    private boolean draggingMoveBox = false;

    public FloatArray objectVertices; // allows for seeing where you are clicking when constructing a new MapObject polygon
    public Vector2 objectVerticePosition;

    public boolean isDrawingObjectPolygon = false;

    public MapInput(FadelandEditor editor, TileMap map)
    {
        this.editor = editor;
        this.map = map;
        this.dragOrigin = new Vector2();
        this.pos = new Vector3();
        this.oldXofDragMap = new ObjectMap<>();
        this.oldYofDragMap = new ObjectMap<>();

        this.objectVertices = new FloatArray();
        this.objectVerticePosition = new Vector2();
    }

    @Override
    public boolean keyDown(int keycode)
    {
        if(keycode == Input.Keys.FORWARD_DEL)
        {
            if(map.selectedLayer != null)
            {
                boolean deletedAttached = false;
                if(map.selectedLayer != null && (map.selectedLayer instanceof SpriteLayer || map.selectedLayer instanceof TileLayer))
                {
                    CreateOrRemoveAttachedObject createOrRemoveAttachedObject = new CreateOrRemoveAttachedObject(map, map.selectedLayer.tiles, map.selectedObjects);
                    for (int i = 0; i < map.selectedLayer.tiles.size; i++)
                    {
                        if(map.selectedLayer.tiles.get(i).tool != null)
                        {
                            midLoop:
                            for (int s = 0; s < map.selectedLayer.tiles.get(i).tool.mapObjects.size; s++)
                            {
                                for (int k = 0; k < map.selectedObjects.size; k++)
                                {
                                    if (map.selectedLayer.tiles.get(i).tool.mapObjects.get(s) == map.selectedObjects.get(k))
                                    {
                                        deletedAttached = true;
                                        map.selectedObjects.removeValue(map.selectedLayer.tiles.get(i).tool.mapObjects.get(s), true);
                                        map.selectedLayer.tiles.get(i).tool.mapObjects.get(s).removeBody();
                                        map.selectedLayer.tiles.get(i).tool.mapObjects.get(s).removeLight();
                                        map.selectedLayer.tiles.get(i).tool.mapObjects.removeIndex(s);
                                        s--;
                                        if(s < 0)
                                            break midLoop;
                                    }
                                }
                            }
                        }
                    }
                    createOrRemoveAttachedObject.addAttachedObjects();
                    map.performAction(createOrRemoveAttachedObject);
                }
                if(deletedAttached)
                    return false;
                CreateOrRemoveSprite createOrRemoveSprite = new CreateOrRemoveSprite(map, map.selectedLayer.tiles, map.selectedSprites);
                CreateOrRemoveObject createOrRemoveObject = new CreateOrRemoveObject(map, map.selectedLayer.tiles, map.selectedObjects);
                map.selectedLayer.tiles.removeAll(map.selectedSprites, true);
                map.selectedLayer.tiles.removeAll(map.selectedObjects, true);
                map.selectedSprites.clear();
                map.selectedObjects.clear();
                map.propertyMenu.rebuild();
                createOrRemoveSprite.addSprites();
                map.performAction(createOrRemoveSprite);
                createOrRemoveObject.addObjects();
                map.performAction(createOrRemoveObject);
                PropertyToolPane.updateLightsAndBlocked(map);
            }
        }
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
        map.stage.unfocusAll();
        Vector3 coords = Utils.unproject(map.camera, screenX, screenY);
        this.dragOrigin.set(coords.x, coords.y);
        if(button == Input.Buttons.RIGHT && isDrawingObjectPolygon)
        {
            isDrawingObjectPolygon = false;
            if(objectVertices.size >= 6) // Polygons can have a minimum of 3 sides
            {
                MapObject mapObject;
                if(map.selectedLayer instanceof ObjectLayer)
                {
                    CreateOrRemoveObject createOrRemoveObject = new CreateOrRemoveObject(map, map.selectedLayer.tiles, null);
                    mapObject = new MapObject(map, objectVertices.toArray(), objectVerticePosition.x, objectVerticePosition.y);
                    ((ObjectLayer) map.selectedLayer).tiles.add(mapObject);
                    createOrRemoveObject.addObjects();
                    map.performAction(createOrRemoveObject);
                }
                else if(map.selectedLayer instanceof SpriteLayer)
                {
                    CreateOrRemoveAttachedObject createOrRemoveAttachedObject = new CreateOrRemoveAttachedObject(map, map.selectedLayer.tiles, null);
                    MapSprite mapSprite = map.selectedSprites.first();
                    Polygon antiRotatePolygon = new Polygon(objectVertices.toArray());
                    antiRotatePolygon.setOrigin(-(objectVerticePosition.x - mapSprite.position.x) + mapSprite.width / 2, -(objectVerticePosition.y - mapSprite.position.y) + mapSprite.height / 2);
                    antiRotatePolygon.rotate(-mapSprite.sprite.getRotation());
                    mapObject = new AttachedMapObject(map, mapSprite, antiRotatePolygon.getTransformedVertices(), objectVerticePosition.x - mapSprite.position.x, objectVerticePosition.y - mapSprite.position.y, mapSprite.sprite.getWidth(), mapSprite.sprite.getHeight(), objectVerticePosition.x, objectVerticePosition.y);
                    mapSprite.addMapObject((AttachedMapObject) mapObject);
                    createOrRemoveAttachedObject.addAttachedObjects();
                    map.performAction(createOrRemoveAttachedObject);
                }
                else if(map.selectedLayer instanceof TileLayer)
                {
                    CreateOrRemoveAttachedObject createOrRemoveAttachedObject = new CreateOrRemoveAttachedObject(map, map.selectedLayer.tiles, null);
                    Tile selectedTile = map.selectedTile;
                    mapObject = new AttachedMapObject(map, selectedTile, objectVertices.toArray(), objectVerticePosition.x - selectedTile.position.x, objectVerticePosition.y - selectedTile.position.y, selectedTile.sprite.getWidth(), selectedTile.sprite.getHeight(), objectVerticePosition.x, objectVerticePosition.y);
                    selectedTile.addMapObject((AttachedMapObject) mapObject);
                    createOrRemoveAttachedObject.addAttachedObjects();
                    map.performAction(createOrRemoveAttachedObject);
                }
            }
            objectVertices.clear();
            return false;
        }
        else if(editor.getFileTool() != null && editor.getFileTool().tool == Tools.DRAWPOINT)
        {
            MapObject mapObject;
            if(map.selectedLayer instanceof ObjectLayer)
            {
                CreateOrRemoveObject createOrRemoveObject = new CreateOrRemoveObject(map, map.selectedLayer.tiles, null);
                mapObject = new MapObject(map, coords.x, coords.y);
                ((ObjectLayer) map.selectedLayer).tiles.add(mapObject);
                createOrRemoveObject.addObjects();
                map.performAction(createOrRemoveObject);
            }
            else if(map.selectedLayer instanceof SpriteLayer)
            {
                CreateOrRemoveAttachedObject createOrRemoveAttachedObject = new CreateOrRemoveAttachedObject(map, map.selectedLayer.tiles, null);
                MapSprite mapSprite = map.selectedSprites.first();
                mapObject = new AttachedMapObject(map, mapSprite, coords.x - mapSprite.position.x, coords.y - mapSprite.position.y, coords.x, coords.y);
                mapSprite.addMapObject((AttachedMapObject) mapObject);
                createOrRemoveAttachedObject.addAttachedObjects();
                map.performAction(createOrRemoveAttachedObject);
            }
            else if(map.selectedLayer instanceof TileLayer)
            {
                CreateOrRemoveAttachedObject createOrRemoveAttachedObject = new CreateOrRemoveAttachedObject(map, map.selectedLayer.tiles, null);
                Tile selectedTile = map.selectedTile;
                mapObject = new AttachedMapObject(map, selectedTile, coords.x - selectedTile.position.x, coords.y - selectedTile.position.y, coords.x, coords.y);
                selectedTile.addMapObject((AttachedMapObject) mapObject);
                createOrRemoveAttachedObject.addAttachedObjects();
                map.performAction(createOrRemoveAttachedObject);
            }
        }
        if(editor.getFileTool() != null && editor.getFileTool().tool == Tools.BOXSELECT && (map.selectedLayer instanceof SpriteLayer || map.selectedLayer instanceof ObjectLayer))
        {
            map.boxSelect.startDrag(coords.x, coords.y);
            return false;
        }
        else if(editor.getFileTool() != null && editor.getFileTool().tool == Tools.DRAWOBJECT &&
                (map.selectedLayer instanceof ObjectLayer || map.selectedSprites.size == 1 || map.selectedTile != null))
        {
            isDrawingObjectPolygon = true;
            if(this.objectVertices.size == 0)
                objectVerticePosition.set(coords.x, coords.y);
            this.objectVertices.add(coords.x - objectVerticePosition.x);
            this.objectVertices.add(coords.y - objectVerticePosition.y);
        }

        MoveSprite moveSprite = null;
        for (int i = 0; i < map.selectedSprites.size; i++)
        {
            if (map.selectedSprites.get(i).moveBox.contains(coords.x, coords.y))
            {
                moveSprite = new MoveSprite(oldXofDragMap, oldYofDragMap);
                break;
            }
        }
        if (moveSprite != null)
        {
            for (int i = 0; i < map.selectedSprites.size; i++)
                moveSprite.addSprite(map.selectedSprites.get(i));
            map.performAction(moveSprite);
        }
        else // rotate
        {
            RotateSprite rotateSprite = null;
            for (int i = 0; i < map.selectedSprites.size; i++)
            {
                if (map.selectedSprites.get(i).rotationBox.contains(coords.x, coords.y))
                {
                    rotateSprite = new RotateSprite(map);
                    break;
                }
            }
            if (rotateSprite != null)
            {
                for (int i = 0; i < map.selectedSprites.size; i++)
                    rotateSprite.addSprite(map.selectedSprites.get(i));
                map.performAction(rotateSprite);
            }
        }
        for(int i = 0; i < map.selectedSprites.size; i ++)
        {
            if(map.selectedSprites.get(i).rotationBox.contains(coords.x, coords.y))
            {
                // If clicked rotateBox with SELECT tool, ignore everything
                this.draggingRotateBox = true;

                float xSum = 0, ySum = 0;
                for(MapSprite mapSprite : map.selectedSprites)
                {
                    xSum += mapSprite.position.x;
                    ySum += mapSprite.position.y;
                }
                float xAverage = xSum / map.selectedSprites.size;
                float yAverage = ySum / map.selectedSprites.size;
                Utils.setCenterOrigin(xAverage, yAverage);

                return false;
            }
            else if(map.selectedSprites.get(i).moveBox.contains(coords.x, coords.y))
            {
                // If clicked moveBox with SELECT tool, ignore everything
                this.draggingMoveBox = true;

                this.oldXofDragMap.clear();
                this.oldYofDragMap.clear();
                for(int k = 0; k < map.selectedSprites.size; k ++)
                {
                    this.oldXofDragMap.put(map.selectedSprites.get(k), map.selectedSprites.get(k).position.x);
                    this.oldYofDragMap.put(map.selectedSprites.get(k), map.selectedSprites.get(k).position.y);
                }

                float xSum = 0, ySum = 0;
                for(MapSprite mapSprite : map.selectedSprites)
                {
                    xSum += mapSprite.position.x;
                    ySum += mapSprite.position.y;
                }
                float xAverage = xSum / map.selectedSprites.size;
                float yAverage = ySum / map.selectedSprites.size;
                Utils.setCenterOrigin(xAverage, yAverage);

                return false;
            }
        }
        if(map.selectedObjects.size > 0 && map.selectedObjects.first() instanceof AttachedMapObject)
        {
            MoveAttachedObject moveObject = null;
            for (int i = 0; i < map.selectedObjects.size; i++)
            {
                if (map.selectedObjects.get(i).moveBox.contains(coords.x, coords.y))
                {
                    moveObject = new MoveAttachedObject(oldXofDragMap, oldYofDragMap);
                    break;
                }
            }
            if (moveObject != null)
            {
                for (int i = 0; i < map.selectedObjects.size; i++)
                    moveObject.addObject((AttachedMapObject) map.selectedObjects.get(i));
                map.performAction(moveObject);
            }
        }
        else if(map.selectedObjects.size > 0 && map.selectedObjects.first() instanceof MapObject)
        {
            MoveObject moveObject = null;
            for (int i = 0; i < map.selectedObjects.size; i++)
            {
                if (map.selectedObjects.get(i).moveBox.contains(coords.x, coords.y))
                {
                    moveObject = new MoveObject(oldXofDragMap, oldYofDragMap);
                    break;
                }
            }
            if (moveObject != null)
            {
                for (int i = 0; i < map.selectedObjects.size; i++)
                    moveObject.addObject(map.selectedObjects.get(i));
                map.performAction(moveObject);
            }
        }
        for(int i = 0; i < map.selectedObjects.size; i ++)
        {
            if(map.selectedObjects.get(i).moveBox.contains(coords.x, coords.y))
            {
                // If clicked moveBox with SELECT tool, ignore everything
                this.draggingMoveBox = true;

                if(editor.getFileTool() != null && editor.getFileTool().tool == Tools.OBJECTVERTICESELECT)
                {
                    MapObject mapObject = map.selectedObjects.get(i);
                    if(mapObject.indexOfSelectedVertice != -1)
                    {
                        MoveVertice moveVertice = new MoveVertice(mapObject, mapObject.getVerticeX(), mapObject.getVerticeY());
                        map.performAction(moveVertice);
                    }
                }

                this.oldXofDragMap.clear();
                this.oldYofDragMap.clear();
                for(int k = 0; k < map.selectedObjects.size; k ++)
                {
                    if(map.selectedObjects.get(k) instanceof AttachedMapObject)
                    {
                        this.oldXofDragMap.put(map.selectedObjects.get(k), ((AttachedMapObject)map.selectedObjects.get(k)).positionOffset.x);
                        this.oldYofDragMap.put(map.selectedObjects.get(k), ((AttachedMapObject)map.selectedObjects.get(k)).positionOffset.y);
                    }
                    else
                    {
                        this.oldXofDragMap.put(map.selectedObjects.get(k), map.selectedObjects.get(k).position.x);
                        this.oldYofDragMap.put(map.selectedObjects.get(k), map.selectedObjects.get(k).position.y);
                    }
                }

                float xSum = 0, ySum = 0;
                for(MapObject mapObject : map.selectedObjects)
                {
                    xSum += mapObject.position.x;
                    ySum += mapObject.position.y;
                }
                float xAverage = xSum / map.selectedObjects.size;
                float yAverage = ySum / map.selectedObjects.size;
                Utils.setCenterOrigin(xAverage, yAverage);

                return false;
            }
        }
        if(editor.getFileTool() != null && map.selectedObjects.size == 1 && !map.selectedObjects.first().isPoint && editor.getFileTool().tool == Tools.OBJECTVERTICESELECT)
        {
            SelectVertice selectVertice = new SelectVertice(map.selectedObjects.first(), map.selectedObjects.first().indexOfSelectedVertice, map.selectedObjects.first().indexOfHoveredVertice);
            map.performAction(selectVertice);
            map.selectedObjects.first().indexOfSelectedVertice = map.selectedObjects.first().indexOfHoveredVertice;
            map.selectedObjects.first().setPosition(map.selectedObjects.first().polygon.getX(), map.selectedObjects.first().polygon.getY()); // Move the movebox to where the selected vertice is
        }
        if(map.selectedLayer instanceof TileLayer && editor.getTileTools() != null)
        {
            if(editor.getTileTools().size > 0 && editor.getTileTools().first() instanceof TileTool && editor.getFileTool() != null && editor.getFileTool().tool == Tools.FILL)
            {
                for(int i = 0; i < map.selectedLayer.tiles.size; i ++)
                    map.selectedLayer.tiles.get(i).hasBeenPainted = false;
                Tile clickedTile = map.getTile(coords.x, coords.y - tileSize);
                if(clickedTile != null)
                {
                    map.performAction(new PlaceTile(map));
                    fill(coords.x, coords.y, clickedTile.tool);
                    map.findAllTilesToBeGrouped();
                }
            }
            else if(editor.getTileTools().size > 1 && editor.getTileTools().first() instanceof TileTool && editor.getFileTool() != null && editor.fileMenu.toolPane.random.selected)
            {
                // Randomly pick a tile from the selected tiles based on weighted probabilities
                Tile clickedTile = map.getTile(coords.x, coords.y - tileSize);
                TileTool randomTile = randomTile();
                if(randomTile != null && editor.getFileTool().tool == Tools.BRUSH)
                {
                    map.performAction(new PlaceTile(map, clickedTile, clickedTile.tool, randomTile));
                    clickedTile.setTool(randomTile);
                }
                map.findAllTilesToBeGrouped();
            }
            else
            {
                if(map.selectedTile != null)
                {
                    for(int k = 0; k < map.selectedTile.tool.mapObjects.size; k ++)
                    {
                        AttachedMapObject attachedMapObject = map.selectedTile.tool.mapObjects.get(k);
                        if (attachedMapObject.isHoveredOver(coords.x, coords.y))
                        {
                            if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
                            {
                                if(map.selectedObjects.contains(attachedMapObject, true))
                                {
                                    map.selectedObjects.removeValue(attachedMapObject, true);
                                    attachedMapObject.unselect();
                                }
                                else
                                {
                                    map.selectedObjects.add(attachedMapObject);
                                    attachedMapObject.select();
                                }
                                map.propertyMenu.rebuild();
                            }
                            else
                            {
                                for (int s = 0; s < map.selectedObjects.size; s++)
                                    map.selectedObjects.get(s).unselect();
                                map.selectedObjects.clear();
                                map.selectedObjects.add(attachedMapObject);
                                attachedMapObject.select();
                                map.propertyMenu.rebuild();
                            }
                            return false;
                        }
                    }
                }
                if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
                {
                    if(map.selectedTile != null)
                        map.performAction(new SelectTile(map, map.selectedTile, null));
                    map.selectedTile = null;
                    return false;
                }
                if(editor.getTileTools().size > 1 && editor.getFileTool().tool == Tools.BRUSH)
                    map.performAction(new PlaceTile(map));
                Tile clickedTile = map.getTile(coords.x, coords.y - tileSize);
                if(editor.getFileTool() != null && editor.getFileTool().tool == Tools.SELECT)
                {
                    if(clickedTile != null && clickedTile.tool != null)
                    {
                        if(map.selectedTile != clickedTile)
                            map.performAction(new SelectTile(map, map.selectedTile, clickedTile));
                        map.selectedTile = clickedTile;
                    }
                }
                for (int i = 0; i < editor.getTileTools().size; i++)
                {
                    int xOffset = editor.getTileTools().first().x - editor.getTileTools().get(i).x;
                    int yOffset = editor.getTileTools().first().y - editor.getTileTools().get(i).y;
                    clickedTile = map.getTile(coords.x + xOffset, coords.y + yOffset - tileSize);
                    if (editor.getFileTool() != null && clickedTile != null)
                    {
                        if (editor.getFileTool().tool == Tools.BRUSH)
                        {
                            if(editor.getTileTools().size > 1)
                            {
                                PlaceTile placeTile = (PlaceTile) map.undo.pop();
                                placeTile.addTile(clickedTile, clickedTile.tool, editor.getTileTools().get(i));
                                map.undo.push(placeTile);
                            }
                            else
                                map.performAction(new PlaceTile(map, clickedTile, clickedTile.tool, editor.getTileTools().get(i)));
                            clickedTile.setTool(editor.getTileTools().get(i));
                        }
                        else if (editor.getFileTool().tool == Tools.ERASER)
                        {
                            map.performAction(new PlaceTile(map, clickedTile, clickedTile.tool, null));
                            clickedTile.setTool(null);
                        }
                        map.findAllTilesToBeGrouped();
                    }
                }
                if (editor.getTileTools() != null && editor.getTileTools().size > 1 && editor.getFileTool().tool == Tools.BIND)
                {
                    if(button == Input.Buttons.LEFT) // bind
                    {
                        TileGroup tileGroup = new TileGroup(coords.x, coords.y, editor.getTileTools(), map);
                        map.performAction(new AddOrRemoveTileGroup(map, map.tileGroups, tileGroup, true));
                        map.tileGroups.add(tileGroup);
                        map.findAllTilesToBeGrouped();
                    }
                    else if(button == Input.Buttons.RIGHT) // unbind
                    {
                        Array<PossibleTileGroup> possibleTileGroups = ((TileLayer) map.selectedLayer).possibleTileGroups;
                        for(int i = 0; i < possibleTileGroups.size; i ++)
                        {
                            if(possibleTileGroups.get(i).clickedGroup(coords.x, coords.y))
                            {
                                for(int k = 0; k < possibleTileGroups.get(i).tileGroups.size; k ++)
                                    map.performAction(new AddOrRemoveTileGroup(map, map.tileGroups, possibleTileGroups.get(i).tileGroups.get(k), false));
                                map.tileGroups.removeAll(possibleTileGroups.get(i).tileGroups, true);
                                map.findAllTilesToBeGrouped();
                                break;
                            }
                        }
                    }
                }
                else if (editor.getFileTool() != null && editor.getFileTool().tool == Tools.STAMP)
                {
                    Array<PossibleTileGroup> possibleTileGroups = ((TileLayer) map.selectedLayer).possibleTileGroups;
                    for(int i = 0; i < possibleTileGroups.size; i ++)
                    {
                        if(possibleTileGroups.get(i).clickedGroup(coords.x, coords.y))
                        {
                            possibleTileGroups.get(i).stamp();
                            map.findAllTilesToBeGrouped();
                            break;
                        }
                    }
                }
            }
        }
        else if(map.selectedLayer instanceof SpriteLayer)
        {
            if(editor.getFileTool() != null)
            {
                if(editor.getFileTool().tool == Tools.SELECT)
                {
                    outerloop:
                    for (int i = map.selectedLayer.tiles.size - 1; i >= 0; i--)
                    {
                        MapSprite mapSprite = ((MapSprite) map.selectedLayer.tiles.get(i));
                        for(int k = 0; k < mapSprite.tool.mapObjects.size; k ++)
                        {
                            AttachedMapObject attachedMapObject = mapSprite.tool.mapObjects.get(k);
                            if (attachedMapObject.isHoveredOver(coords.x, coords.y))
                            {
                                SelectObject selectObject = new SelectObject(map, map.selectedObjects);
                                if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
                                {
                                    if(map.selectedObjects.contains(attachedMapObject, true))
                                    {
                                        map.selectedObjects.removeValue(attachedMapObject, true);
                                        attachedMapObject.unselect();
                                    }
                                    else
                                    {
                                        map.selectedObjects.add(attachedMapObject);
                                        attachedMapObject.select();
                                    }
                                    map.propertyMenu.rebuild();
                                }
                                else
                                {
                                    for (int s = 0; s < map.selectedObjects.size; s++)
                                        map.selectedObjects.get(s).unselect();
                                    map.selectedObjects.clear();
                                    map.selectedObjects.add(attachedMapObject);
                                    attachedMapObject.select();
                                    map.propertyMenu.rebuild();
                                }
                                selectObject.addSelected();
                                map.performAction(selectObject);
                                break outerloop;
                            }
                        }

                        if (mapSprite.polygon.contains(coords.x, coords.y))
                        {
                            SelectSprite selectSprite = new SelectSprite(map, map.selectedSprites);
                            if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
                            {
                                if(map.selectedSprites.contains(mapSprite, true))
                                {
                                    map.selectedSprites.removeValue(mapSprite, true);
                                    mapSprite.unselect();
                                }
                                else
                                {
                                    map.selectedSprites.add(mapSprite);
                                    mapSprite.select();
                                    map.propertyMenu.spritePropertyPanel.setVisible(true);
                                }
                                map.propertyMenu.rebuild();
                            }
                            else
                            {
                                for (int k = 0; k < map.selectedSprites.size; k++)
                                    map.selectedSprites.get(k).unselect();
                                map.selectedSprites.clear();
                                map.selectedSprites.add(mapSprite);
                                map.propertyMenu.spritePropertyPanel.setVisible(true);
                                mapSprite.select();
                                map.propertyMenu.rebuild();
                            }
                            selectSprite.addSelected();
                            map.performAction(selectSprite);
                            break;
                        }
                    }
                }
                else if(editor.getSpriteTool() != null && editor.getFileTool() != null && editor.getFileTool().tool == Tools.BRUSH &&
                        coords.x > 0 && coords.y > 0 && coords.x < map.selectedLayer.width * tileSize && coords.y < map.selectedLayer.height * tileSize)
                {
                    CreateOrRemoveSprite createOrRemoveSprite = new CreateOrRemoveSprite(map, ((SpriteLayer) map.selectedLayer).tiles, null);
                    MapSprite mapSprite = newMapSprite(map, editor.getSpriteTool(), coords.x, coords.y);

                    ((SpriteLayer) map.selectedLayer).tiles.add(mapSprite);
                    createOrRemoveSprite.addSprites();
                    map.performAction(createOrRemoveSprite);
                    PropertyToolPane.updateLightsAndBlocked(map);
                }
            }
        }
        else if(map.selectedLayer instanceof ObjectLayer)
        {
            if(editor.getFileTool() != null)
            {
                if(editor.getFileTool().tool == Tools.SELECT)
                {
                    for (int i = map.selectedLayer.tiles.size - 1; i >= 0; i--)
                    {
                        MapObject mapObject = ((MapObject) map.selectedLayer.tiles.get(i));
                        if (mapObject.isHoveredOver(coords.x, coords.y))
                        {
                            SelectObject selectObject = new SelectObject(map, map.selectedObjects);
                            if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
                            {
                                if(map.selectedObjects.contains(mapObject, true))
                                {
                                    map.selectedObjects.removeValue(mapObject, true);
                                    mapObject.unselect();
                                }
                                else
                                {
                                    map.selectedObjects.add(mapObject);
                                    mapObject.select();
                                }
                                map.propertyMenu.rebuild();
                            }
                            else
                            {
                                for (int k = 0; k < map.selectedObjects.size; k++)
                                    map.selectedObjects.get(k).unselect();
                                map.selectedObjects.clear();
                                map.selectedObjects.add(mapObject);
                                mapObject.select();
                                map.propertyMenu.rebuild();
                            }
                            selectObject.addSelected();
                            map.performAction(selectObject);
                            break;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button)
    {
        if(map.boxSelect.isDragging && map.selectedLayer != null)
        {
            if(map.selectedLayer instanceof SpriteLayer)
            {
                SelectSprite selectSprite = new SelectSprite(map, map.selectedSprites);
                if (!Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
                {
                    for (int k = 0; k < map.selectedSprites.size; k++)
                        map.selectedSprites.get(k).unselect();
                    map.selectedSprites.clear();
                }
                for (int i = 0; i < map.selectedLayer.tiles.size; i++)
                {
                    MapSprite mapSprite = ((MapSprite) map.selectedLayer.tiles.get(i));
                    if (Intersector.overlapConvexPolygons(mapSprite.polygon.getTransformedVertices(), map.boxSelect.getVertices(), null))
                    {
                        boolean selected = map.selectedSprites.contains(mapSprite, true);
                        if (!selected)
                        {
                            map.selectedSprites.add(mapSprite);
                            mapSprite.select();
                            map.propertyMenu.spritePropertyPanel.setVisible(true);
                        }
                    }
                }
                selectSprite.addSelected();
                map.performAction(selectSprite);
            }
            else if(map.selectedLayer instanceof ObjectLayer || (map.selectedSprites.size == 1 && map.selectedSprites.first().tool.mapObjects.size > 1))
            {
                SelectObject selectObject = new SelectObject(map, map.selectedObjects);
                if (!Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
                {
                    for (int k = 0; k < map.selectedObjects.size; k++)
                        map.selectedObjects.get(k).unselect();
                    map.selectedObjects.clear();
                }
                for (int i = 0; i < map.selectedLayer.tiles.size; i++)
                {
                    MapObject mapObject = ((MapObject) map.selectedLayer.tiles.get(i));
                    if (Intersector.overlapConvexPolygons(mapObject.polygon.getTransformedVertices(), map.boxSelect.getVertices(), null))
                    {
                        boolean selected = map.selectedObjects.contains(mapObject, true);
                        if (!selected)
                        {
                            map.selectedObjects.add(mapObject);
                            mapObject.select();
                        }
                    }
                }
                selectObject.addSelected();
                map.performAction(selectObject);
            }
            map.propertyMenu.rebuild();
            map.boxSelect.isDragging = false;
        }
        else if(draggingMoveBox && editor.getFileTool() != null && editor.getFileTool().tool == Tools.OBJECTVERTICESELECT && map.selectedObjects.size == 1 && map.undo.size() > 0 && map.undo.peek() instanceof MoveVertice)
        {
            MoveVertice moveVertice = (MoveVertice) map.undo.pop();
            moveVertice.addNewVertices(map.selectedObjects.first().getVerticeX(), map.selectedObjects.first().getVerticeY());
            map.undo.push(moveVertice);
        }
        else if(draggingMoveBox && editor.getFileTool() != null && editor.getFileTool().tool == Tools.SELECT && map.selectedObjects.size > 0)
        {
            if(map.selectedObjects.first() instanceof AttachedMapObject)
            {
                if(map.undo.size() > 0 && map.undo.peek() instanceof MoveAttachedObject)
                {
                    MoveAttachedObject moveObject = (MoveAttachedObject) map.undo.pop();
                    moveObject.addNewPosition();
                    map.undo.push(moveObject);
                }
            }
            else if(map.selectedObjects.first() instanceof MapObject)
            {
                if(map.undo.size() > 0 && map.undo.peek() instanceof MoveObject)
                {
                    MoveObject moveObject = (MoveObject) map.undo.pop();
                    moveObject.addNewPosition();
                    map.undo.push(moveObject);
                }
            }
        }
        if(map.undo.size() > 0 && map.undo.peek() instanceof MoveSprite)
        {
            MoveSprite moveSprite = (MoveSprite) map.undo.pop();
            moveSprite.addNewPosition();
            map.undo.push(moveSprite);
        }
        else if(map.undo.size() > 0 && map.undo.peek() instanceof RotateSprite)
        {
            RotateSprite rotateSprite = (RotateSprite) map.undo.pop();
            rotateSprite.addNewRotation();
            map.undo.push(rotateSprite);
        }
        if(map.undo.size() > 0 && map.undo.peek() instanceof PlaceTile)
        {
            PlaceTile placeTile = (PlaceTile) map.undo.pop();
            if(placeTile.changed())
                map.undo.push(placeTile);
        }
        this.draggingRotateBox = false;
        this.draggingMoveBox = false;
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer)
    {
        editor.stage.unfocus(map.tileMenu.tileScrollPane);
        editor.stage.unfocus(map.tileMenu.spriteScrollPane);
        Vector3 coords = Utils.unproject(map.camera, screenX, screenY);
        if(editor.getFileTool() != null && editor.getFileTool().tool == Tools.BOXSELECT)
        {
            map.boxSelect.continueDrag(coords.x, coords.y);
            return false;
        }
        this.pos = coords.cpy().sub(dragOrigin.x, dragOrigin.y, 0);
        if(draggingRotateBox)
        {
            Vector2 pos2 = new Vector2(this.pos.x, this.pos.y);
            for(int i = 0; i < map.selectedSprites.size; i ++)
                map.selectedSprites.get(i).rotate(dragOrigin.angle(pos2) / 35f);
            return false;
        }
        else if(draggingMoveBox)
        {
            for(int i = 0; i < map.selectedSprites.size; i ++)
            {
                if(!this.oldXofDragMap.containsKey(map.selectedSprites.get(i)))
                    break;
                map.selectedSprites.get(i).setPosition(this.oldXofDragMap.get(map.selectedSprites.get(i)) + pos.x, this.oldYofDragMap.get(map.selectedSprites.get(i)) + pos.y);
            }
            if(map.selectedObjects.size == 1 && map.selectedObjects.first().indexOfSelectedVertice != -1 && editor.getFileTool() != null && editor.getFileTool().tool == Tools.OBJECTVERTICESELECT)
            {
                MapObject mapObject = map.selectedObjects.first();
                mapObject.moveVertice(coords.x, coords.y);
            }
            else
            {
                for (int i = 0; i < map.selectedObjects.size; i++)
                {
                    if(!this.oldXofDragMap.containsKey(map.selectedObjects.get(i)))
                        break;
                    if(map.selectedObjects.get(i) instanceof AttachedMapObject)
                        ((AttachedMapObject)map.selectedObjects.get(i)).positionOffset.set(this.oldXofDragMap.get(map.selectedObjects.get(i)) + pos.x, this.oldYofDragMap.get(map.selectedObjects.get(i)) + pos.y);
                    else
                        map.selectedObjects.get(i).setPosition(this.oldXofDragMap.get(map.selectedObjects.get(i)) + pos.x, this.oldYofDragMap.get(map.selectedObjects.get(i)) + pos.y);
                }
            }
            return false;
        }
        if(editor.getFileTool() != null && editor.getFileTool().tool == Tools.GRAB)
        {
            map.camera.position.x -= this.pos.x / 15f;
            map.camera.position.y -= this.pos.y / 15f;
            map.camera.update();
        }
        if(map.selectedLayer instanceof TileLayer && editor.getTileTools() != null)
        {
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
                        randomTile = editor.getTileTools().get(i);
                        break;
                    }
                }
                if(clickedTile != null && randomTile != null && editor.getFileTool().tool == Tools.BRUSH)
                {
                    if(clickedTile.tool != randomTile)
                    {
                        PlaceTile placeTile = (PlaceTile) map.undo.pop();
                        placeTile.addTile(clickedTile, clickedTile.tool, randomTile);
                        map.undo.push(placeTile);
                    }
                    clickedTile.setTool(randomTile);
                    map.findAllTilesToBeGrouped();
                }
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
                        {
                            if(clickedTile.tool != editor.getTileTools().get(i) && map.undo.size() > 0 && map.undo.peek() instanceof PlaceTile)
                            {
                                PlaceTile placeTile = (PlaceTile) map.undo.pop();
                                placeTile.addTile(clickedTile, clickedTile.tool, editor.getTileTools().get(i));
                                map.undo.push(placeTile);
                            }
                            clickedTile.setTool(editor.getTileTools().get(i));
                        }
                        else if (editor.getFileTool().tool == Tools.ERASER)
                        {
                            if(clickedTile.tool != null)
                            {
                                PlaceTile placeTile = (PlaceTile) map.undo.pop();
                                placeTile.addTile(clickedTile, clickedTile.tool, null);
                                map.undo.push(placeTile);
                            }
                            clickedTile.setTool(null);
                        }
                        map.findAllTilesToBeGrouped();
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
        if(map.selectedLayer instanceof TileLayer && editor.getTileTools() != null)
        {
            for(int i = 0; i < editor.getTileTools().size; i ++)
            {
                int xOffset = editor.getTileTools().first().x - editor.getTileTools().get(i).x;
                int yOffset = editor.getTileTools().first().y - editor.getTileTools().get(i).y;
                Tile hoverTile = map.getTile(coords.x + xOffset, coords.y + yOffset - tileSize);

                if (editor.getFileTool() != null && hoverTile != null && (editor.getFileTool().tool == Tools.BRUSH || editor.getFileTool().tool == Tools.BIND))
                    editor.getTileTools().get(i).previewSprite.setPosition(hoverTile.position.x, hoverTile.position.y);
            }
        }
        else if(map.selectedLayer instanceof SpriteLayer && editor.getSpriteTool() != null)
        {
            if (editor.getFileTool() != null && editor.getFileTool().tool == Tools.BRUSH)
                editor.getSpriteTool().previewSprite.setPosition(coords.x - editor.getSpriteTool().previewSprite.getWidth() / 2, coords.y - editor.getSpriteTool().previewSprite.getHeight() / 2);
        }
        if(editor.getFileTool() != null && map.selectedObjects.size == 1 && editor.getFileTool().tool == Tools.OBJECTVERTICESELECT)
        {
            if(!map.selectedObjects.first().isPoint)
            {
                for (int i = 0; i < map.selectedObjects.first().polygon.getTransformedVertices().length; i += 2)
                {
                    double distance = Math.sqrt(Math.pow((coords.x - map.selectedObjects.first().polygon.getTransformedVertices()[i]), 2) + Math.pow((coords.y - map.selectedObjects.first().polygon.getTransformedVertices()[i + 1]), 2));
                    if (distance <= 15)
                    {
                        map.selectedObjects.first().indexOfHoveredVertice = i;
                        break;
                    } else
                        map.selectedObjects.first().indexOfHoveredVertice = -1;
                }
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
            this.map.zoom += amount / 3f;
            if(this.map.zoom < .1f)
                this.map.zoom = .1f;
        }

        return false;
    }

    private TileTool randomTile()
    {
        // Randomly pick a tile from the selected tiles based on weighted probabilities
        TileTool randomTile = null;
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
                randomTile = editor.getTileTools().get(i);
                break;
            }
        }
        return randomTile;
    }

    private void fill(float x, float y, TileTool tool)
    {
        Tile tileToPaint = map.getTile(x, y - tileSize);
        if(tileToPaint != null && tileToPaint.tool == tool && !tileToPaint.hasBeenPainted)
        {
            TileTool tile;
            if(editor.fileMenu.toolPane.random.selected)
                tile = randomTile();
            else
                tile = editor.getTileTools().first();
            if(tile != null)
            {
                tileToPaint.hasBeenPainted = true;
                PlaceTile placeTile = (PlaceTile) map.undo.pop();
                placeTile.addTile(tileToPaint, tileToPaint.tool, tile);
                map.undo.push(placeTile);
                tileToPaint.setTool(tile);
                fill(x + 64, y, tool);
                fill(x - 64, y, tool);
                fill(x, y + 64, tool);
                fill(x, y - 64, tool);
            }
        }
    }

    public static MapSprite newMapSprite(TileMap map, TileTool tileTool, float x, float y)
    {
        MapSprite mapSprite = new MapSprite(map, tileTool,
                x - tileTool.textureRegion.getRegionWidth() / 2, y - tileTool.textureRegion.getRegionHeight() / 2);

        TextField.TextFieldFilter valueFilter = new TextField.TextFieldFilter()
        {
            @Override
            public boolean acceptChar(TextField textField, char c)
            {
                return c == '.' || c == '-' || Character.isDigit(c);
            }
        };
        PropertyField rotationField = new PropertyField("Rotation", "0", GameAssets.getUISkin(), map.propertyMenu, false);
        rotationField.value.setTextFieldFilter(valueFilter);
        rotationField.value.getListeners().clear();
        TextField.TextFieldClickListener rotationListener = rotationField.value.new TextFieldClickListener(){
            @Override
            public boolean keyUp (InputEvent event, int keycode)
            {
                try
                {
                    if (keycode == Input.Keys.ENTER)
                    {
                        for(int i = 0; i < map.selectedSprites.size; i ++)
                            map.selectedSprites.get(i).setRotation(Float.parseFloat(rotationField.value.getText()));
                    }
                }
                catch (NumberFormatException e){}
                return true;
            }
        };
        rotationField.value.addListener(rotationListener);

        PropertyField zField = new PropertyField("Z", "0", GameAssets.getUISkin(), map.propertyMenu, false);
        zField.value.setTextFieldFilter(valueFilter);
        zField.value.getListeners().clear();
        TextField.TextFieldClickListener zListener = zField.value.new TextFieldClickListener(){
            @Override
            public boolean keyUp (InputEvent event, int keycode)
            {
                try
                {
                    if (keycode == Input.Keys.ENTER)
                    {
                        for(int i = 0; i < map.selectedSprites.size; i ++)
                            map.selectedSprites.get(i).setZ(Float.parseFloat(zField.value.getText()));
                    }
                }
                catch (NumberFormatException e){}
                return true;
            }
        };
        zField.value.addListener(zListener);


        mapSprite.lockedProperties.add(rotationField);
        mapSprite.lockedProperties.add(zField);
        return mapSprite;
    }
}
