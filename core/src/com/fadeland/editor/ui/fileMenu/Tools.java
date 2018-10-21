package com.fadeland.editor.ui.fileMenu;

public enum Tools
{
    BRUSH("brush"), RANDOMBRUSH("randomBrush"), ERASER("eraser"), FILL("fill"), GRAB("grab"), LINES("lines");

    public String name;
    Tools(String name)
    {
        this.name = name;
    }
}
