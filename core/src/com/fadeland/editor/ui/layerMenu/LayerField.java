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

import static com.fadeland.editor.ui.layerMenu.LayerMenu.toolHeight;

public class LayerField extends Group
{
    public TextField layerName;
    private LayerTypes type;
    public Image typeImage;
    public Image attachedImage;
    private TextButton up;
    private TextButton down;
    private Stack visibilityStack;
    private Stack attachedVisibilityStack;
    public Image visibleImg;
    public Image notVisibleImg;
    public Image attachedVisibleImg;
    public Image attachedNotVisibleImg;
    public TextButton remove;
    private Table table;

    public Layer mapLayer;

    private LayerMenu menu;

    public boolean isSelected = false;

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
        if(type == LayerTypes.TILE || type == LayerTypes.SPRITE)
        {
            this.attachedImage = new Image(new Texture("ui/" + LayerTypes.OBJECT.name + ".png")); // TODO pack it in atlas
            this.attachedVisibilityStack = new Stack();
            this.attachedVisibleImg = new Image(new Texture("ui/visible.png")); // TODO pack it in atlas
            this.attachedNotVisibleImg = new Image(new Texture("ui/notVisible.png")); // TODO pack it in atlas
        }
        this.up = new TextButton("^", skin);
        this.down = new TextButton("v", skin);
        this.visibilityStack = new Stack();
        this.visibleImg = new Image(new Texture("ui/visible.png")); // TODO pack it in atlas
        this.notVisibleImg = new Image(new Texture("ui/notVisible.png")); // TODO pack it in atlas
        this.visibilityStack.add(this.typeImage);
        this.visibilityStack.add(this.visibleImg);
        this.visibilityStack.add(this.notVisibleImg);
        this.notVisibleImg.setVisible(false);
        if(this.attachedImage != null)
        {
            this.attachedVisibilityStack.add(this.attachedImage);
            this.attachedVisibilityStack.add(this.attachedVisibleImg);
            this.attachedVisibilityStack.add(this.attachedNotVisibleImg);
            this.attachedNotVisibleImg.setVisible(false);
        }
        this.remove = new TextButton("X", skin);
        this.remove.setColor(Color.FIREBRICK);

        this.table.add(this.layerName);
        this.table.add(this.visibilityStack);
        if(this.attachedVisibilityStack != null)
            this.table.add(this.attachedVisibilityStack);
        else
            this.table.add().padRight(toolHeight);
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
        if(this.attachedVisibilityStack != null)
        {
            this.attachedVisibleImg.addListener(new ClickListener()
            {
                @Override
                public void clicked(InputEvent event, float x, float y)
                {
                    attachedVisibleImg.setVisible(false);
                    attachedNotVisibleImg.setVisible(true);
                }
            });
            this.attachedNotVisibleImg.addListener(new ClickListener()
            {
                @Override
                public void clicked(InputEvent event, float x, float y)
                {
                    attachedVisibleImg.setVisible(true);
                    attachedNotVisibleImg.setVisible(false);
                }
            });
        }
        this.remove.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                map.performAction(new CreateOrRemoveLayer(map, layer.mapLayer, false));
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
        this.table.getCell(this.up).size(height, height);
        this.table.getCell(this.down).size(height, height);
        this.table.getCell(this.visibilityStack).size(height, height);
        this.typeImage.setSize(height, height);
        this.visibleImg.setSize(height, height);
        this.notVisibleImg.setSize(height, height);
        if(this.attachedVisibilityStack != null)
        {
            this.table.getCell(this.attachedVisibilityStack).size(height, height);
            this.attachedImage.setSize(height, height);
            this.attachedVisibleImg.setSize(height, height);
            this.attachedNotVisibleImg.setSize(height, height);
        }
        this.table.getCell(this.remove).size(height, height);
        this.table.invalidateHierarchy();
        super.setSize(width, height);
    }

    public void select()
    {
        this.layerName.setColor(Color.GREEN);
        this.up.setColor(Color.GREEN);
        this.down.setColor(Color.GREEN);
        this.isSelected = true;
    }

    public void unselect()
    {
        this.layerName.setColor(Color.WHITE);
        this.up.setColor(Color.WHITE);
        this.down.setColor(Color.WHITE);
        this.isSelected = false;
    }
}

