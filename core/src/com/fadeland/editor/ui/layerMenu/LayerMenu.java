package com.fadeland.editor.ui.layerMenu;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.fadeland.editor.FadelandEditor;
import com.fadeland.editor.GameAssets;
import com.fadeland.editor.map.Layer;
import com.fadeland.editor.map.TileMap;
import com.fadeland.editor.undoredo.CreateOrRemoveLayer;
import com.fadeland.editor.undoredo.MoveLayer;
import com.fadeland.editor.undoredo.SelectLayer;

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
        this.scrollPane.setPosition(0, toolHeight);

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

        this.scrollPane.setSize(width, height - toolHeight);
        this.scrollPane.invalidateHierarchy();

        this.stack.setSize(width, height - toolHeight);
        this.background.setBounds(0, 0, width, height - toolHeight);
        this.toolPane.setSize(width, toolHeight);

        this.stack.invalidateHierarchy();

        super.setSize(width, height);
    }

    public Layer newLayer(LayerTypes type)
    {
        final TileMap selectedMap = this.map;
        final LayerField layer = new LayerField(type.name, type, editor, map, skin, this);
        map.performAction(new CreateOrRemoveLayer(map, layer.mapLayer, true));
        this.table.add(layer).padBottom(1).row();
        this.layers.add(layer);
        selectedMap.layers.add(layer.mapLayer);

        ClickListener listener = new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                SelectLayer selectLayer = new SelectLayer(selectedMap, selectedMap.selectedLayer, layer.mapLayer);
                selectedMap.performAction(selectLayer);
                unselectAll();
                layer.select();
                selectedMap.selectedLayer = layer.mapLayer;
                selectedMap.propertyMenu.rebuild();
            }
        };
        layer.layerName.addListener(listener);
        layer.typeImage.addListener(listener);

        rearrangeLayers();

        setSize(getWidth(), getHeight()); // Resize to fit the new field
        return layer.mapLayer;
    }

    public void addLayer(Layer layer)
    {
        this.table.add(layer.layerField).padBottom(1).row();
        this.layers.add(layer.layerField);
        this.map.layers.add(layer);

        rearrangeLayers();

        setSize(getWidth(), getHeight()); // Resize to fit the new field
    }

    public void moveLayerUp(LayerField layer)
    {
        MoveLayer moveLayer = new MoveLayer(map, this.layers);
        map.performAction(moveLayer);
        int index = this.layers.indexOf(layer, false);
        index --;
        if(index < 0)
            index = 0;
        this.layers.removeValue(layer, false);
        this.layers.insert(index, layer);

        rearrangeLayers();
        rebuild();
        moveLayer.addNewLayers();
    }

    public void moveLayerDown(LayerField layer)
    {
        MoveLayer moveLayer = new MoveLayer(map, this.layers);
        map.performAction(moveLayer);
        int index = this.layers.indexOf(layer, false);
        index ++;
        if(index >= this.layers.size)
            index = this.layers.size - 1;
        this.layers.removeValue(layer, false);
        this.layers.insert(index, layer);

        rearrangeLayers();
        rebuild();
        moveLayer.addNewLayers();
    }

    public void removeLayer(LayerField layerField)
    {
        this.table.removeActor(layerField, false);
        this.layers.removeValue(layerField, false);
        this.map.layers.removeValue(layerField.mapLayer, false);
        if(this.map.selectedLayer == layerField.mapLayer)
            this.map.selectedLayer = null;

        rearrangeLayers();
        rebuild();
    }

    /** Fixes the TileMap array to draw in the correct order. */
    public void rearrangeLayers()
    {
        this.map.layers.clear();
        for(int i = this.layers.size - 1; i >= 0; i --)
            this.map.layers.add(layers.get(i).mapLayer);
    }

    /** Rebuilds the table to remove gaps when removing properties. */
    public void rebuild()
    {
        this.table.clearChildren();
        for(int i = 0; i < this.layers.size; i ++)
            this.table.add(this.layers.get(i)).padBottom(1).row();
        setSize(getWidth(), getHeight()); // Resize to fit the fields
    }

    public void unselectAll()
    {
        for(int i = 0; i < map.selectedSprites.size; i ++)
            map.selectedSprites.get(i).unselect();
        for(int i = 0; i < map.selectedObjects.size; i ++)
            map.selectedObjects.get(i).unselect();
        map.selectedSprites.clear();
        map.selectedObjects.clear();
        for(int i = 0; i < this.layers.size; i ++)
            this.layers.get(i).unselect();
        this.map.selectedLayer = null;
        this.map.propertyMenu.rebuild();
    }
}