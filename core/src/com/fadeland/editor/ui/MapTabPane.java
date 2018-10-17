package com.fadeland.editor.ui;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ObjectMap;
import com.fadeland.editor.FadelandEditor;
import com.fadeland.editor.GameAssets;
import com.fadeland.editor.map.TileMap;

public class MapTabPane extends Group
{
    private Stack pane;
    private Table buttonTable;
    private Image background;
    private Skin skin;
    private ObjectMap<TileMap, TextButton> mapsToButtons;
    private FadelandEditor editor;
    private ButtonGroup<TextButton> buttonGroup;

    public MapTabPane(FadelandEditor editor, Skin skin)
    {
        this.buttonTable = new Table();
        this.mapsToButtons = new ObjectMap<>();
        this.buttonGroup = new ButtonGroup<>();

        this.editor = editor;
        this.skin = skin;
        this.pane = new Stack();

        this.background = new Image(GameAssets.getUIAtlas().createPatch("load-background"));
        this.pane.add(this.background);
        this.buttonTable.left();
        this.pane.add(this.buttonTable);

        this.buttonTable.debug();

        this.addActor(this.pane);
    }

    @Override
    public void setSize(float width, float height)
    {
        this.pane.setSize(width, height);
        this.background.setBounds(0, 0, width, height);

        // Resize all buttons in the pane
        ObjectMap.Entries<TileMap, TextButton> mapIterator = this.mapsToButtons.iterator();
        TextButton button;
        while(mapIterator.hasNext)
        {
            button = mapIterator.next().value;
            this.buttonTable.getCell(button).size(150, height);
        }
        this.buttonTable.invalidateHierarchy();

        this.pane.invalidateHierarchy();

        super.setSize(width, height);
    }

    public void addMap(final TileMap map)
    {
        editor.maps.add(map);

        TextButton mapButton = new TextButton(map.getName(), skin, "checked");

        // For closing out of the map in the pane
        TextButton closeButton = new TextButton("X", skin);
        closeButton.setSize(25, getHeight());
        closeButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                removeMap(map);
            }
        });
        mapButton.addActor(closeButton);
        this.mapsToButtons.put(map, mapButton);

        this.buttonGroup.add(mapButton);

        this.buttonTable.add(mapButton).left();

        this.setSize(getWidth(), getHeight()); // Fit the buttons
    }

    public void removeMap(TileMap map)
    {
        TextButton mapButton = this.mapsToButtons.get(map); // Button to remove

        this.buttonGroup.remove(mapButton);
        this.buttonTable.removeActor(mapButton);

        // Move the tabs to the left by rebuilding the pane
        this.buttonTable.clearChildren();
        ObjectMap.Entries<TileMap, TextButton> mapIterator = this.mapsToButtons.iterator();
        TextButton button;
        while(mapIterator.hasNext)
        {
            button = mapIterator.next().value;
            // Don't remove from the map til after this to preserve the location of the tabs. Otherwise things get placed randomly
            if(button == mapButton)
                continue;
            this.buttonTable.add(button);
        }

        this.mapsToButtons.remove(map); // Explained above


        this.setSize(getWidth(), getHeight()); // Fit the buttons
    }
}
