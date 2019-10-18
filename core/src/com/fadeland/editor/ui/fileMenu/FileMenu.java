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
import com.fadeland.editor.GameAssets;
import com.fadeland.editor.map.TileMap;
import com.fadeland.editor.map.mapdata.*;
import com.fadeland.editor.ui.AreYouSureDialog;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import static com.fadeland.editor.FadelandEditor.prefs;
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
    private TextButton saveFLMDefaultsButton;
    private TextButton setFLMDefaultsButton;
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
        this.saveFLMDefaultsButton = new TextButton("Save FLM Defaults", skin);
        this.setFLMDefaultsButton = new TextButton("Set FLM Defaults", skin);
        this.undoButton = new TextButton("Undo", skin);
        this.redoButton = new TextButton("Redo", skin);

        // Buttons text color
        this.newButton.getLabel().setColor(Color.BLACK);
        this.openButton.getLabel().setColor(Color.BLACK);
        this.saveButton.getLabel().setColor(Color.BLACK);
        this.saveAsButton.getLabel().setColor(Color.BLACK);
        this.saveFLMDefaultsButton.getLabel().setColor(Color.BLACK);
        this.setFLMDefaultsButton.getLabel().setColor(Color.BLACK);
        this.undoButton.getLabel().setColor(Color.BLACK);
        this.redoButton.getLabel().setColor(Color.BLACK);

        // Add listeners
        this.newButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                new AreYouSureDialog("Create a new map with FLM default settings/properties?", editor.stage, "", GameAssets.getUISkin(), true)
                {
                    TileMap newMap;
                    @Override
                    public void yes()
                    {
                        try
                        {
                            File file = new File("defaultFLM.flm");
                            String content = null;
                            content = new Scanner(file).useDelimiter("\\Z").next();
                            Json json = createJson();
                            TileMapData tileMapData = json.fromJson(TileMapData.class, content);
                            tileMapData.layers.clear();
                            TileMap newMap = new TileMap(editor, tileMapData);
                            newMap.file = file;
                            editor.addToMaps(newMap);
                            mapTabPane.lookAtMap(newMap);
                        }
                        catch (FileNotFoundException e)
                        {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void no()
                    {
                        newMap = new TileMap(editor, "untitled " + untitledCount++);
                        editor.addToMaps(newMap);
                        mapTabPane.lookAtMap(newMap);
                    }
                };
            }
        });
        this.openButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                if (editor.fileChooserOpen)
                    return;
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        editor.fileChooserOpen = true;
                        JFileChooser chooser = new JFileChooser();
                        FileNameExtensionFilter flmFilter = new FileNameExtensionFilter(
                                "flm files (*.flm)", "flm");
                        chooser.setFileFilter(flmFilter);
                        String path = prefs.getString("lastSave", "null");
                        if(!path.equals("null"))
                            chooser.setSelectedFile(new File(path));
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
                                    Json json = createJson();
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
                    save((TileMap) editor.getScreen(), false, false);
            }
        });
        this.saveAsButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                if(editor.getScreen() != null)
                    saveAs((TileMap) editor.getScreen(), false, false);
            }
        });
        this.saveFLMDefaultsButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                if(editor.getScreen() != null)
                {
                    TileMap map = (TileMap) editor.getScreen();
                    new AreYouSureDialog("Override and save new FLM default properties?", editor.stage, "", GameAssets.getUISkin(), true)
                    {
                        @Override
                        public void yes()
                        {
                            fadelandEditor.fileMenu.saveFLMDefaults(map);
                        }

                        @Override
                        public void no()
                        {
                        }
                    };
                }
            }
        });
        this.setFLMDefaultsButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                if(editor.getScreen() != null)
                {
                    TileMap map = (TileMap) editor.getScreen();
                    new AreYouSureDialog("Override and set FLM properties to default for this map?", editor.stage, "", GameAssets.getUISkin(), true)
                    {
                        @Override
                        public void yes()
                        {
                            fadelandEditor.fileMenu.setFLMDefaults(map);
                        }

                        @Override
                        public void no()
                        {
                        }
                    };
                }
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
        this.buttonTable.add(this.saveFLMDefaultsButton);
        this.buttonTable.add(this.setFLMDefaultsButton);
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

    public void save(TileMap tileMap, boolean removeMapAfterSaving, boolean closeApplicationAfterSaving)
    {
        tileMap.searchForBlockedTiles();
        if (tileMap.file == null)
        {
            saveAs(tileMap, removeMapAfterSaving, closeApplicationAfterSaving);
            return;
        }
        TileMapData tileMapData = new TileMapData(tileMap, false);

        Json json = createJson();

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

            tileMap.setChanged(false);

            if(removeMapAfterSaving)
                editor.fileMenu.mapTabPane.removeMap(tileMap);
            if(closeApplicationAfterSaving)
                Gdx.app.exit();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void saveFLMDefaults(TileMap tileMap)
    {
        TileMapData tileMapData = new TileMapData(tileMap, true);

        Json json = createJson();

        File file = new File("defaultFLM.flm");
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

    public void setFLMDefaults(TileMap tileMap)
    {
        try
        {
            File file = new File("defaultFLM.flm");
            String content = null;
            content = new Scanner(file).useDelimiter("\\Z").next();
            Json json = createJson();
            TileMapData tileMapData = json.fromJson(TileMapData.class, content);
            tileMap.setMapPropertiesAndObjects(tileMapData, true);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    public void saveAs(TileMap tileMap, boolean removeMapAfterSaving, boolean closeApplicationAfterSaving)
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
                TileMap map = tileMap;
                if(map.file != null)
                    chooser.setSelectedFile(map.file);
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
                        map.setName(chooser.getSelectedFile().getName());
                        TileMapData tileMapData = new TileMapData(map, false);
                        Json json = createJson();

                        File file = chooser.getSelectedFile();
                        map.file = file;
                        try
                        {
                            //Create the file
                            file.createNewFile();
                            prefs.putString("lastSave", file.getAbsolutePath());
                            prefs.flush();

                            //Write Content
                            FileWriter writer = new FileWriter(file);
                            writer.write(json.prettyPrint(tileMapData));
                            writer.close();

                            tileMap.setChanged(false);

                            if(removeMapAfterSaving)
                                editor.fileMenu.mapTabPane.removeMap(tileMap);
                            if(closeApplicationAfterSaving)
                                Gdx.app.exit();
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
        this.buttonTable.getCell(this.saveFLMDefaultsButton).size(buttonWidth, buttonHeight);
        this.buttonTable.getCell(this.setFLMDefaultsButton).size(buttonWidth, buttonHeight);
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

    private Json createJson()
    {
        Json json = new Json();
        json.addClassTag("colorPropertyData", ColorPropertyData.class);
        json.addClassTag("layerData", LayerData.class);
        json.addClassTag("lightPropertyData", LightPropertyData.class);
        json.addClassTag("mapObjectData", MapObjectData.class);
        json.addClassTag("mapObjectLayerData", MapObjectLayerData.class);
        json.addClassTag("mapPointData", MapPointData.class);
        json.addClassTag("mapPolygonData", MapPolygonData.class);
        json.addClassTag("mapSpriteData", MapSpriteData.class);
        json.addClassTag("mapSpriteLayerData", MapSpriteLayerData.class);
        json.addClassTag("mapTileData", MapTileData.class);
        json.addClassTag("nonColorPropertyData", NonColorPropertyData.class);
        json.addClassTag("propertyData", PropertyData.class);
        json.addClassTag("tileGroupData", TileGroupData.class);
        json.addClassTag("tileLayerData", TileLayerData.class);
        json.addClassTag("toolData", ToolData.class);
        json.addClassTag("sheetData", SheetData.class);
        json.addClassTag("tileSheetData", TileSheetData.class);
        json.addClassTag("spriteSheetData", SpriteSheetData.class);
        return json;
    }
}
