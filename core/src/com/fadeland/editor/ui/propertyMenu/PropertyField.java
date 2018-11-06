package com.fadeland.editor.ui.propertyMenu;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.fadeland.editor.TextFieldAction;
import com.fadeland.editor.map.TileMap;

public class PropertyField extends Group
{
    private Label property; // Null if removeable is true
    public TextField propertyTextField; // Null if removeable is false
    public TextField value;
    private TextButton remove; // Null if removeable is false
    private Table table;
    private boolean removeable;

    private PropertyMenu menu;

    private static Array<TextFieldAction> textFieldActions = new Array<>();

    public PropertyField(String property, String value, Skin skin, final PropertyMenu menu, boolean removeable)
    {
        this.menu = menu;

        this.removeable = removeable;

        if(removeable)
            this.propertyTextField = new TextField(property, skin);
        else
            this.property = new Label(property, skin);
        this.value = new TextField(value, skin);

        this.table = new Table();
        this.table.bottom().left();
        if(removeable)
            this.table.add(this.propertyTextField);
        else
            this.table.add(this.property);
        this.table.add(this.value);

        if(removeable)
        {
            this.remove = new TextButton("X", skin);
            final PropertyField removeableField = this;
            this.remove.addListener(new ClickListener()
            {
                @Override
                public void clicked(InputEvent event, float x, float y)
                {
                    menu.removeProperty(removeableField);
                }
            });

            this.table.add(this.remove);

            addListener();
        }


        addActor(this.table);
    }

    @Override
    public void setSize(float width, float height)
    {
        this.value.setSize(width / 2, height);
        if(this.removeable)
        {
            this.propertyTextField.setSize(width / 2, height);
            this.table.getCell(this.propertyTextField).size(width / 2, height);
            this.table.getCell(this.value).size((width / 2) - height, height);
        }
        else
        {
            this.property.setSize(width / 2, height);
            this.table.getCell(this.property).size(width / 2, height);
            this.table.getCell(this.value).size(width / 2, height);
        }
        if(this.removeable)
            this.table.getCell(this.remove).size(height, height);
        this.table.invalidateHierarchy();
        super.setSize(width, height);
    }

    public String getProperty()
    {
        if(this.removeable)
            return this.propertyTextField.getText();
        else
            return this.property.getText().toString();
    }
    public String getValue()
    {
        return this.value.getText();
    }

    @Override
    public boolean equals(Object o)
    {
        if(o instanceof PropertyField)
        {
            PropertyField toCompare = (PropertyField) o;
            return this.propertyTextField.getText().equals(toCompare.propertyTextField.getText()) && this.value.getText().equals(toCompare.value.getText());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.propertyTextField.getText().hashCode() + this.value.getText().hashCode();
    }

    private void addListener()
    {
        this.propertyTextField.getListeners().clear();
        this.value.getListeners().clear();

        final TileMap map = menu.map;

        final PropertyField property = this;

        TextField.TextFieldClickListener propertyClickListener = propertyTextField.new TextFieldClickListener(){
            @Override
            public boolean keyTyped (InputEvent event, char character)
            {
                for(int i = 0; i < map.tileMenu.selectedTiles.size; i ++)
                {
                    if(map.tileMenu.selectedTiles.get(i).properties.contains(property, true))
                        continue;
                    PropertyField propertyField = null;
                    if(map.tileMenu.selectedTiles.get(i).properties.contains(property, false))
                        propertyField = map.tileMenu.selectedTiles.get(i).properties.get(map.tileMenu.selectedTiles.get(i).properties.indexOf(property, false));
                    if(propertyField != null)
                    {
                        final PropertyField finalPropertyField = propertyField;
                        textFieldActions.add(() -> {finalPropertyField.propertyTextField.setText(property.propertyTextField.getText());} );
                    }
                }
                for(int i = 0; i < map.selectedObjects.size; i ++)
                {
                    if(map.selectedObjects.get(i).properties.contains(property, true))
                        continue;
                    PropertyField propertyField = null;
                    if(map.selectedObjects.get(i).properties.contains(property, false))
                        propertyField = map.selectedObjects.get(i).properties.get(map.selectedObjects.get(i).properties.indexOf(property, false));
                    if(propertyField != null)
                    {
                        final PropertyField finalPropertyField = propertyField;
                        textFieldActions.add(() -> {finalPropertyField.propertyTextField.setText(property.propertyTextField.getText());} );
                    }
                }
                super.keyTyped(event, character);
                for(int i = 0; i < textFieldActions.size; i ++)
                    textFieldActions.get(i).action();
                textFieldActions.clear();
                return false;
            }
        };
        this.propertyTextField.addListener(propertyClickListener);

        TextField.TextFieldClickListener valueClickListener = value.new TextFieldClickListener(){
            @Override
            public boolean keyTyped (InputEvent event, char character)
            {
                for(int i = 0; i < map.tileMenu.selectedTiles.size; i ++)
                {
                    if(map.tileMenu.selectedTiles.get(i).properties.contains(property, true))
                        continue;
                    PropertyField propertyField = null;
                    if(map.tileMenu.selectedTiles.get(i).properties.contains(property, false))
                        propertyField = map.tileMenu.selectedTiles.get(i).properties.get(map.tileMenu.selectedTiles.get(i).properties.indexOf(property, false));
                    if(propertyField != null)
                    {
                        final PropertyField finalPropertyField = propertyField;
                        textFieldActions.add(() -> {finalPropertyField.value.setText(property.value.getText());} );
                    }
                }
                for(int i = 0; i < map.selectedObjects.size; i ++)
                {
                    if(map.selectedObjects.get(i).properties.contains(property, true))
                        continue;
                    PropertyField propertyField = null;
                    if(map.selectedObjects.get(i).properties.contains(property, false))
                        propertyField = map.selectedObjects.get(i).properties.get(map.selectedObjects.get(i).properties.indexOf(property, false));
                    if(propertyField != null)
                    {
                        final PropertyField finalPropertyField = propertyField;
                        textFieldActions.add(() -> {finalPropertyField.value.setText(property.value.getText());} );
                    }
                }
                super.keyTyped(event, character);
                for(int i = 0; i < textFieldActions.size; i ++)
                    textFieldActions.get(i).action();
                textFieldActions.clear();
                return false;
            }
        };
        this.value.addListener(valueClickListener);
    }
}
