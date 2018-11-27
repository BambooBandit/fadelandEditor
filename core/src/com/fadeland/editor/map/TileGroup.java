package com.fadeland.editor.map;

import com.badlogic.gdx.utils.Array;
import com.fadeland.editor.ui.tileMenu.TileTool;

import java.util.ArrayList;

import static com.fadeland.editor.ui.tileMenu.TileMenu.*;

public class TileGroup
{
    public Array<TileTool> boundGroup; // 2D array of the group of tile tools attached to the tiles
    public Array<String> types; // 2D array of tile types that have a group bound to it
    public int width; // Determines how long the rows of the boundGroup array is.
    public int height; // Determines how long the columns of the boundGroup array is.
    public TileMap map;

    public TileGroup(int width, int height, ArrayList<Integer> boundGroupIds, ArrayList<String> types, TileMap tileMap)
    {
        this.width = width;
        this.height = height;
        this.map = tileMap;

        this.boundGroup = new Array<>();
        this.types = new Array<>();

        for(int i = 0; i < boundGroupIds.size(); i ++)
        {
            if(boundGroupIds.get(i) == null)
                boundGroup.add(null);
            else
            {
                TileTool tileTool = map.tileMenu.getTileTool("tile", boundGroupIds.get(i));
                boundGroup.add(tileTool);
            }
        }
        for(int i = 0; i < types.size(); i ++)
            this.types.add(types.get(i));
        debugShapePrint();
    }

    public TileGroup(float clickedX, float clickedY, Array<TileTool> selectedTileTools, TileMap map)
    {
        try
        {
            if(selectedTileTools == null)
                throw new Exception("Selected tile tools cannot be null.");
            if(selectedTileTools.size < 2)
                throw new Exception("Must have more than 1 selected Tile Tool.");
        }
        catch (Exception e) { e.printStackTrace(); }

        this.boundGroup = new Array<>();
        this.types = new Array<>();
        this.map = map;

        // Find lowest and highest X to determine width
        int lowestX = tileSheetWidth - selectedTileTools.first().x / tileSize;
        int highestX = tileSheetWidth - selectedTileTools.first().x / tileSize;
        int lowestY = tileSheetHeight - selectedTileTools.first().y / tileSize;
        int highestY = tileSheetHeight - selectedTileTools.first().y / tileSize;
        for(int i = 1; i < selectedTileTools.size; i ++)
        {
            if(tileSheetWidth - selectedTileTools.get(i).x / tileSize < lowestX)
                lowestX = tileSheetWidth - selectedTileTools.get(i).x / tileSize;
            if(tileSheetWidth - selectedTileTools.get(i).x / tileSize > highestX)
                highestX = tileSheetWidth - selectedTileTools.get(i).x / tileSize;

            if(tileSheetHeight - selectedTileTools.get(i).y / tileSize < lowestY)
                lowestY = tileSheetHeight - selectedTileTools.get(i).y / tileSize;
            if(tileSheetHeight - selectedTileTools.get(i).y / tileSize > highestY)
                highestY = tileSheetHeight - selectedTileTools.get(i).y / tileSize;
        }
        width = highestX - lowestX + 1;
        height = highestY - lowestY + 1;

        // Fill the boundGroup array
        int x = 0;
        for(int i = 0; i < selectedTileTools.size; i ++)
        {
            while((tileSheetWidth - selectedTileTools.get(i).x / tileSize) - lowestX > x)
            {
                boundGroup.add(null);
                types.add(null);
                x ++;
            }

            if((tileSheetWidth - selectedTileTools.get(i).x / tileSize) - lowestX == x)
            {
                boundGroup.add(selectedTileTools.get(i));

                int xOffset = selectedTileTools.first().x - selectedTileTools.get(i).x;
                int yOffset = selectedTileTools.first().y - selectedTileTools.get(i).y;
                Tile tile = map.getTile(clickedX + xOffset, clickedY + yOffset - tileSize);
                if(tile.tool == null)
                    types.add(null);
                else
                    types.add(tile.tool.getPropertyField("Type").value.getText());
            }

            x ++;
            if(x >= width)
                x = 0;
        }

        debugShapePrint();
    }

    private void debugShapePrint()
    {
        // Print to see if the shapes good
        System.out.println("group:");
        int x = 0;
        for(int i = 0; i < boundGroup.size; i ++)
        {
            if(boundGroup.get(i) == null)
                System.out.print("null ");
            else
                System.out.print(boundGroup.get(i).getPropertyField("Type").value.getText() + " ");
            x ++;
            if(x >= width)
            {
                System.out.println();
                x = 0;
            }
        }

        System.out.println();
        System.out.println("tiles:");
        x = 0;
        for(int i = 0; i < types.size; i ++)
        {
            System.out.print(types.get(i) + " ");
            x ++;
            if(x >= width)
            {
                System.out.println();
                x = 0;
            }
        }
    }
}
