package cchase.platformergame.console;

import cchase.platformergame.NewPlatformerInput;
import cchase.platformergame.SlotsGame;
import cchase.platformergame.World;
import cchase.platformergame.screens.MainMenuScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.strongjoshua.console.CommandExecutor;
import com.strongjoshua.console.Console;
import com.strongjoshua.console.GUIConsole;

public class ConsoleCommands extends CommandExecutor {

    private static GUIConsole console;

    public ConsoleCommands(){

        Skin skin = new Skin(Gdx.files.internal("ui/uiskin.json"), new TextureAtlas(Gdx.files.internal("ui/uiskin.atlas")));
        console = new GUIConsole(skin, false, Input.Keys.SEMICOLON);
        console.setCommandExecutor(this);
        console.setSize(Gdx.graphics.getWidth()/4, Gdx.graphics.getHeight()/2);
        console.setPosition(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        console.setSizePercent(30, 50);
        console.enableSubmitButton(true);
    }

    public static Console getConsole(){
        return console;
    }

    public static void draw(){
        if (console.isVisible()) {
            console.draw();
        }
    }

    //                  //
    // Console Commands //
    //                  //

    public void test(){
        console.log("Hi! I'm your friendly console");
    }

    public void fps()
    {
        console.log(String.valueOf(Gdx.graphics.getFramesPerSecond()));
    }

    public void debug(boolean d)
    {
        World.debug = d;
        NewPlatformerInput.debug = d;
        MainMenuScreen.debug = d;
        console.log("Debug has been set to " + d);
    }

    public void changeSlotChance(int i)
    {
        SlotsGame.randomCap = i;
        console.log("Cap has been set to " + i);
    }

    public void hide(){
        console.setVisible(false);
    }
}