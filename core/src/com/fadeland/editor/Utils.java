package com.fadeland.editor;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector3;

public class Utils
{
    private static RandomXS128 random = new RandomXS128();
    private static Vector3 unprojector = new Vector3();
    public static boolean print = true;
    public static void centerPrint(SpriteBatch batch, String string, float x, float y)
    {
        GameAssets.getGlyph().setText(GameAssets.getFont(), string);
        float w = GameAssets.getGlyph().width;
        float h = GameAssets.getGlyph().height;
        GameAssets.getFont().draw(batch, GameAssets.getGlyph(), x - w / 2, y + h / 2);
    }

    public static float degreeAngleFix(float angle)
    {
        angle = ((int) angle % 360) + (angle - ((int)angle));
        if(angle > 0.0)
            return angle;
        else
            return angle + 360f;
    }

    public static void print(String string)
    {
        if(print)
            System.out.print(string);
    }
    public static void println(String string)
    {
        if(print)
            System.out.println(string);
    }

    public static float radianAngleFix(float angle)
    {
        angle = (float) (((int) angle % (Math.PI * 2)) + (angle - ((int)angle)));
        if(angle > 0.0)
            return angle;
        else
            return (float) (angle + (Math.PI * 2));
    }

    public static float randomFloat(float minRange, float maxRange) { return minRange + random.nextFloat() * (maxRange - minRange); }

    public static int randomInt(int minRange, int maxRange) { return random.nextInt(maxRange - minRange + 1) + minRange; }

    public static float unprojectX(OrthographicCamera camera, float x, float y)
    {
        unprojector.set(x, y, 0);
        camera.unproject(unprojector);
        return unprojector.x;
    }

    public static float unprojectY(OrthographicCamera camera, float x, float y)
    {
        unprojector.set(x, y, 0);
        camera.unproject(unprojector);
        return unprojector.y;
    }

    public static Vector3 unproject(Camera camera, float x, float y)
    {
        unprojector.set(x, y, 0);
        camera.unproject(unprojector);
        return unprojector;
    }

    public static Vector3 project(Camera camera, float x, float y)
    {
        unprojector.set(x, y, 0);
        camera.project(unprojector);
        return unprojector;
    }

    public static int setBit(int bit, int target)
    {
        // Create mask
        int mask = 1 << bit;
        // Set bit
        return target | mask;
    }

    public static int turnBitOn(int value, int pos)
    {
        return value | (1 << pos);
    }
    public static int turnBitOff(int value, int pos)
    {
        return value & ~(1 << pos);
    }


}
