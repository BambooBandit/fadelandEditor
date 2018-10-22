package com.fadeland.editor.ui.layerMenu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.fadeland.editor.FadelandEditor;
import com.fadeland.editor.map.Layer;
import com.fadeland.editor.map.ObjectLayer;
import com.fadeland.editor.map.TileLayer;
import com.fadeland.editor.map.TileMap;

public class LayerField extends Group
{
    private TextField layerName;
    private LayerTypes type;
    private Image typeImage;
    private TextButton up;
    private TextButton down;
    private TextButton remove;
    private Table table;

    public Layer mapLayer;

    private LayerMenu menu;

    public LayerField(String name, LayerTypes type, FadelandEditor editor, TileMap map, Skin skin, final LayerMenu menu)
    {
        this.menu = menu;
        this.type = type;

        if(type == LayerTypes.TILE)
            this.mapLayer = new TileLayer(editor, map);
        else if(type == LayerTypes.OBJECT)
            this.mapLayer = new ObjectLayer(editor, map);

        this.layerName = new TextField(name, skin);
        this.table = new Table();
        this.table.bottom().left();
        this.typeImage = new Image(new Texture("ui/" + type.name + ".png")); // TODO pack it in atlas
        this.up = new TextButton("^", skin);
        this.down = new TextButton("v", skin);
        this.remove = new TextButton("X", skin);

        this.table.add(this.layerName);
        this.table.add(this.typeImage);
        this.table.add(this.up);
        this.table.add(this.down);
        this.table.add(this.remove);

        final LayerField layer = this;
        this.up.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                menu.moveLayerUp(layer);
            }
        });
        this.down.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                menu.moveLayerDown(layer);
            }
        });
        this.remove.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                menu.removeLayer(layer);
            }
        });

        addActor(this.table);
    }

    @Override
    public void setSize(float width, float height)
    {
        this.layerName.setSize(width - (height * 4), height);
        this.table.getCell(this.layerName).size(width - (height * 4), height);
        this.table.getCell(this.typeImage).size(height, height);
        this.table.getCell(this.up).size(height, height);
        this.table.getCell(this.down).size(height, height);
        this.table.getCell(this.remove).size(height, height);
        this.table.invalidateHierarchy();
        super.setSize(width, height);
    }

    public void select()
    {
        this.layerName.setColor(Color.GREEN);
        this.up.setColor(Color.GREEN);
        this.down.setColor(Color.GREEN);
        this.remove.setColor(Color.GREEN);
    }

    public void unselect()
    {
        this.layerName.setColor(Color.WHITE);
        this.up.setColor(Color.WHITE);
        this.down.setColor(Color.WHITE);
        this.remove.setColor(Color.WHITE);
    }
}

