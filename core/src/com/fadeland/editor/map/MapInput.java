package com.fadeland.editor.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.ObjectMap;
import com.fadeland.editor.FadelandEditor;
import com.fadeland.editor.GameAssets;
import com.fadeland.editor.Utils;
import com.fadeland.editor.ui.fileMenu.Tools;
import com.fadeland.editor.ui.propertyMenu.PropertyField;
import com.fadeland.editor.ui.tileMenu.TileTool;

import static com.fadeland.editor.ui.tileMenu.TileMenu.tileSize;

public class MapInput implements InputProcessor
{
    private FadelandEditor editor;
    private TileMap map;

    private Vector2 dragOrigin;
    private Vector3 pos;
    private ObjectMap<MapSprite, Float> oldXofDragMap;
    private ObjectMap<MapSprite, Float> oldYofDragMap;

    private boolean draggingRotateBox = false;
    private boolean draggingMoveBox = false;

    public MapInput(FadelandEditor editor, TileMap map)
    {
        this.editor = editor;
        this.map = map;
        this.dragOrigin = new Vector2();
        this.pos = new Vector3();
        this.oldXofDragMap = new ObjectMap<>();
        this.oldYofDragMap = new ObjectMap<>();
    }

    @Override
    public boolean keyDown(int keycode)
    {
        if(keycode == Input.Keys.FORWARD_DEL)
        {
            if(map.selectedLayer != null)
            {
                map.selectedLayer.tiles.removeAll(map.selectedSprites, true);
                map.selectedSprites.clear();
                map.propertyMenu.rebuild();
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
        if(editor.getFileTool() != null && editor.getFileTool().tool == Tools.BOXSELECT && map.selectedLayer instanceof SpriteLayer)
        {
            map.boxSelect.startDrag(coords.x, coords.y);
            return false;
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
        if(map.selectedLayer instanceof TileLayer && editor.getTileTools() != null)
        {
            if(editor.getTileTools().size > 0 && editor.getTileTools().first() instanceof TileTool && editor.getFileTool() != null && editor.getFileTool().tool == Tools.FILL)
            {
                for(int i = 0; i < map.selectedLayer.tiles.size; i ++)
                    map.selectedLayer.tiles.get(i).hasBeenPainted = false;
                Tile clickedTile = map.getTile(coords.x, coords.y - tileSize);
                if(clickedTile != null)
                    fill(coords.x, coords.y, clickedTile.tool);
            }
            else if(editor.getTileTools().size > 1 && editor.getTileTools().first() instanceof TileTool && editor.getFileTool() != null && editor.fileMenu.toolPane.random.selected)
            {
                // Randomly pick a tile from the selected tiles based on weighted probabilities
                Tile clickedTile = map.getTile(coords.x, coords.y - tileSize);
                TileTool randomTile = randomTile();
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
            if(editor.getFileTool() != null)
            {
                if(editor.getFileTool().tool == Tools.SELECT)
                {
                    this.editor.shapeRenderer.setColor(Color.YELLOW);
                    for (int i = map.selectedLayer.tiles.size - 1; i >= 0; i--)
                    {
                        MapSprite mapSprite = ((MapSprite) map.selectedLayer.tiles.get(i));
                        if (mapSprite.polygon.contains(coords.x, coords.y))
                        {
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
                            break;
                        }
                    }
                }
                else if(editor.getSpriteTool() != null &&
                        coords.x > 0 && coords.y > 0 && coords.x < map.mapWidth * tileSize && coords.y < map.mapHeight * tileSize)
                {
                    MapSprite mapSprite = new MapSprite(map, (SpriteLayer) map.selectedLayer, editor.getSpriteTool(),
                            coords.x - editor.getSpriteTool().textureRegion.getRegionWidth() / 2, coords.y - editor.getSpriteTool().textureRegion.getRegionHeight() / 2);

                    PropertyField propertyField = new PropertyField("Rotation", "0", GameAssets.getUISkin(), map.propertyMenu, false);
                    propertyField.value.setTextFieldFilter(new TextField.TextFieldFilter()
                    {
                        @Override
                        public boolean acceptChar(TextField textField, char c)
                        {
                            return c == '.' || c == '-' || Character.isDigit(c);
                        }
                    });

                    propertyField.value.getListeners().clear();

                    TextField.TextFieldClickListener rotationListener = propertyField.value.new TextFieldClickListener(){
                        @Override
                        public boolean keyUp (InputEvent event, int keycode)
                        {
                            try
                            {
                                if (keycode == Input.Keys.ENTER)
                                {
                                    for(int i = 0; i < map.selectedSprites.size; i ++)
                                        map.selectedSprites.get(i).setRotation(Float.parseFloat(propertyField.value.getText()));
                                }
                            }
                            catch (NumberFormatException e){}
                            return true;
                        }
                    };
                    propertyField.value.addListener(rotationListener);

                    mapSprite.lockedProperties.add(propertyField);

                    if (editor.getFileTool().tool == Tools.BRUSH)
                        ((SpriteLayer) map.selectedLayer).tiles.add(mapSprite);
//                else if (editor.getFileTool().tool == Tools.ERASER)
//                    clickedTile.setTool(null);
                }
            }
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button)
    {
        this.draggingRotateBox = false;
        this.draggingMoveBox = false;
        if(map.boxSelect.isDragging && map.selectedLayer != null && map.selectedLayer instanceof SpriteLayer)
        {
            if(!Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
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
            map.propertyMenu.rebuild();
            map.boxSelect.isDragging = false;
        }
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
                map.selectedSprites.get(i).setPosition(this.oldXofDragMap.get(map.selectedSprites.get(i)) + pos.x, this.oldYofDragMap.get(map.selectedSprites.get(i)) + pos.y);
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
        if(map.selectedLayer instanceof TileLayer && editor.getTileTools() != null)
        {
            for(int i = 0; i < editor.getTileTools().size; i ++)
            {
                int xOffset = editor.getTileTools().first().x - editor.getTileTools().get(i).x;
                int yOffset = editor.getTileTools().first().y - editor.getTileTools().get(i).y;
                Tile hoverTile = map.getTile(coords.x + xOffset, coords.y + yOffset - tileSize);

                if (editor.getFileTool() != null && hoverTile != null && editor.getFileTool().tool == Tools.BRUSH)
                    editor.getTileTools().get(i).previewSprite.setPosition(hoverTile.position.x, hoverTile.position.y);
            }
        }
        else if(map.selectedLayer instanceof SpriteLayer && editor.getSpriteTool() != null)
        {
            if (editor.getFileTool() != null && editor.getFileTool().tool == Tools.BRUSH)
                editor.getSpriteTool().previewSprite.setPosition(coords.x - editor.getSpriteTool().previewSprite.getWidth() / 2, coords.y - editor.getSpriteTool().previewSprite.getHeight() / 2);
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
                tileToPaint.setTool(tile);
                fill(x + 64, y, tool);
                fill(x - 64, y, tool);
                fill(x, y + 64, tool);
                fill(x, y - 64, tool);
            }
        }
    }
}
