package com.fadeland.editor.ui.layerMenu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.fadeland.editor.FadelandEditor;
import com.fadeland.editor.map.*;
import com.fadeland.editor.undoredo.CreateOrRemoveLayer;

public class LayerField extends Group
{
    public TextField layerName;
    private LayerTypes type;
    public Image typeImage;
    private TextButton up;
    private TextButton down;
    private Stack visibilityStack;
    public Image visibleImg;
    public Image notVisibleImg;
    public TextButton remove;
    private Table table;

    public Layer mapLayer;

    private LayerMenu menu;

    public LayerField(String name, LayerTypes type, FadelandEditor editor, TileMap map, Skin skin, final LayerMenu menu)
    {
        this.menu = menu;
        this.type = type;

        if(type == LayerTypes.TILE)
            this.mapLayer = new TileLayer(editor, map, type, this);
        else if(type == LayerTypes.SPRITE)
            this.mapLayer = new SpriteLayer(editor, map, type, this);
        else if(type == LayerTypes.OBJECT)
            this.mapLayer = new ObjectLayer(editor, map, type, this);

        this.layerName = new TextField(name, skin);
        this.table = new Table();
        this.table.bottom().left();
        this.typeImage = new Image(new Texture("ui/" + type.name + ".png")); // TODO pack it in atlas
        this.up = new TextButton("^", skin);
        this.down = new TextButton("v", skin);
        this.visibilityStack = new Stack();
        this.visibleImg = new Image(new Texture("ui/visible.png")); // TODO pack it in atlas
        this.notVisibleImg = new Image(new Texture("ui/notVisible.png")); // TODO pack it in atlas
        this.visibilityStack.add(this.visibleImg);
        this.visibilityStack.add(this.notVisibleImg);
        this.notVisibleImg.setVisible(false);
        this.remove = new TextButton("X", skin);

        this.table.add(this.layerName);
        this.table.add(this.typeImage);
        this.table.add(this.up);
        this.table.add(this.down);
        this.table.add(this.visibilityStack);
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
        this.visibleImg.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                visibleImg.setVisible(false);
                notVisibleImg.setVisible(true);
            }
        });
        this.notVisibleImg.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                visibleImg.setVisible(true);
                notVisibleImg.setVisible(false);
            }
        });
        this.remove.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                map.performAction(new CreateOrRemoveLayer(layer.mapLayer, false));
                menu.removeLayer(layer);
            }
        });

        addActor(this.table);
    }

    @Override
    public void setSize(float width, float height)
    {
        this.layerName.setSize(width - (height * 5), height);
        this.table.getCell(this.layerName).size(width - (height * 5), height);
        this.table.getCell(this.typeImage).size(height, height);
        this.table.getCell(this.up).size(height, height);
        this.table.getCell(this.down).size(height, height);
        this.table.getCell(this.visibilityStack).size(height, height);
        this.visibleImg.setSize(height, height);
        this.notVisibleImg.setSize(height, height);
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

