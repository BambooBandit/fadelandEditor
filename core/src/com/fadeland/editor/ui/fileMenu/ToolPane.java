package com.fadeland.editor.ui.fileMenu;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.fadeland.editor.FadelandEditor;
import com.fadeland.editor.GameAssets;
import com.fadeland.editor.map.Layer;
import com.fadeland.editor.map.MapSprite;
import com.fadeland.editor.map.SpriteLayer;
import com.fadeland.editor.map.TileMap;
import com.fadeland.editor.ui.MinMaxDialog;
import com.fadeland.editor.undoredo.BringSpriteUpOrDown;

import static com.fadeland.editor.FadelandEditor.toolHeight;

/** Handles switching views of maps via tabs, adding and removing tabs.*/
public class ToolPane extends Group
{
    private Stack pane;
    private Table toolTable;
    private Image background;
    private Skin skin;
    private FadelandEditor editor;

    private Tool brush;
    private Tool eraser;
    private Tool fill;
    private Tool bind;
    private Tool stamp;
    private Tool drawPoint;
    private Tool drawObject;
    private Tool objectVerticeSelect;
    private Tool boxSelect;
    private Tool select;
    private Tool grab;
    public Tool random;
    public Tool blocked;
    public Tool parallax;
    public Tool perspective;
    public Tool top;
    public Tool lines;
    public Tool b2drender;
    private Tool selectedTool;
    private TextButton bringUp;
    private TextButton bringDown;
    private TextButton bringTop;
    private TextButton bringBottom;
    private TextButton layerDownOverride;
    private TextButton layerUpOverride;
    private TextButton layerOverrideReset;

    public MinMaxDialog minMaxDialog;
    private TextButton minMaxButton;

    public Label fps;

    public ToolPane(FadelandEditor editor, Skin skin)
    {
        this.toolTable = new Table();
        this.brush = new Tool(Tools.BRUSH, this, skin, false);
        this.eraser = new Tool(Tools.ERASER, this, skin, false);
        this.fill = new Tool(Tools.FILL, this, skin, false);
        this.bind = new Tool(Tools.BIND, this, skin, false);
        this.stamp = new Tool(Tools.STAMP, this, skin, false);
        this.drawPoint = new Tool(Tools.DRAWPOINT, this, skin, false);
        this.drawObject = new Tool(Tools.DRAWOBJECT, this, skin, false);
        this.objectVerticeSelect = new Tool(Tools.OBJECTVERTICESELECT, this, skin, false);
        this.boxSelect = new Tool(Tools.BOXSELECT, this, skin, false);
        this.select = new Tool(Tools.SELECT, this, skin, false);
        this.grab = new Tool(Tools.GRAB, this, skin, false);
        this.random = new Tool(Tools.RANDOM, this, skin, true);
        this.blocked = new Tool(Tools.BLOCKED, this, skin, true);
        this.parallax = new Tool(Tools.PARALLAX, this, skin, true);
        this.parallax.select();
        this.perspective = new Tool(Tools.PERSPECTIVE, this, skin, true);
        this.top = new Tool(Tools.TOP, this, skin, true);
        this.top.select();
        this.lines = new Tool(Tools.LINES, this, skin, true);
        this.b2drender = new Tool(Tools.B2DR, this, skin, true);
        this.bringUp = new TextButton("^", skin);
        this.bringDown = new TextButton("v", skin);
        this.bringTop = new TextButton("^^", skin);
        this.bringBottom = new TextButton("vv", skin);
        this.layerDownOverride = new TextButton("Layer Override v", skin);
        this.layerUpOverride = new TextButton("Layer Override ^", skin);
        this.layerOverrideReset= new TextButton("Layer Override Reset", skin);

        this.minMaxDialog = new MinMaxDialog(editor.stage, skin);
        this.minMaxButton = new TextButton("Min Max Settings", skin);

        this.fps = new Label("0", skin);

        setListeners();
        this.toolTable.left();
        this.toolTable.add(this.brush).padRight(1);
        this.toolTable.add(this.eraser).padRight(1);
        this.toolTable.add(this.fill).padRight(1);
        this.toolTable.add(this.bind).padRight(1);
        this.toolTable.add(this.stamp).padRight(1);
        this.toolTable.add(this.drawPoint).padRight(1);
        this.toolTable.add(this.drawObject).padRight(1);
        this.toolTable.add(this.objectVerticeSelect).padRight(1);
        this.toolTable.add(this.boxSelect).padRight(1);
        this.toolTable.add(this.select).padRight(1);
        this.toolTable.add(this.grab).padRight(1);
        this.toolTable.add(this.random).padRight(1);
        this.toolTable.add(this.blocked).padRight(1);
        this.toolTable.add(this.parallax).padRight(1);
        this.toolTable.add(this.perspective).padRight(1);
        this.toolTable.add(this.top).padRight(1);
        this.toolTable.add(this.lines).padRight(5);
        this.toolTable.add(this.b2drender).padRight(5);
        this.toolTable.add(this.bringUp).padRight(1);
        this.toolTable.add(this.bringDown).padRight(1);
        this.toolTable.add(this.bringTop).padRight(1);
        this.toolTable.add(this.bringBottom).padRight(5);
        this.toolTable.add(this.layerDownOverride).padRight(1);
        this.toolTable.add(this.layerUpOverride).padRight(1);
        this.toolTable.add(this.layerOverrideReset).padRight(5);
        this.toolTable.add(this.minMaxButton).padRight(5);
        this.toolTable.add(this.fps).padRight(1);

        this.editor = editor;
        this.skin = skin;
        this.pane = new Stack();

        this.background = new Image(GameAssets.getUIAtlas().createPatch("load-background"));
        this.pane.add(this.background);
        this.pane.add(this.toolTable);

        this.addActor(this.pane);
    }

    @Override
    public void setSize(float width, float height)
    {
        this.pane.setSize(width, height);
        this.background.setBounds(0, 0, width, height);

        // Resize all buttons in the pane
        this.toolTable.getCell(this.brush).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.eraser).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.fill).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.bind).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.stamp).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.drawPoint).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.drawObject).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.objectVerticeSelect).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.boxSelect).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.select).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.grab).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.random).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.blocked).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.parallax).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.perspective).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.top).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.lines).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.b2drender).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.bringUp).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.bringDown).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.bringTop).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.bringBottom).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.layerDownOverride).size(toolHeight * 4.75f, toolHeight);
        this.toolTable.getCell(this.layerUpOverride).size(toolHeight * 4.75f, toolHeight);
        this.toolTable.getCell(this.layerOverrideReset).size(toolHeight * 4.75f, toolHeight);
        this.toolTable.getCell(this.minMaxButton).size(toolHeight * 4, toolHeight);
        this.toolTable.getCell(this.fps).size(toolHeight, toolHeight);
        this.toolTable.invalidateHierarchy();

        this.pane.invalidateHierarchy();

        super.setSize(width, height);
    }

    public void selectTool(Tool selectedTool)
    {
        if(selectedTool.isToggleable)
        {
            if(selectedTool.selected)
                selectedTool.unselect();
            else
                selectedTool.select();
        }
        else
        {
            this.selectedTool = selectedTool;
            for (int i = 0; i < this.toolTable.getChildren().size; i++)
            {
                if(!(this.toolTable.getChildren().get(i) instanceof Tool))
                    continue;
                Tool tool = (Tool) this.toolTable.getChildren().get(i);
                if (tool == selectedTool)
                    tool.select();
                else if(!tool.isToggleable)
                    tool.unselect();
            }
        }
    }

    public Tool getTool()
    {
        return selectedTool;
    }

    private void setListeners()
    {
        this.bringUp.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                TileMap map = ((TileMap)editor.getScreen());
                if(map == null || map.selectedSprites.size != 1)
                    return;
                BringSpriteUpOrDown bringSpriteUpOrDown = new BringSpriteUpOrDown(map, map.selectedLayer.tiles);
                MapSprite selectedSprite = map.selectedSprites.first();
                int index = map.selectedLayer.tiles.indexOf(selectedSprite, true);
                if(index < map.selectedLayer.tiles.size - 1)
                    map.selectedLayer.tiles.swap(index, index + 1);
                bringSpriteUpOrDown.addNew();
                map.performAction(bringSpriteUpOrDown);
            }
        });

        this.bringDown.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                TileMap map = ((TileMap)editor.getScreen());
                if(map == null || map.selectedSprites.size != 1)
                    return;
                BringSpriteUpOrDown bringSpriteUpOrDown = new BringSpriteUpOrDown(map, map.selectedLayer.tiles);
                MapSprite selectedSprite = map.selectedSprites.first();
                int index = map.selectedLayer.tiles.indexOf(selectedSprite, true);
                if(index > 0)
                    map.selectedLayer.tiles.swap(index, index - 1);
                bringSpriteUpOrDown.addNew();
                map.performAction(bringSpriteUpOrDown);
            }
        });

        this.bringTop.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                TileMap map = ((TileMap)editor.getScreen());
                if(map == null || map.selectedSprites.size != 1)
                    return;
                BringSpriteUpOrDown bringSpriteUpOrDown = new BringSpriteUpOrDown(map, map.selectedLayer.tiles);
                MapSprite selectedSprite = map.selectedSprites.first();
                map.selectedLayer.tiles.removeValue(selectedSprite, true);
                map.selectedLayer.tiles.add(selectedSprite);
                bringSpriteUpOrDown.addNew();
                map.performAction(bringSpriteUpOrDown);
            }
        });

        this.bringBottom.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                TileMap map = ((TileMap)editor.getScreen());
                if(map == null || map.selectedSprites.size != 1)
                    return;
                BringSpriteUpOrDown bringSpriteUpOrDown = new BringSpriteUpOrDown(map, map.selectedLayer.tiles);
                MapSprite selectedSprite = map.selectedSprites.first();
                map.selectedLayer.tiles.removeValue(selectedSprite, true);
                map.selectedLayer.tiles.insert(0, selectedSprite);
                bringSpriteUpOrDown.addNew();
                map.performAction(bringSpriteUpOrDown);
            }
        });

        this.layerDownOverride.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                TileMap map = ((TileMap)editor.getScreen());
                if(map == null || map.selectedLayer == null)
                    return;
                if(map.selectedLayer.overrideSprite == null)
                {
                    int layerIndex = map.layers.indexOf(map.selectedLayer, true);
                    if(layerIndex == 0)
                        return;
                    for(int i = layerIndex - 1; i > 0; i--)
                    {
                        Layer layer = map.layers.get(i);
                        if(layer instanceof SpriteLayer)
                        {
                            MapSprite mapSprite = (MapSprite) layer.tiles.get(layer.tiles.size - 1);
                            map.selectedLayer.overrideSprite = mapSprite;
                            mapSprite.layerOverride = map.selectedLayer;
                            return;
                        }
                    }
                }
                else
                {
                    int layerIndex = map.layers.indexOf(map.selectedLayer.overrideSprite.layer, true);
                    int spriteIndex = map.selectedLayer.overrideSprite.layer.tiles.indexOf(map.selectedLayer.overrideSprite, true);
                    spriteIndex --;
                    if(spriteIndex < 0)
                    {
                        while(layerIndex - 1 > 0)
                        {
                            layerIndex--;
                            if(map.layers.get(layerIndex) instanceof SpriteLayer)
                                break;
                        }
                        if(layerIndex < 0 || !(map.layers.get(layerIndex) instanceof SpriteLayer))
                            return;
                        spriteIndex = map.layers.get(layerIndex).tiles.size - 1;
                    }
                    map.selectedLayer.overrideSprite.layerOverride = null;
                    map.selectedLayer.overrideSprite = (MapSprite) map.layers.get(layerIndex).tiles.get(spriteIndex);
                    map.selectedLayer.overrideSprite.layerOverride = map.selectedLayer;
                }
            }
        });

        this.layerUpOverride.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                TileMap map = ((TileMap)editor.getScreen());
                if(map == null || map.selectedLayer == null)
                    return;
                if(map.selectedLayer.overrideSprite == null)
                {
                    int layerIndex = map.layers.indexOf(map.selectedLayer, true);
                    if(layerIndex == map.layers.size - 1)
                        return;
                    for(int i = layerIndex + 1; i < map.layers.size; i++)
                    {
                        Layer layer = map.layers.get(i);
                        if(layer instanceof SpriteLayer)
                        {
                            MapSprite mapSprite = (MapSprite) layer.tiles.get(layer.tiles.size - 1);
                            map.selectedLayer.overrideSprite = mapSprite;
                            mapSprite.layerOverride = map.selectedLayer;
                            return;
                        }
                    }
                }
                else
                {
                    int layerIndex = map.layers.indexOf(map.selectedLayer.overrideSprite.layer, true);
                    int spriteIndex = map.selectedLayer.overrideSprite.layer.tiles.indexOf(map.selectedLayer.overrideSprite, true);
                    spriteIndex ++;
                    if(spriteIndex >= map.selectedLayer.overrideSprite.layer.tiles.size)
                    {
                        while(layerIndex + 1 < map.layers.size - 1)
                        {
                            layerIndex++;
                            if(map.layers.get(layerIndex) instanceof SpriteLayer)
                                break;
                        }
                        if(layerIndex >= map.layers.size || !(map.layers.get(layerIndex) instanceof SpriteLayer))
                            return;
                        spriteIndex = 0;
                    }
                    map.selectedLayer.overrideSprite.layerOverride = null;
                    map.selectedLayer.overrideSprite = (MapSprite) map.layers.get(layerIndex).tiles.get(spriteIndex);
                    map.selectedLayer.overrideSprite.layerOverride = map.selectedLayer;
                }
            }
        });
        this.layerOverrideReset.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                TileMap map = ((TileMap)editor.getScreen());
                if(map == null || map.selectedLayer == null)
                    return;
                if(map.selectedLayer.overrideSprite != null)
                {
                    map.selectedLayer.overrideSprite.layerOverride = null;
                    map.selectedLayer.overrideSprite = null;
                }
            }
        });

        this.minMaxButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                minMaxDialog.setVisible(true);
            }
        });
    }
}
