package cchase.platformergame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.Timer;

/**
 * GameState serves the purpose of saving screens.
 * Will be renamed to Game manager depending on how things progress
 */
public class GameState
{
    public static Screen gameScreen;
    public static Screen mainMenuScreen;
    public static float lastRecordedPlayerX;
    public static float lastRecordedPlayerY;
}
