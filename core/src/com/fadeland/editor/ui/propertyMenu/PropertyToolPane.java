package com.fadeland.editor.ui.propertyMenu;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.fadeland.editor.FadelandEditor;
import com.fadeland.editor.GameAssets;
import com.fadeland.editor.map.MapObject;
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
    private TextButton apply;

    public PropertyMenu menu;

    public PropertyToolPane(FadelandEditor editor, PropertyMenu menu, Skin skin)
    {
        this.menu = menu;
        this.toolTable = new Table();
        this.newProperty = new PropertyTool(PropertyTools.NEW, this, skin);
        this.apply = new TextButton("Apply", skin);
        setApplyListener();
        this.toolTable.left();
        this.toolTable.add(this.newProperty).padRight(1);
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
                // map tiles
                for(int i = 0; i < menu.map.tileMenu.tileTable.getChildren().size; i ++)
                {
                    TileTool tileTool = (TileTool) menu.map.tileMenu.tileTable.getChildren().get(i);
                    // top
                    PropertyField topProperty = tileTool.getPropertyField("top");
                    if(topProperty == null)
                    {
                        tileTool.setTopSprite("");
                        continue;
                    }
                    String topValue = topProperty.value.getText();
                    tileTool.setTopSprite(topValue);
                }
                // map sprites
                for(int i = 0; i < menu.map.tileMenu.spriteTable.getChildren().size; i ++)
                {
                    TileTool tileTool = (TileTool) menu.map.tileMenu.spriteTable.getChildren().get(i);
                    // top
                    PropertyField topProperty = tileTool.getPropertyField("top");
                    if(topProperty == null)
                    {
                        tileTool.setTopSprite("");
                        continue;
                    }
                    String topValue = topProperty.value.getText();
                    tileTool.setTopSprite(topValue);
                }
                // map objects
                for(int i = 0; i < menu.map.layers.size; i ++)
                {
                    for(int k = 0; k < menu.map.layers.get(i).tiles.size; k ++)
                    {
                        MapObject mapObject = (MapObject) menu.map.layers.get(i).tiles.get(k);
                        // blocked
                        PropertyField blockedProperty = mapObject.getPropertyField("blocked");
                        if(blockedProperty == null)
                        {
                            mapObject.removeBody();
                            continue;
                        }
                        mapObject.createBody();
                    }
                }
            }
        });
    }
}
