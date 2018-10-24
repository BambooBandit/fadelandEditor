package com.fadeland.editor.ui.layerMenu;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.fadeland.editor.FadelandEditor;
import com.fadeland.editor.GameAssets;
import com.fadeland.editor.map.TileMap;
import com.fadeland.editor.ui.propertyMenu.PropertyField;

public class LayerMenu extends Group
{
    private FadelandEditor editor;
    private TileMap map;

    private Image background;

    private LayerToolPane toolPane;

    private ScrollPane scrollPane;

    public static int toolHeight = 35;

    private Stack stack;
    public Table table;

    private Skin skin;

    public Array<LayerField> layers;

    public LayerMenu(Skin skin, FadelandEditor fadelandEditor, TileMap map)
    {
        this.skin = skin;
        this.editor = fadelandEditor;
        this.map = map;

        this.layers = new Array<>();

        this.stack = new Stack();
        this.background = new Image(GameAssets.getUIAtlas().createPatch("load-background"));
        this.toolPane = new LayerToolPane(editor, this, skin);

        this.table = new Table();
        this.table.left().top();

        this.scrollPane = new ScrollPane(this.table, skin);

        this.stack.add(this.background);
        this.stack.setPosition(0, toolHeight);

        this.addActor(this.stack);
        this.addActor(this.scrollPane);
        this.addActor(this.toolPane);
    }

    @Override
    public void setSize(float width, float height)
    {
        for(int i = 0; i < this.table.getChildren().size; i ++)
        {
            this.table.getChildren().get(i).setSize(width, toolHeight);
            this.table.getCell(this.table.getChildren().get(i)).size(width, toolHeight);
        }

        this.table.invalidateHierarchy();

        this.scrollPane.setSize(width, height);
        this.scrollPane.invalidateHierarchy();

        this.stack.setSize(width, height - toolHeight);
        this.background.setBounds(0, 0, width, height - toolHeight);
        this.toolPane.setSize(width, toolHeight);

        this.stack.invalidateHierarchy();

        super.setSize(width, height);
    }

    public void newLayer(LayerTypes type)
    {
        final TileMap selectedMap = this.map;
        final LayerField layer = new LayerField(type.name, type, editor, map, skin, this);
        this.table.add(layer).padBottom(1).row();
        this.layers.add(layer);
        selectedMap.layers.add(layer.mapLayer);

        ClickListener listener = new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                unselectAll();
                layer.select();
                selectedMap.selectedLayer = layer.mapLayer;
            }
        };
        layer.layerName.addListener(listener);
        layer.typeImage.addListener(listener);

        rearrangeLayers();

        setSize(getWidth(), getHeight()); // Resize to fit the new field
    }

    public void moveLayerUp(LayerField layer)
    {
        int index = this.layers.indexOf(layer, false);
        index --;
        if(index < 0)
            index = 0;
        this.layers.removeValue(layer, false);
        this.layers.insert(index, layer);

        rearrangeLayers();
        rebuild();
    }

    public void moveLayerDown(LayerField layer)
    {
        int index = this.layers.indexOf(layer, false);
        index ++;
        if(index >= this.layers.size)
            index = this.layers.size - 1;
        this.layers.removeValue(layer, false);
        this.layers.insert(index, layer);

        rearrangeLayers();
        rebuild();
    }

    public void removeLayer(LayerField layerField)
    {
        this.table.removeActor(layerField, false);
        this.layers.removeValue(layerField, false);
        this.map.layers.removeValue(layerField.mapLayer, false);

        rearrangeLayers();
        rebuild();
    }

    /** Fixes the TileMap array to draw in the correct order. */
    private void rearrangeLayers()
    {
        this.map.layers.clear();
        for(int i = this.layers.size - 1; i >= 0; i --)
            this.map.layers.add(layers.get(i).mapLayer);
    }

    /** Rebuilds the table to remove gaps when removing properties. */
    private void rebuild()
    {
        this.table.clearChildren();
        for(int i = 0; i < this.layers.size; i ++)
            this.table.add(this.layers.get(i)).padBottom(1).row();
        setSize(getWidth(), getHeight()); // Resize to fit the fields
    }

    public void unselectAll()
    {
        for(int i = 0; i < this.layers.size; i ++)
            this.layers.get(i).unselect();
        this.map.selectedLayer = null;
    }
}