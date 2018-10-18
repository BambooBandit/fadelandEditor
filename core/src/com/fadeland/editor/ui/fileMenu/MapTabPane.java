package com.fadeland.editor.ui.fileMenu;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ObjectMap;
import com.fadeland.editor.FadelandEditor;
import com.fadeland.editor.GameAssets;
import com.fadeland.editor.map.TileMap;

/** Handles switching views of maps via tabs, adding and removing tabs.*/
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
        mapButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                // Clicking this tab sets the active map and screen to that map
                lookAtMap(map);
            }
        });

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

        // Removed map that we are looking at, so look at another map
        boolean changeFocus = false;
        if(this.buttonGroup.getChecked() == mapButton)
            changeFocus = true;

        this.buttonGroup.remove(mapButton);
        this.buttonTable.removeActor(mapButton);

        // Move the tabs to the left by rebuilding the pane
        this.buttonTable.clearChildren();
        ObjectMap.Entries<TileMap, TextButton> mapIterator = this.mapsToButtons.iterator();
        ObjectMap.Entry<TileMap, TextButton> button;
        boolean first = true;
        while(mapIterator.hasNext)
        {
            button = mapIterator.next();
            // Don't remove from the map til after this to preserve the location of the tabs. Otherwise things get placed randomly
            if(button.value == mapButton)
                continue;
            if(changeFocus && first)
            {
                first = false;
                changeFocus = false;
                lookAtMap(button.key);
            }
            this.buttonTable.add(button.value);
        }
        if(changeFocus) // Still true, meaning no more tabs are open, so just look at nothing.
            lookAtMap(null);
        this.mapsToButtons.remove(map); // Explained above

        this.setSize(getWidth(), getHeight()); // Fit the buttons
    }

    /** Makes the editor look at this map. */
    public void lookAtMap(TileMap map)
    {
        this.editor.activeMap = map;
        this.editor.setScreen(map);
        if(map != null)
            this.buttonGroup.setChecked(map.getName());
    }
}
