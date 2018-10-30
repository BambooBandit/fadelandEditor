package com.fadeland.editor.ui.fileMenu;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.fadeland.editor.FadelandEditor;
import com.fadeland.editor.GameAssets;

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
    private Tool grab;
    private Tool random;
    public Tool lines;
    private Tool selectedTool;

    public ToolPane(FadelandEditor editor, Skin skin)
    {
        this.toolTable = new Table();
        this.brush = new Tool(Tools.BRUSH, this, skin, false);
        this.eraser = new Tool(Tools.ERASER, this, skin, false);
        this.fill = new Tool(Tools.FILL, this, skin, false);
        this.grab = new Tool(Tools.GRAB, this, skin, false);
        this.random = new Tool(Tools.RANDOM, this, skin, true);
        this.lines = new Tool(Tools.LINES, this, skin, true);
        this.toolTable.left();
        this.toolTable.add(this.brush).padRight(1);
        this.toolTable.add(this.eraser).padRight(1);
        this.toolTable.add(this.fill).padRight(1);
        this.toolTable.add(this.grab).padRight(1);
        this.toolTable.add(this.random).padRight(1);
        this.toolTable.add(this.lines);

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
        this.toolTable.getCell(this.grab).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.random).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.lines).size(toolHeight, toolHeight);
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
}
