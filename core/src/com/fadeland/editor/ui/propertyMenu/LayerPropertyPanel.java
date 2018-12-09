package com.fadeland.editor.ui.propertyMenu;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.fadeland.editor.FadelandEditor;
import com.fadeland.editor.GameAssets;

import static com.fadeland.editor.map.TileMap.mapHeight;
import static com.fadeland.editor.map.TileMap.mapWidth;

public class LayerPropertyPanel extends Group
{
    public static int textFieldHeight = 32;

    private FadelandEditor editor;
    private PropertyMenu menu;

    private Image background;
    private Stack stack;
    public Table table; // Holds all the text fields

    public PropertyField layerWidthProperty;
    public PropertyField layerHeightProperty;

    private Table buttonDirectionTable;
    private ButtonGroup<TextButton> buttonDirectionUpDownGroup;
    private ButtonGroup<TextButton> buttonDirectionLeftRightGroup;
    public TextButton down, up, right, left, apply; // Dictates which direction the map grows

    public LayerPropertyPanel(Skin skin, PropertyMenu menu, FadelandEditor fadelandEditor)
    {
        this.editor = fadelandEditor;
        this.menu = menu;

        this.background = new Image(GameAssets.getUIAtlas().createPatch("load-background"));
        this.stack = new Stack();
        this.table = new Table();
        this.table.left().top();

        TextField.TextFieldFilter.DigitsOnlyFilter filter = new TextField.TextFieldFilter.DigitsOnlyFilter();

        this.layerWidthProperty = new PropertyField("Layer Width", Integer.toString(mapWidth), skin, menu, false);
        this.layerWidthProperty.value.setTextFieldFilter(filter);
        this.layerHeightProperty = new PropertyField("Layer Height", Integer.toString(mapHeight), skin, menu, false);
        this.layerHeightProperty.value.setTextFieldFilter(filter);

        this.buttonDirectionTable = new Table();
        this.buttonDirectionUpDownGroup = new ButtonGroup<>();
        this.buttonDirectionLeftRightGroup = new ButtonGroup<>();
        this.down = new TextButton("v", skin, "checked");
        this.up = new TextButton("^", skin, "checked");
        this.right = new TextButton(">", skin, "checked");
        this.left = new TextButton("<", skin, "checked");
        this.apply = new TextButton("Apply", skin);
        this.buttonDirectionUpDownGroup.add(down, up);
        this.buttonDirectionLeftRightGroup.add(right, left);
        this.buttonDirectionTable.add(down, up, right, left, apply);
        this.apply.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
//                ResizeMap resizeMap = new ResizeMap(menu.map, mapWidth, mapHeight);
                menu.map.selectedLayer.resize(Integer.parseInt(layerWidthProperty.value.getText()), Integer.parseInt(layerHeightProperty.value.getText()), down.isChecked(), right.isChecked());
//                resizeMap.addNew(mapWidth, mapHeight);
//                menu.map.performAction(resizeMap);
            }
        });

        this.table.add(this.layerWidthProperty).padBottom(1).row();
        this.table.add(this.layerHeightProperty).padBottom(1).row();
        this.table.add(this.buttonDirectionTable);

        this.stack.add(this.background);
        this.stack.add(this.table);

        this.addActor(this.stack);
    }

    @Override
    public void setSize(float width, float height)
    {
        for(int i = 0; i < this.table.getChildren().size; i ++)
        {
            this.table.getChildren().get(i).setSize(width, textFieldHeight);
            this.table.getCell(this.table.getChildren().get(i)).size(width, textFieldHeight);
        }
        for(int i = 0; i < this.buttonDirectionTable.getChildren().size; i ++)
        {
            this.buttonDirectionTable.getChildren().get(i).setSize(width / 7, textFieldHeight);
            this.buttonDirectionTable.getCell(this.buttonDirectionTable.getChildren().get(i)).size(width / 7, textFieldHeight);
        }
        this.buttonDirectionTable.getCell(this.apply).size(width - (width / 7) * 4, textFieldHeight);
        this.buttonDirectionTable.invalidateHierarchy();

        float newHeight = textFieldHeight * 3;

        this.background.setBounds(0, 0, width, newHeight);
        this.stack.setSize(width, newHeight);
        this.stack.invalidateHierarchy();

        if(height == 0)
            super.setSize(0, 0);
        else
            super.setSize(width, newHeight);
    }
}