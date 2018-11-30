package com.fadeland.editor.ui.fileMenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Json;
import com.fadeland.editor.FadelandEditor;
import com.fadeland.editor.map.TileMap;
import com.fadeland.editor.map.TileMapData;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import static com.fadeland.editor.map.TileMap.untitledCount;

public class FileMenu extends Group
{
    private Table fileMenuTable;
    private Table buttonTable;
    public MapTabPane mapTabPane;
    public ToolPane toolPane;

    private TextButton newButton;
    private TextButton openButton;
    private TextButton saveButton;
    private TextButton saveAsButton;
    private TextButton undoButton;
    private TextButton redoButton;

    private FadelandEditor editor;

    public FileMenu(Skin skin, FadelandEditor fadelandEditor)
    {
        this.editor = fadelandEditor;

        this.newButton = new TextButton("New", skin);
        this.openButton = new TextButton("Open", skin);
        this.saveButton = new TextButton("Save", skin);
        this.saveAsButton = new TextButton("Save As", skin);
        this.undoButton = new TextButton("Undo", skin);
        this.redoButton = new TextButton("Redo", skin);

        // Buttons text color
        this.newButton.getLabel().setColor(Color.BLACK);
        this.openButton.getLabel().setColor(Color.BLACK);
        this.saveButton.getLabel().setColor(Color.BLACK);
        this.saveAsButton.getLabel().setColor(Color.BLACK);
        this.undoButton.getLabel().setColor(Color.BLACK);
        this.redoButton.getLabel().setColor(Color.BLACK);

        // Add listeners
        this.newButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                TileMap newMap = new TileMap(editor, "untitled " + untitledCount ++);
                editor.addToMaps(newMap);
                mapTabPane.lookAtMap(newMap);
            }
        });
        this.openButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                if(editor.fileChooserOpen)
                    return;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        editor.fileChooserOpen = true;
                        JFileChooser chooser = new JFileChooser();
                        FileNameExtensionFilter flmFilter = new FileNameExtensionFilter(
                                "flm files (*.flm)", "flm");
                        chooser.setFileFilter(flmFilter);
                        JFrame f = new JFrame();
                        f.setVisible(true);
                        f.setAlwaysOnTop(true);
                        f.toFront();
                        f.setVisible(false);
                        int res = chooser.showOpenDialog(f);
                        f.dispose();
                        editor.fileChooserOpen = false;
                        if (res == JFileChooser.APPROVE_OPTION)
                        {
                            Gdx.app.postRunnable(() ->
                            {
                                try
                                {
                                    File file = chooser.getSelectedFile();
                                    String content = new Scanner(file).useDelimiter("\\Z").next();
                                    Json json = new Json();
                                    TileMapData tileMapData = json.fromJson(TileMapData.class, content);
                                    TileMap newMap = new TileMap(editor, tileMapData);
                                    newMap.file = file;
                                    editor.addToMaps(newMap);
                                    mapTabPane.lookAtMap(newMap);
                                }
                                catch (FileNotFoundException e)
                                {
                                    e.printStackTrace();
                                }
                            });
                        }
                    }
                }).start();
            }
        });
        this.saveButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                if(editor.getScreen() != null)
                {
                    TileMap tileMap = (TileMap) editor.getScreen();
                    if(tileMap.file == null)
                    {
                        saveAs();
                        return;
                    }
                    TileMapData tileMapData = new TileMapData(tileMap);
                    Json json = new Json();

                    File file = tileMap.file;
                    try
                    {
                        //Create the file
                        if (file.createNewFile())
                            System.out.println("File is created!");
                        else
                            System.out.println("File already exists.");

                        //Write Content
                        FileWriter writer = new FileWriter(file);
                        writer.write(json.prettyPrint(tileMapData));
                        writer.close();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });
        this.saveAsButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                saveAs();
            }
        });
        this.undoButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                editor.undo();
            }
        });
        this.redoButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                editor.redo();
            }
        });

        // Add header and buttons to the buttonTable
        this.buttonTable = new Table();
        this.buttonTable.add(this.newButton);
        this.buttonTable.add(this.openButton);
        this.buttonTable.add(this.saveButton);
        this.buttonTable.add(this.saveAsButton);
        this.buttonTable.add(this.undoButton);
        this.buttonTable.add(this.redoButton);

        this.mapTabPane = new MapTabPane(editor, skin);
        this.toolPane = new ToolPane(editor, skin);

        this.fileMenuTable = new Table();
        this.fileMenuTable.add(this.buttonTable).row();
        this.fileMenuTable.add(this.mapTabPane).row();
        this.fileMenuTable.add(this.toolPane);
        this.addActor(this.fileMenuTable);
    }

    private void saveAs()
    {
        if(editor.fileChooserOpen || editor.getScreen() == null)
            return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                editor.fileChooserOpen = true;
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter flmFilter = new FileNameExtensionFilter(
                        "flm files (*.flm)", "flm");
                chooser.setFileFilter(flmFilter);
                TileMap tileMap = (TileMap) editor.getScreen();
                if(tileMap.file != null)
                    chooser.setSelectedFile(tileMap.file);
                else
                    chooser.setSelectedFile(new File("map.flm"));
                JFrame f = new JFrame();
                f.setVisible(true);
                f.setAlwaysOnTop(true);
                f.toFront();
                f.setVisible(false);
                int res = chooser.showSaveDialog(f);
                f.dispose();
                editor.fileChooserOpen = false;
                if (res == JFileChooser.APPROVE_OPTION)
                {
                    Gdx.app.postRunnable(() ->
                    {
                        tileMap.setName(chooser.getSelectedFile().getName());
                        TileMapData tileMapData = new TileMapData(tileMap);
                        Json json = new Json();

                        File file = chooser.getSelectedFile();
                        tileMap.file = file;
                        try
                        {
                            //Create the file
                            file.createNewFile();

                            //Write Content
                            FileWriter writer = new FileWriter(file);
                            writer.write(json.prettyPrint(tileMapData));
                            writer.close();
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    });
                }
            }
        }).start();
    }

    public void setSize(float width, float buttonHeight, float tabHeight, float toolHeight)
    {
        int buttonAmount = buttonTable.getCells().size;
        float buttonWidth = width / buttonAmount;
        this.buttonTable.getCell(this.newButton).size(buttonWidth, buttonHeight);
        this.buttonTable.getCell(this.openButton).size(buttonWidth, buttonHeight);
        this.buttonTable.getCell(this.saveButton).size(buttonWidth, buttonHeight);
        this.buttonTable.getCell(this.saveAsButton).size(buttonWidth, buttonHeight);
        this.buttonTable.getCell(this.undoButton).size(buttonWidth, buttonHeight);
        this.buttonTable.getCell(this.redoButton).size(buttonWidth, buttonHeight);
        this.buttonTable.invalidateHierarchy();

        this.mapTabPane.setSize(width, tabHeight);
        this.toolPane.setSize(width, toolHeight);

        this.fileMenuTable.invalidateHierarchy();

        super.setSize(width, buttonHeight + this.mapTabPane.getHeight() + this.toolPane.getHeight());

    }

    @Override
    public void setPosition (float x, float y)
    {
        super.setPosition(x + getWidth() / 2, y + getHeight() / 2);
    }
}
