package com.fadeland.editor.ui.propertyMenu;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.fadeland.editor.FadelandEditor;
import com.fadeland.editor.GameAssets;

public class MapPropertyPanel extends Group
{
    public static int textFieldHeight = 32;

    public static int mapWidth = 5;
    public static int mapHeight = 5;

    private FadelandEditor editor;

    private Image background;
    private Stack stack;
    public Table table; // Holds all the text fields

    private PropertyField mapWidthProperty;
    private PropertyField mapHeightProperty;

    public MapPropertyPanel(Skin skin, FadelandEditor fadelandEditor)
    {
        this.editor = fadelandEditor;

        this.background = new Image(GameAssets.getUIAtlas().createPatch("load-background"));
        this.stack = new Stack();
        this.table = new Table();
        this.table.left().top();

        this.mapWidthProperty = new PropertyField("Map Width", Integer.toString(mapWidth), skin);
        this.mapHeightProperty = new PropertyField("Map Height", Integer.toString(mapHeight), skin);

        this.table.add(this.mapWidthProperty).padBottom(1).row();
        this.table.add(this.mapHeightProperty).row();

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

        float newHeight = textFieldHeight * this.table.getChildren().size;

        this.background.setBounds(0, 0, width, newHeight);
        this.stack.setSize(width, newHeight);
        this.stack.invalidateHierarchy();

        super.setSize(width, newHeight);
    }
}