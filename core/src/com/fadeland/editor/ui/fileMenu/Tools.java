package com.fadeland.editor.ui.fileMenu;

public enum Tools
{
    BRUSH("brush"), RANDOM("random"), ERASER("eraser"), FILL("fill"), BOXSELECT("boxSelect"), SELECT("select"), GRAB("grab"), LINES("lines");

    public String name;
    Tools(String name)
    {
        this.name = name;
    }
}
