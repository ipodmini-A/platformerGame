package cchase.platformergame;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;

public class Player
{
    private static final float GRAVITY = -1000f; // Adjust the gravity value as needed -1000f
    private static final float JUMP_VELOCITY = 450f; // Adjust the jump velocity as needed
    protected static final float HEIGHT = 60f;
    protected static final float WIDTH = 30f;
    protected static float MAX_VELOCITY = 150f;
    private static float SCALE = 1f;
    private float health;
    final HashMap<String, Sprite> sprites = new HashMap<String, Sprite>();

    private Texture texture;
    protected Sprite sprite;
    protected Vector2 position;
    protected Vector2 velocity;
    protected Rectangle bounds;
    private PlatformerInput platformerInput;
    private boolean grounded;
    private boolean touchingLeftWall;
    private boolean touchingRightWall;
    private boolean touchingWall;
    private boolean touchingCeiling;
    private boolean flying;
    protected OrthographicCamera camera;
    protected SpriteBatch spriteBatch;
    private TextureAtlas textureAtlas;
    private boolean disableControls;
    enum State
    {
        STANDING, WALKING, JUMPING
    }
    protected State state;
    protected boolean facingRight = false;

    /**
     * Default constructor. The location of the player is set to 0,0
     */
    public Player()
    {
        position = new Vector2(0,0);
        velocity = new Vector2();
        grounded = false;
        health = 100f;
        touchingCeiling = false;
        touchingLeftWall = false;
        touchingRightWall = false;
        touchingWall = false;
        flying = false;
        platformerInput = new PlatformerInput();
        disableControls = false;
        bounds = new Rectangle(position.x, position.y, WIDTH, HEIGHT);
        bounds.setSize(WIDTH, HEIGHT); // Update the bounds size
        state = State.STANDING;

        textureAtlas = new TextureAtlas("sprites.txt");
        spriteBatch = new SpriteBatch();
        texture = new Texture("debugSquare.png");
        sprite = new Sprite(texture);

        addSprites();
        sprite.setSize(WIDTH,HEIGHT);

        System.out.println("Width: " + sprite.getWidth() + " Height: " + sprite.getHeight());
    }

    /**
     * This constructor accepts a x and y value, which determines where the player is placed.
     * @param x x coordinate
     * @param y y coordinate
     */
    public Player(float x, float y)
    {
        position = new Vector2(x, y);
        velocity = new Vector2();
        grounded = false;
        health = 100f;
        touchingCeiling = false;
        touchingLeftWall = false;
        touchingRightWall = false;
        touchingWall = false;
        flying = false;
        platformerInput = new PlatformerInput();
        bounds = new Rectangle(position.x, position.y, WIDTH, HEIGHT);
        bounds.setSize(WIDTH, HEIGHT); // Update the bounds size
        state = State.STANDING;

        textureAtlas = new TextureAtlas("sprites.txt");
        spriteBatch = new SpriteBatch();
        texture = new Texture("debugSquare.png");
        sprite = new Sprite(texture);

        addSprites();
        sprite.setSize(WIDTH,HEIGHT);

        System.out.println("Width: " + sprite.getWidth() + " Height: " + sprite.getHeight());
    }

    /**
     * input() controls the input for the player.
     *
     * TODO: isDownPressed doesn't do anything.
     */
    public void input()
    {
        platformerInput.update();
        if (!disableControls)
        {
            if (platformerInput.isLeftPressed())
            {
                if (velocity.x >= -1 * MAX_VELOCITY)
                {
                    velocity.x -= 5;
                } else
                {
                    velocity.x = -150;
                }
            }

            if (platformerInput.isRightPressed())
            {
                if (velocity.x <= MAX_VELOCITY)
                {
                    velocity.x += 5;
                } else
                {
                    velocity.x = 150;
                }
            }

            if (platformerInput.isUpPressed() && !flying)
            {
                jump();
            }

            if (platformerInput.isDownPressed())
            {
                velocity.y -= 5;
            }
        }
        flying = platformerInput.isDebugPressed();
        //System.out.println(flying);
    }

    /**
     * render is called every frame. Render should be used while the player is in a level.
     *
     * @param spriteBatch
     * @param delta
     */
    public void render(SpriteBatch spriteBatch,float delta)
    {
        this.spriteBatch = spriteBatch;
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        input();
        update(delta);
        renderMovement();
        //drawSprite("standing", position.x, position.y);
        spriteBatch.end();
        //System.out.println("Sprite X:" + sprite.getX() + " Sprite Y:" + sprite.getY());
        //System.out.println("Bounding X:" + bounds.getX() + " Bounding Y:" + bounds.getY());
    }

    /**
     * renderMovement() controls movement and will display the correct sprite depending on what action is being performed
     * The method uses drawSprite(), and changes by using the enum State.
     */
    public void renderMovement()
    {
        if (velocity.x < 0)
        {
            facingRight = true;
        } else if (velocity.x > 0)
        {
            facingRight = false;
        }
        if (state == State.STANDING)
        {
            drawSprite("standing", position.x, position.y);
        } else if (state == State.WALKING)
        {
            drawSprite("running", position.x, position.y);
        } else if ( state == State.JUMPING)
        {
            drawSprite("jumping", position.x, position.y);
        }
    }

    /**
     * renderBattle is a render screen that is used for the battle screen.
     * When in battle the player cannot move and they are locked into the standing animation (for now... until I
     * figure out what animation is)
     *
     * @param spriteBatch SpriteBatch
     * @param delta float
     * @param scale float, how large the sprites will be rendered.
     */
    public void renderBattle(SpriteBatch spriteBatch,float delta, float scale)
    {
        this.spriteBatch = spriteBatch;
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        input();
        updateBattle(delta, scale);
        drawSpriteBattle("standing", position.x, position.y, scale);
        spriteBatch.end();
    }

    /**
     * Allows the player to jump. If they are grounded, first the player is moved up one pixel, then jump velocity is applied
     */
    public void jump()
    {
        if (grounded)
        {
            position.y += 1;
            velocity.y = JUMP_VELOCITY;
            grounded = false;
        }
    }

    public Vector2 getPosition()
    {
        return position;
    }

    public void setVelocity(float x, float y)
    {
        velocity.set(x, y);
    }

    public Vector2 getVelocity()
    {
        return velocity;
    }

    public Rectangle getBounds()
    {
        return bounds;
    }

    public void updateCamera(OrthographicCamera camera)
    {
        this.camera = camera;
    }

    /**
     * Spaghetti code :^)
     *
     * Updates the player each frame, which in this case is delta.
     *
     * First thing that is checked is input, next bounding box and sprite is edited.
     * UPDATE: All code relating to collision was moved to World.java.
     * TODO: Refactor sprite so that it doesn't look horrible
     *
     * Afterwards, collision code is checked.
     *
     * @param delta
     */
    public void update(float delta)
    {
        sprite.setBounds(
                position.x,
                position.y,
                WIDTH,
                HEIGHT);
        // Update position based on velocity
        bounds.setPosition(position.x, position.y); // Update the bounds with the new position
        //position.add(velocity.x * delta, velocity.y * delta);
        if (grounded && velocity.x == 0)
        {
            state = State.STANDING;
        }else if (grounded && velocity.x != 0)
        {
            state = State.WALKING;
        }else if (!grounded)
        {
            state = State.JUMPING;
        }
    }

    public void updateBattle(float delta, float scale)
    {
        sprite.setBounds(
                position.x,
                position.y,
                WIDTH * scale,
                HEIGHT * scale);
        // Update position based on velocity
        bounds.setPosition(position.x, position.y); // Update the bounds with the new position
        facingRight = false;
        state = State.STANDING;
    }

    /**
     * Adds sprites to the hashmap
     */
    private void addSprites()
    {
        Array<TextureAtlas.AtlasRegion> regions = textureAtlas.getRegions();

        for (TextureAtlas.AtlasRegion region : regions) {
            Sprite sprite = textureAtlas.createSprite(region.name);

            sprites.put(region.name, sprite);
        }
    }

    /**
     * Draws the sprite on screen.
     * TODO: Currently messing around with bounds and sprite bounds. Create a variable dedicated to the sprite bounds.
     * @param name The name is the hash map key.
     * @param x X position of the sprite
     * @param y Y position of the sprite
     */
    private void drawSprite(String name, float x, float y)
    {
        Sprite sprite = sprites.get(name);

        sprite.setBounds(x - (WIDTH / 2f) - 5f,y,(WIDTH * 2f) + 10f,HEIGHT + 10f);

        if (facingRight && sprite.isFlipX())
        {
            sprite.flip(true,false);
        } else if (!facingRight && !sprite.isFlipX())
        {
            sprite.flip(true, false);
        }
        sprite.draw(spriteBatch);
    }

    /**
     * drawSpriteBattle draws the sprite on screen inside of a battle screen.
     * Going to be honest... I made this method so that I can scale up the sprite, and in hindsight I could have just
     * overloaded drawSprite.
     * @param name
     * @param x
     * @param y
     * @param scale
     */
    protected void drawSpriteBattle(String name, float x, float y, float scale)
    {
        Sprite sprite = sprites.get(name);

        sprite.setBounds(x,y,(WIDTH + 10f) * scale,(HEIGHT + 10f) * scale);

        if (facingRight && sprite.isFlipX())
        {
            sprite.flip(true,false);
        } else if (!facingRight && !sprite.isFlipX())
        {
            sprite.flip(true, false);
        }
        sprite.draw(spriteBatch);
    }


    public boolean isGrounded()
    {
        return grounded;
    }

    public void setGrounded(boolean grounded)
    {
        this.grounded = grounded;
    }

    public boolean isTouchingWall()
    {
        return touchingWall;
    }

    public void setTouchingWall(boolean touchingWall)
    {
        this.touchingWall = touchingWall;
    }

    public boolean isTouchingLeftWall()
    {
        return touchingLeftWall;
    }

    public void setTouchingLeftWall(boolean touchingLeftWall)
    {
        this.touchingLeftWall = touchingLeftWall;
    }

    public boolean isTouchingRightWall()
    {
        return touchingRightWall;
    }

    public void setTouchingRightWall(boolean touchingRightWall)
    {
        this.touchingRightWall = touchingRightWall;
    }

    public boolean isTouchingCeiling()
    {
        return touchingCeiling;
    }

    public void setTouchingCeiling(boolean touchingCeiling)
    {
        this.touchingCeiling = touchingCeiling;
    }

    public void setSCALE(float f)
    {
        SCALE = f;
    }

    public float getHeight()
    {
        return HEIGHT;
    }

    public float getWidth()
    {
        return WIDTH;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public void setPositionX(float x)
    {
        position.x = x;
    }

    public void setPositionY(float y)
    {
        position.y = y;
    }

    public PlatformerInput getPlatformerInput()
    {
        return platformerInput;
    }

    public void setPlatformerInput(PlatformerInput platformerInput)
    {
        this.platformerInput = platformerInput;
    }

    public float getHealth()
    {
        return health;
    }

    public void setHealth(float health)
    {
        this.health = health;
    }

    public boolean isDisableControls()
    {
        return disableControls;
    }

    public void setDisableControls(boolean disableControls)
    {
        this.disableControls = disableControls;
    }

    public static float getMaxVelocity()
    {
        return MAX_VELOCITY;
    }

    public void setBounds(Rectangle bounds)
    {
        this.bounds = bounds;
    }

    public static void setMaxVelocity(float maxVelocity)
    {
        MAX_VELOCITY = maxVelocity;
    }

    public void dispose()
    {
        texture.dispose();
        textureAtlas.dispose();
        spriteBatch.dispose();
    }
}

