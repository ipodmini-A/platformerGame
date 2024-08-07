package sunflowersandroses.platformergame.screens;

import sunflowersandroses.platformergame.GameState;
import sunflowersandroses.platformergame.NewPlatformerInput;
import sunflowersandroses.platformergame.PlatformerGame;
import sunflowersandroses.platformergame.player.Player;
import sunflowersandroses.platformergame.console.ConsoleCommands;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MainMenuScreen extends ScreenAdapter {
    private Stage stage;
    private Skin skin;
    private Viewport viewport;
    private PlatformerGame game;

    private Table mainMenuTable;
    private Table optionsTable;
    public static boolean debug = true;
    InputMultiplexer multiplexer = new InputMultiplexer();

    public MainMenuScreen(PlatformerGame game) {
        this.game = game;
        viewport = new FillViewport(1280,720);
        stage = new Stage(viewport);

        ConsoleCommands consoleCommands = new ConsoleCommands(viewport);

        multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(ConsoleCommands.getConsole().getInputProcessor());
        multiplexer.addProcessor(new NewPlatformerInput());
        multiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(multiplexer);

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        mainMenuTable = new Table();
        mainMenuTable.setFillParent(true);
        stage.addActor(mainMenuTable);
        stage.setDebugAll(debug);

        Label titleLabel = new Label("Main Menu", skin, "title");
        mainMenuTable.add(titleLabel).padBottom(50f).row();

        TextButton playButton = new TextButton("Play", skin);
        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new GameScreen(game));
                dispose();
            }
        });
        mainMenuTable.add(playButton).padBottom(20f).row();

        TextButton optionsButton = new TextButton("Options (In Development)", skin);
        optionsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showOptionsScreen();
            }
        });
        mainMenuTable.add(optionsButton).padBottom(20f).row();

        TextButton slots = new TextButton("Slots", skin);
        slots.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new SlotsScreen(game, new Player()));
            }
        });
        mainMenuTable.add(slots).padBottom(20f).row();

        TextButton exitButton = new TextButton("Exit", skin);
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
        mainMenuTable.add(exitButton);
    }

    private void showOptionsScreen() {
        optionsTable = new Table();
        optionsTable.setFillParent(true);
        stage.clear();
        stage.addActor(optionsTable);

        Label titleLabel = new Label("Options (Currently in Development)", skin, "title");
        optionsTable.add(titleLabel).padBottom(50f).row();

        Label resolutionLabel = new Label("Resolution (Can cause UI issues) :", skin);
        optionsTable.add(resolutionLabel).padBottom(20f).row();

        final SelectBox<String> resolutionSelectBox = new SelectBox<>(skin);
        resolutionSelectBox.setItems("720x480","1280x720", "1920x1080", "2560x1440");
        optionsTable.add(resolutionSelectBox).padBottom(20f).row();

        Label FPSLabel = new Label("FPS (Can cause rendering issues) :", skin);
        optionsTable.add(FPSLabel).padBottom(20f).row();

        final SelectBox<String> FPSLabelBox = new SelectBox<>(skin);
        FPSLabelBox.setItems("60","90", "120", "160");
        optionsTable.add(FPSLabelBox).padBottom(20f).row();

        TextButton applyButton = new TextButton("Apply", skin);
        applyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String selectedResolution = resolutionSelectBox.getSelected();
                String selectedFPS = FPSLabelBox.getSelected();
                String[] parts = selectedResolution.split("x");

                int width = Integer.parseInt(parts[0]);
                int height = Integer.parseInt(parts[1]);

                int FPSint = Integer.parseInt(selectedFPS);

                int targetWidth = 1280;  // The target width for scaling
                int targetHeight = 720;  // The target height for scaling

                float scaleX = (float) width / targetWidth;
                float scaleY = (float) height / targetHeight;

                float scale = Math.min(scaleX, scaleY);

                int scaledWidth = (int) (targetWidth * scale);
                int scaledHeight = (int) (targetHeight * scale);

                //Gdx.graphics.setWindowedMode(scaledWidth, scaledHeight);
                stage.getViewport().update(scaledWidth, scaledHeight, true);
                game.resize(scaledWidth,scaledHeight);
                stage.draw();

                Gdx.graphics.setForegroundFPS(FPSint);
            }

        });


        optionsTable.add(applyButton).padBottom(20f).row();

        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showMainMenuScreen();
            }
        });
        optionsTable.add(backButton);
    }

    private void showMainMenuScreen() {
        stage.clear();
        stage.addActor(mainMenuTable);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(multiplexer);
        super.show();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
        ConsoleCommands.draw();
    }

    @Override
    public void resize(int width, int height) {
        ConsoleCommands.resize(width, height);
        //stage.getViewport().update(width, height, true);
        viewport.apply();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        GameState.mainMenuScreen = game.getScreen();
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}



