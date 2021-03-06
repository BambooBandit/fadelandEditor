package com.fadeland.editor.ui.propertyMenu;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.fadeland.editor.FadelandEditor;
import com.fadeland.editor.GameAssets;
import com.fadeland.editor.map.*;
import com.fadeland.editor.ui.tileMenu.TileTool;

import static com.fadeland.editor.ui.tileMenu.TileMenu.toolHeight;

public class PropertyToolPane extends Group
{
    private Stack pane;
    private Table toolTable;
    private Image background;
    private Skin skin;
    private FadelandEditor editor;

    private PropertyTool newProperty;
    private PropertyTool newLightProperty;
    private TextButton apply;

    public PropertyMenu menu;

    public PropertyToolPane(FadelandEditor editor, PropertyMenu menu, Skin skin)
    {
        this.menu = menu;
        this.toolTable = new Table();
        this.newProperty = new PropertyTool(PropertyTools.NEW, this, skin);
        this.newLightProperty = new PropertyTool(PropertyTools.NEWLIGHT, this, skin);
        this.apply = new TextButton("Apply", skin);
        setApplyListener();
        this.toolTable.left();
        this.toolTable.add(this.newProperty).padRight(1);
        this.toolTable.add(this.newLightProperty).padRight(1);
        this.toolTable.add(this.apply);

        this.editor = editor;
        this.skin = skin;
        this.pane = new Stack();

        this.background = new Image(GameAssets.getUIAtlas().createPatch("load-background"));
        this.pane.add(this.background);
        this.pane.add(this.toolTable);

        this.addActor(this.pane);
    }

    @Override
    public void setSize(float width, float height)
    {
        this.pane.setSize(width, height);
        this.background.setBounds(0, 0, width, height);

        // Resize all buttons in the pane
        this.toolTable.getCell(this.newProperty).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.newLightProperty).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.apply).size(toolHeight * 2, toolHeight);
        this.toolTable.invalidateHierarchy();

        this.pane.invalidateHierarchy();

        super.setSize(width, height);
    }

    public void setApplyListener()
    {
        this.apply.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                apply(menu.map);
            }
        });
    }

    public static void apply(TileMap map)
    {
        // map tiles
        for(int i = 0; i < map.tileMenu.tileTable.getChildren().size; i ++)
        {
            if(map.tileMenu.tileTable.getChildren().get(i) instanceof TileTool)
            {
                TileTool tileTool = (TileTool) map.tileMenu.tileTable.getChildren().get(i);
                // top
                PropertyField topProperty = tileTool.getPropertyField("top");
                if (topProperty == null)
                    tileTool.setTopSprites("");
                else
                {
                    String topValue = topProperty.value.getText();
                    tileTool.setTopSprites(topValue);
                }
            }
        }
        // map sprites
        for(int i = 0; i < map.tileMenu.spriteTable.getChildren().size; i ++)
        {
            if(map.tileMenu.spriteTable.getChildren().get(i) instanceof TileTool)
            {
                TileTool tileTool = (TileTool) map.tileMenu.spriteTable.getChildren().get(i);
                // top
                PropertyField topProperty = tileTool.getPropertyField("top");
                if (topProperty == null)
                    tileTool.setTopSprites("");
                else
                {
                    String topValue = topProperty.value.getText();
                    tileTool.setTopSprites(topValue);
                }
            }
        }

        // Set sprite color
        for(int i = 0; i < map.layers.size; i ++)
        {
            if(map.layers.get(i) instanceof SpriteLayer)
            {
                SpriteLayer spriteLayer = (SpriteLayer) map.layers.get(i);
                for(int k = 0; k < spriteLayer.tiles.size; k ++)
                {
                    MapSprite mapSprite = (MapSprite) spriteLayer.tiles.get(k);
                    for(int s = 0; s < mapSprite.lockedProperties.size; s++)
                    {
                        if(mapSprite.lockedProperties.get(s).rgba)
                        {
                            PropertyField colorProperty = mapSprite.lockedProperties.get(s);
                            mapSprite.setColor(colorProperty.getR(), colorProperty.getG(), colorProperty.getB(), colorProperty.getA());
                        }
                    }
                }
            }
        }

        if(map.editor.fileMenu.toolPane.perspective.selected)
            updatePerspective(map);
        updateLightsAndBlocked(map);
        map.searchForBlockedTiles();
    }

    public static void updatePerspective(TileMap map)
    {
        for(int i = 0; i < map.layers.size; i++)
        {
            Layer layer = map.layers.get(i);
            if(!(layer instanceof SpriteLayer))
                continue;
            for(int k = 0; k < layer.tiles.size; k ++)
            {
                MapSprite mapSprite = (MapSprite) layer.tiles.get(k);
                mapSprite.updatePerspectiveScale();
            }
        }
    }

    public static void updateLightsAndBlocked(TileMap map)
    {
        // map tiles
//        for(int i = 0; i < map.tileMenu.tileTable.getChildren().size; i ++)
//        {
//            TileTool tileTool = (TileTool) map.tileMenu.tileTable.getChildren().get(i);
//
//            // attached map objects
//            for(int k = 0; k < tileTool.mapObjects.size; k ++)
//            {
//                AttachedMapObject attachedMapObject = tileTool.mapObjects.get(k);
//                if(!attachedMapObject.isPoint)
//                {
//                    // blocked
//                    PropertyField blockedProperty = attachedMapObject.getPropertyField("blocked");
//                    if (blockedProperty == null)
//                    {
//                        attachedMapObject.removeBody();
//                        continue;
//                    }
//                    attachedMapObject.createBody();
//                }
//                else
//                {
//                    // light
//                    PropertyField lightProperty = attachedMapObject.getLightPropertyField();
//                    if (lightProperty == null)
//                    {
//                        attachedMapObject.removeLight();
//                        continue;
//                    }
//                    attachedMapObject.createLight();
//                }
//            }
//        }
//        // map sprites
//        for(int i = 0; i < map.tileMenu.spriteTable.getChildren().size; i ++)
//        {
//            TileTool tileTool = (TileTool) map.tileMenu.spriteTable.getChildren().get(i);
//
//            // attached map objects
//            for(int k = 0; k < tileTool.mapObjects.size; k ++)
//            {
//                AttachedMapObject attachedMapObject = tileTool.mapObjects.get(k);
//                if(!attachedMapObject.isPoint)
//                {
//                    // blocked
//                    PropertyField blockedProperty = attachedMapObject.getPropertyField("blocked");
//                    if (blockedProperty == null)
//                    {
//                        attachedMapObject.removeBody();
//                        continue;
//                    }
//                    attachedMapObject.createBody();
//                }
//                else
//                {
//                    // light
//                    PropertyField lightProperty = attachedMapObject.getLightPropertyField();
//                    if (lightProperty == null)
//                    {
//                        attachedMapObject.removeLight();
//                        continue;
//                    }
//                    attachedMapObject.createLight();
//                }
//            }
//        }

        for(int i = 0; i < map.layers.size; i ++)
        {
            if(map.layers.get(i) instanceof SpriteLayer || map.layers.get(i) instanceof TileLayer)
            {
                for (int k = 0; k < map.layers.get(i).tiles.size; k++)
                {
                    for(int m = 0; m < map.layers.get(i).tiles.get(k).drawableAttachedMapObjects.size; m ++)
                    {
                        AttachedMapObject attachedMapObject = map.layers.get(i).tiles.get(k).drawableAttachedMapObjects.get(m);
                        if(!attachedMapObject.isPoint)
                        {
                            // blocked
                            PropertyField blockedProperty = attachedMapObject.getPropertyField("blocked");
                            if (blockedProperty == null)
                            {
                                attachedMapObject.removeBody();
                                continue;
                            }
                            attachedMapObject.createBody();
                        }
                        else
                        {
                            // light
                            PropertyField lightProperty = attachedMapObject.getLightPropertyField();
                            if (lightProperty == null)
                            {
                                attachedMapObject.removeLight();
                                continue;
                            }
                            attachedMapObject.createLight();
                        }
                    }
                }
            }
        }
        // map objects
        for(int i = 0; i < map.layers.size; i ++)
        {
            if(map.layers.get(i) instanceof ObjectLayer)
            {
                for (int k = 0; k < map.layers.get(i).tiles.size; k++)
                {
                    MapObject mapObject = (MapObject) map.layers.get(i).tiles.get(k);
                    if(!mapObject.isPoint)
                    {
                        // blocked
                        PropertyField blockedProperty = mapObject.getPropertyField("blocked");
                        if (blockedProperty == null)
//                        {
                            mapObject.removeBody();
//                            continue;
//                        }
                        else
                        {
                            mapObject.removeBody();
                            mapObject.createBody();
                        }
                    }
                    else
                    {
                        // light
                        PropertyField lightProperty = mapObject.getLightPropertyField();
                        if (lightProperty == null)
//                        {
                            mapObject.removeLight();
//                            continue;
//                        }
                        else
                        {
                            mapObject.removeLight();
                            mapObject.createLight();
                        }
                    }
                }
            }
        }
    }
}
