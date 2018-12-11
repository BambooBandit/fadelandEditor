package com.fadeland.editor.ui.propertyMenu;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.fadeland.editor.FadelandEditor;
import com.fadeland.editor.GameAssets;

public class MapPropertyPanel extends Group
{
    public static int textFieldHeight = 32;

    private FadelandEditor editor;
    private PropertyMenu menu;

    private Image background;
    private Stack stack;
    public Table table; // Holds all the text fields

    public PropertyField mapBrightnessProperty;

    public TextButton apply;

    public MapPropertyPanel(Skin skin, PropertyMenu menu, FadelandEditor fadelandEditor)
    {
        this.editor = fadelandEditor;
        this.menu = menu;

        this.background = new Image(GameAssets.getUIAtlas().createPatch("load-background"));
        this.stack = new Stack();
        this.table = new Table();
        this.table.left().top();

        this.mapBrightnessProperty = new PropertyField("Brightness", "1", skin, menu, false);
        this.mapBrightnessProperty.value.setTextFieldFilter(new TextField.TextFieldFilter()
        {
            @Override
            public boolean acceptChar(TextField textField, char c)
            {
                return c == '.' || Character.isDigit(c);
            }
        });

        this.apply = new TextButton("Apply", skin);
        this.apply.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                menu.map.rayHandler.setAmbientLight(Float.parseFloat(mapBrightnessProperty.value.getText()));
            }
        });

        this.table.add(this.mapBrightnessProperty).padBottom(1).row();
        this.table.add(this.apply).padBottom(1).row();

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

        float newHeight = textFieldHeight * 2;

        this.background.setBounds(0, 0, width, newHeight);
        this.stack.setSize(width, newHeight);
        this.stack.invalidateHierarchy();

        if(height == 0)
            super.setSize(0, 0);
        else
            super.setSize(width, newHeight);
    }
}