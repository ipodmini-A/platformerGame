package sunflowersandroses.platformergame.screens;

import sunflowersandroses.platformergame.*;
import sunflowersandroses.platformergame.console.ConsoleCommands;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import sunflowersandroses.platformergame.player.Player;

/**
 * GameScreen is the main platformer screen of the game.
 * TODO: Rename this class.
 */
public class GameScreen extends ScreenAdapter
{
    public static PlatformerGame game;
    private Stage stage;
    private SpriteBatch batch;
    protected World world;
    protected LevelManager levelManager;
    protected Player player;

    float x = 400f;
    float y = 1300f;
    private boolean firstSpawnCheck = false;
    public static InputMultiplexer multiplexer = new InputMultiplexer();

    public boolean playerWon = false;


    /**
     * Constructor to GameScreen. Accepts a PlatformerGame and assigns game, while creating a new SpriteBatch,
     * player and world.
     * @param game
     */
    public GameScreen(PlatformerGame game)
    {
        System.out.println("GameScreen created");
        GameScreen.game = game;
        batch = new SpriteBatch();

        // For some reason, when I add newPlatformerInput to the multiplexer before the console commands, it causes the
        // console commands to break. Not sure why but if you want a functional console on a specific screen, add it
        // to the multiplexer before everything else to ensure it doesn't get overridden.
        levelManager = new LevelManager();
        player = new Player(); // Initialize your player
        levelManager.loadLevel(player, game, 1); // Load the initial level
        //world = new World(player,game,"test1.tmx");
    }

    @Override
    public void show()
    {
        LevelManager.show();
        player.setDisableControls(false);
        if (firstSpawnCheck)
        {
            player.setPositionX(GameState.lastRecordedPlayerX);
            player.setPositionY(GameState.lastRecordedPlayerY);
        }

        // Resetting players movement
        player.setDownMove(false);
        player.setLeftMove(false);
        player.setRightMove(false);

        // Resetting players velocity
        player.setVelocity(0,0);

        firstSpawnCheck = true;
        super.show();
    }

    @Override
    public void hide()
    {
        GameState.gameScreen = game.getScreen();
        GameState.lastRecordedPlayerX = player.getPosition().x;
        System.out.println(GameState.lastRecordedPlayerX);
        GameState.lastRecordedPlayerY = player.getPosition().y;
        System.out.println(GameState.lastRecordedPlayerY);
        System.out.println("Hidden");
    }

    /**
     * render Renders the images on screen. First by clearing the screen, then running render from different classes
     * In this method you will also find game objectives, such as touching the end goal or touching an enemy.
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render(float delta)
    {
        levelManager.render(delta);

        if (player.menuPressed())
        {
            game.setScreen(new TitleScreen(game));
        }

        /*
        Following code stores the state of the game screen and the player.
        TODO: Put into a method
         */

        // This code takes the player to a battle screen when they press I. I don't know if I am going to keep this.
        /*
        if (Gdx.input.isKeyPressed(Input.Keys.I))
        {
            GameState.gameScreen = game.getScreen();
            player.setDisableControls(true);
            GameState.lastRecordedPlayerX = player.getPosition().x;
            System.out.println(GameState.lastRecordedPlayerX);
            GameState.lastRecordedPlayerY = player.getPosition().y;
            System.out.println(GameState.lastRecordedPlayerY);
            world.music.pause();
            game.setScreen(new BattleScreen(game,player,world.enemy));
        }

         */

        //System.out.println(player.isTouchingWall());
        //System.out.println(player.isTouchingWall());

        ConsoleCommands.draw(); // For some reason enter doesn't work.
    }

    @Override
    public void dispose()
    {
        batch.dispose();
        player.dispose();
        //world.dispose();
    }
}
