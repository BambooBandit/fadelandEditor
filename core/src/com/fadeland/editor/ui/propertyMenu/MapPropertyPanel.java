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

    private Label mapWidthProperty;
    private TextField mapWidthValue;
    private Label mapHeightProperty;
    private TextField mapHeightValue;

    public MapPropertyPanel(Skin skin, FadelandEditor fadelandEditor)
    {
        this.editor = fadelandEditor;

        this.background = new Image(GameAssets.getUIAtlas().createPatch("load-background"));
        this.stack = new Stack();
        this.table = new Table();
        this.table.left().top();

        this.mapWidthProperty = new Label("Map Width", skin);
        this.mapWidthValue = new TextField(Integer.toString(mapWidth), skin);
        this.mapHeightProperty = new Label("Map Height", skin);
        this.mapHeightValue = new TextField(Integer.toString(mapHeight), skin);

        this.table.add(this.mapWidthProperty);
        this.table.add(this.mapWidthValue).row();
        this.table.add(this.mapHeightProperty);
        this.table.add(this.mapHeightValue).row();

        this.stack.add(this.background);
        this.stack.add(this.table);

        this.addActor(this.stack);
    }

    @Override
    public void setSize(float width, float height)
    {
        for(int i = 0; i < this.table.getChildren().size; i ++)
            this.table.getCell(this.table.getChildren().get(i)).size(width / 2, textFieldHeight);

        float newHeight = textFieldHeight * this.table.getChildren().size / 2;

        this.background.setBounds(0, 0, width, newHeight);
        this.stack.setSize(width, newHeight);
        this.stack.invalidateHierarchy();

        super.setSize(width, newHeight);
    }
}