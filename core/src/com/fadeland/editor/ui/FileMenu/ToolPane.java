package com.fadeland.editor.ui.FileMenu;

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
    private Tool grab;
    private Tool selectedTool;

    public ToolPane(FadelandEditor editor, Skin skin)
    {
        this.toolTable = new Table();
        this.brush = new Tool(Tools.BRUSH, this, skin);
        this.eraser = new Tool(Tools.ERASER, this, skin);
        this.grab = new Tool(Tools.GRAB, this, skin);
        this.toolTable.left();
        this.toolTable.add(this.brush).padRight(1);
        this.toolTable.add(this.eraser).padRight(1);
        this.toolTable.add(this.grab);

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
        this.toolTable.getCell(this.grab).size(toolHeight, toolHeight);
        this.toolTable.invalidateHierarchy();

        this.pane.invalidateHierarchy();

        super.setSize(width, height);
    }

    public void selectTool(Tool selectedTool)
    {
        this.selectedTool = selectedTool;
        for(int i = 0; i < this.toolTable.getChildren().size; i ++)
        {
            Tool tool = (Tool) this.toolTable.getChildren().get(i);
            if(tool == selectedTool)
                tool.select();
            else
                tool.unselect();
        }
    }
}
