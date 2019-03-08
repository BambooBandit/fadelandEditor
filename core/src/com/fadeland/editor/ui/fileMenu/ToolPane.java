package com.fadeland.editor.ui.fileMenu;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.fadeland.editor.FadelandEditor;
import com.fadeland.editor.GameAssets;
import com.fadeland.editor.map.MapSprite;
import com.fadeland.editor.map.TileMap;
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
    public Tool top;
    public Tool lines;
    private Tool selectedTool;
    private TextButton bringUp;
    private TextButton bringDown;
    private TextButton bringTop;
    private TextButton bringBottom;

    private Label minSizeLabel;
    public TextField minSize;
    private Label maxSizeLabel;
    public TextField maxSize;
    private Label minRotationLabel;
    public TextField minRotation;
    private Label maxRotationLabel;
    public TextField maxRotation;
    public Label fps;

    public float minSizeValue = 1;
    public float maxSizeValue = 1;
    public float minRotationValue = 0;
    public float maxRotationValue = 0;

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
        this.top = new Tool(Tools.TOP, this, skin, true);
        this.top.select();
        this.lines = new Tool(Tools.LINES, this, skin, true);
        this.bringUp = new TextButton("^", skin);
        this.bringDown = new TextButton("v", skin);
        this.bringTop = new TextButton("^^", skin);
        this.bringBottom = new TextButton("vv", skin);

        this.minSizeLabel = new Label("MnSz", skin);
        this.minSize = new TextField("1", skin);
        this.maxSizeLabel = new Label("MxSz", skin);
        this.maxSize = new TextField("1", skin);
        this.minRotationLabel = new Label("MnRt", skin);
        this.minRotation = new TextField("0", skin);
        this.maxRotationLabel = new Label("MxRt", skin);
        this.maxRotation = new TextField("0", skin);

        this.fps = new Label("0", skin);

        setUpAndDownListeners();
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
        this.toolTable.add(this.top).padRight(1);
        this.toolTable.add(this.lines).padRight(5);
        this.toolTable.add(this.bringUp).padRight(1);
        this.toolTable.add(this.bringDown).padRight(1);
        this.toolTable.add(this.bringTop).padRight(1);
        this.toolTable.add(this.bringBottom).padRight(5);
        this.toolTable.add(this.minSizeLabel).padRight(1);
        this.toolTable.add(this.minSize).padRight(1);
        this.toolTable.add(this.maxSizeLabel).padRight(1);
        this.toolTable.add(this.maxSize).padRight(5);
        this.toolTable.add(this.minRotationLabel).padRight(1);
        this.toolTable.add(this.minRotation).padRight(1);
        this.toolTable.add(this.maxRotationLabel).padRight(1);
        this.toolTable.add(this.maxRotation).padRight(5);
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
        this.toolTable.getCell(this.top).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.lines).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.bringUp).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.bringDown).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.bringTop).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.bringBottom).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.minSizeLabel).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.minSize).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.maxSizeLabel).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.maxSize).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.minRotationLabel).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.minRotation).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.maxRotationLabel).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.maxRotation).size(toolHeight, toolHeight);
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

    private void setUpAndDownListeners()
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

        TextField.TextFieldFilter valueFilter = new TextField.TextFieldFilter()
        {
            @Override
            public boolean acceptChar(TextField textField, char c)
            {
                return c == '.' || c == '-' || Character.isDigit(c);
            }
        };

        this.minSize.setTextFieldFilter(valueFilter);
        this.minSize.addListener(new InputListener()
        {
            @Override
            public boolean keyTyped (InputEvent event, char character)
            {
                try
                {
                    minSizeValue = Float.parseFloat(minSize.getText());
                    if(minSizeValue > 1)
                        minSizeValue = 1;
                }
                catch(NumberFormatException e)
                {
                    minSizeValue = 0;
                }
                return false;
            }
        });

        this.maxSize.setTextFieldFilter(valueFilter);
        this.maxSize.addListener(new InputListener()
        {
            @Override
            public boolean keyTyped (InputEvent event, char character)
            {
                try
                {
                    maxSizeValue = Float.parseFloat(maxSize.getText());
                    if(maxSizeValue > 1)
                        maxSizeValue = 1;
                }
                catch(NumberFormatException e)
                {
                    maxSizeValue = 0;
                }
                return false;
            }
        });


        this.minRotation.setTextFieldFilter(valueFilter);
        this.minRotation.addListener(new InputListener()
        {
            @Override
            public boolean keyTyped (InputEvent event, char character)
            {
                try
                {
                    minRotationValue = Float.parseFloat(minRotation.getText());
                }
                catch(NumberFormatException e)
                {
                    minRotationValue = 0;
                }
                return false;
            }
        });

        this.maxRotation.setTextFieldFilter(valueFilter);
        this.maxRotation.addListener(new InputListener()
        {
            @Override
            public boolean keyTyped (InputEvent event, char character)
            {
                try
                {
                    maxRotationValue = Float.parseFloat(maxRotation.getText());
                }
                catch(NumberFormatException e)
                {
                    maxRotationValue = 0;
                }
                return false;
            }
        });
    }
}
