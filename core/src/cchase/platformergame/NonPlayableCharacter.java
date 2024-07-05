package cchase.platformergame;

import cchase.platformergame.screens.GameScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;

import java.util.LinkedList;

public class NonPlayableCharacter extends Player {
    private boolean touchingPlayer = false;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;
    private BitmapFont font;
    private Skin skin;
    protected Stage stage;
    private Player player;
    private LinkedList<String> messageList;
    private TypingLabel typingLabel;
    private int messageIndex;
    private boolean displayMessage;
    private Window dialogueBox;
    private TextButton nextButton;

    public NonPlayableCharacter(float x, float y) {
        super(x, y); // NonPlayableCharacter inherits everything from Player.java at first. Things such as sprites.
        textureAtlas = new TextureAtlas("npcsprites.txt");
        addSprites();
        font = new BitmapFont();
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        stage = new Stage();
        //stage.setDebugAll(true);
        shapeRenderer = new ShapeRenderer();
        spriteBatch = new SpriteBatch();
        messageIndex = 0;
        messageList = new LinkedList<>();
        messageList.add("Hi!");
        messageList.add("I'm a generic NPC!");
        messageList.add("I can't really move yet but hopefully in the future I gain that ability");
        messageList.add("Goodbye!");
        GameScreen.multiplexer.addProcessor(stage);

        facingRight = true;

        typingLabel = new TypingLabel("", skin);

        // Create and set up the dialogue box
        dialogueBox = new Window("", skin);
        dialogueBox.setSize(500, 200);
        dialogueBox.setPosition(Gdx.graphics.getWidth() / 2f - 200, 50);
        dialogueBox.add(typingLabel).width(380).pad(10).row();
        typingLabel.setWrap(true);

        // Create the next button
        nextButton = new TextButton("Next", skin);
        nextButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                if (player.isDisplayMessage())
                {
                    player.setNextMessage(true);
                }
                /*
                if (player != null && player.isNextMessage()) {
                    player.setNextMessage(false);
                    messageIndex++;

                    if (messageIndex < messageList.size()) {
                        typingLabel.restart();
                        typingLabel.setText(messageList.get(messageIndex));
                    } else {
                        // All messages have been displayed
                        player.setDisableControls(false);
                        dialogueBox.setVisible(false);
                    }
                }

                 */
            }
        });

        dialogueBox.add(nextButton).pad(10);
        stage.addActor(dialogueBox);
        dialogueBox.setVisible(false); // Initially hidden
    }

    @Override
    public void input() {
        // Currently this is here to override input.
        position.add(velocity.x * dt, velocity.y * dt);
    }

    public void Message(Player player) {
        this.player = player;
        player.getVelocity().x = 0;
        if (!player.facingRight) {
            facingRight = true;
            //player.setPositionX(position.x - 28f);
            player.setPositionX(position.x + getWidth() - 2f);
            //player.facingRight = true;
        } else
        {
            facingRight = false;
            player.setPositionX(position.x - 28f);
        }

        if (touchingPlayer) {
            if (messageIndex >= messageList.size()) {
                // All messages have been displayed
                player.setDisableControls(false);
                resetDialogue();
            } else {
                disablePlayerInput();

                if (!dialogueBox.isVisible()) {
                    dialogueBox.setVisible(true);
                    typingLabel.restart();
                    typingLabel.setText(messageList.get(messageIndex));
                }

                stage.act(Gdx.graphics.getDeltaTime());
                stage.draw();

                if (player.isNextMessage()) {
                    player.setNextMessage(false);
                    messageIndex++;

                    if (messageIndex < messageList.size()) {
                        typingLabel.restart();
                        typingLabel.setText(messageList.get(messageIndex));
                    } else {
                        // All messages have been displayed
                        player.setDisableControls(false);
                        dialogueBox.setVisible(false);
                    }
                }
            }
        } else {
            // Player is not touching the NPC
            resetDialogue();
            player.setDisableControls(false);
        }
    }

    private void resetDialogue() {
        messageIndex = 0;
        if (player != null) {
            player.setNextMessage(false);
            player.setDisplayMessage(false);
        }
        displayMessage = false;
        dialogueBox.setVisible(false);
    }

    private void addSprites() {
        Array<TextureAtlas.AtlasRegion> regions = textureAtlas.getRegions();

        for (TextureAtlas.AtlasRegion region : regions) {
            Sprite sprite = textureAtlas.createSprite(region.name);
            sprites.put(region.name, sprite);
        }
    }

    public void removeAllMessages()
    {
        messageList = new LinkedList<String>();
    }

    public void disablePlayerInput() {
        player.setDisableControls(true);
    }
    //                      //
    // Setters and Getters  //
    //                      //


    public LinkedList<String> getMessageList() {
        return messageList;
    }

    public void setMessageList(LinkedList<String> messageList) {
        this.messageList = messageList;
    }

    public boolean isTouchingPlayer() {
        return touchingPlayer;
    }

    public void setTouchingPlayer(boolean touchingPlayer) {
        this.touchingPlayer = touchingPlayer;
    }

    public boolean isDisplayMessage() {
        return displayMessage;
    }

    public void setDisplayMessage(boolean displayMessage) {
        this.displayMessage = displayMessage;
    }
}
