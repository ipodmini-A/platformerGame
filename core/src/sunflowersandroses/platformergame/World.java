package sunflowersandroses.platformergame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import sunflowersandroses.platformergame.items.Item;
import sunflowersandroses.platformergame.player.Player;

import java.util.LinkedList;

/**
 * This class is a mess, im not even joking
 * TODO: Clean up
 * The World class contains the details in the world and how the player along with enemies interact with it.
 * This class is intended to serve as a template for levels to be created from. As this class gets more refined, more
 * documentation will be added.
 * Key words to know from the map file
 * tiles: The visual tiles (Not used except for rendering)
 * endgoal: The end goal of the game.
 * collision: The collision of the map.
 */
public class World {
    //private TiledMapRenderer mapRenderer; // What does this do?
    private static final float GRAVITY = -1000f; // Adjust the gravity value as needed -1000f
    private static final float MAX_FALL = -500f;
    //private static final float JUMP_VELOCITY = 400f; // Adjust the jump velocity as needed
    private static final float SCALE = 2f;
    private static final float FRICTION = 5f;
    private final OrthographicCamera camera;
    protected Player player;
    protected PlatformerGame game;
    // When creating a new level, each collectable will be added to this linked list.
    // Each item in the linked list has its coordinates
    protected LinkedList<Item> items;
    protected LinkedList<Enemy> enemies;
    protected LinkedList<NonPlayableCharacter> nonPlayableCharacters;
    private final TiledMap map;
    private MapProperties mapProperties;
    private final OrthogonalTiledMapRenderer mapRenderer;
    private final MapObjects objects;
    private final MapObjects endGameObject;
    protected LinkedList<MapObject> loadingZones;
    protected MapLayer collisionLayer;
    protected MapLayer endGoalLayer;
    protected MapLayer playerSpawnPoint;
    protected MapLayer loadingZoneLayer;
    public static boolean debug = true;
    private SpriteBatch spriteBatch;
    private final ShapeRenderer debugRenderer;
    private final BitmapFont debugFont;
    private final SpriteBatch debugBatch;
    private Texture backgroundTexture;
    private Sprite backgroundSprite;
    private boolean playerReachedEnd = false;
    // TODO: Implement a proper debug tool.

    public World(Player player, PlatformerGame game, String mapName) {
        // Sound creation

        // Game context
        this.game = game;

        // Map creation
        TmxMapLoader loader = new TmxMapLoader();
        map = loader.load(mapName);
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1);
        mapProperties = map.getProperties();

        //sprite batch creation
        spriteBatch = new SpriteBatch();

        // Layers from the map that was spawned above.
        collisionLayer = map.getLayers().get("collision");
        endGoalLayer = map.getLayers().get("endgoal");
        playerSpawnPoint = map.getLayers().get("playerSpawn");
        objects = collisionLayer.getObjects();
        endGameObject = endGoalLayer.getObjects();

        try {
            loadingZones = new LinkedList<>();
            loadingZoneLayer = map.getLayers().get("loadingZones");
            for (int i = 0; i < loadingZoneLayer.getObjects().getCount(); i++) {
                loadingZones.add(loadingZoneLayer.getObjects().get(i));
            }
        } catch (Exception e)
        {
            System.err.println(e.getMessage());
            System.err.println("Loading Zones not present.");
        }

        MapObjects playerSpawnPointObject = playerSpawnPoint.getObjects();

        // Camera creation
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 480, 270);
        Viewport viewport = new FitViewport(480, 270, camera);
        viewport.apply();
        camera.update();
        mapRenderer.setView(camera);

        // Background Creation
        backgroundTexture = new Texture("background.png");
        backgroundSprite = new Sprite(backgroundTexture);
        backgroundSprite.setSize(viewport.getWorldWidth(), viewport.getWorldHeight());
        backgroundSprite.setPosition(0, 0); // Adjust as needed

        //TODO: Move playerSpawn here.
        //I LOVE JAVA I LOVE OOP I LOVE 500 GETS

        float xSpawnPoint = playerSpawnPointObject.get(0).getProperties().get("x", Float.class);
        float ySpawnPoint = playerSpawnPointObject.get(0).getProperties().get("y", Float.class);
        player.setPositionX(xSpawnPoint);
        if (debug)
        {
            System.out.println("Player Spawn Location: x: " + xSpawnPoint + " Y: " + ySpawnPoint);
        }
        player.setPositionY(ySpawnPoint);

        // Sets the size of the player. Going to be honest, forgot what this does.
        player.setSCALE(SCALE);

        // Enemy creation
        enemies = new LinkedList<>();

        //NPC creation
        //nonPlayableCharacter = new NonPlayableCharacter(player.getPosition().x + 500, player.getPosition().y);
        nonPlayableCharacters = new LinkedList<>();

        //Item creation
        items = new LinkedList<>();

        //Roulette Creation (Test)
        //roulette = new Item.Roulette(player.getPosition().x + 100, player.getPosition().y);

        // Debug
        debugRenderer = new ShapeRenderer();
        debugFont = new BitmapFont();
        debugBatch = new SpriteBatch();

        if (debug)
        {
            System.out.println("World created");
        }
    }

    /**
     * WorldUpdate updates the World class with the player class from GameScreen. Removing this shouldn't cause a null
     * exception, but it does :/
     *
     * @param player Player
     */
    public void worldUpdate(Player player) {
        this.player = player;
    }


    /**
     * checkCollisions()
     * <p>
     * A method that is used to test for collisions
     * Using MapLayer, the collision layer is gathered. It then iterates through the objects to see if each object had some
     * form of interaction.
     * Currently in progress
     * TODO: See if the iteration can be changed.
     * TODO: Calculate a way to allow for the method to jump.
     * TODO: Refactor
     *
     * @param delta float
     * @param p     A player object. This includes players and objects that are extended from it such as enemies.
     */
    public void checkCollisions(float delta, Player p) {
        float playerBottom;
        float playerTop;
        float playerLeft;
        float playerRight;

        float objectBottom;
        float objectTop;
        float objectLeft;
        float objectRight;

        float oldX = p.getPosition().x;
        float oldY = p.getPosition().y;

        //isTouchingWall = isTouchingWall(player);
        //System.out.println(isTouchingWall);

        // Iterate through all objects in the collision layer
        for (MapObject object : objects) {
            if (object instanceof RectangleMapObject) {
                RectangleMapObject rectObject = (RectangleMapObject) object;
                Rectangle rect = rectObject.getRectangle();

                playerBottom = p.getPosition().y;
                playerTop = p.getPosition().y + p.getHeight();
                playerLeft = p.getPosition().x;
                playerRight = p.getPosition().x + p.getWidth();

                objectBottom = rect.y;
                objectTop = rect.y + rect.height;
                objectLeft = rect.x;
                objectRight = rect.x + rect.width;

                if (p.getBounds().overlaps(rect)) {
                    // Check for ground collision
                    if (playerBottom < objectTop + 5f && playerTop - 50f > objectTop) {
                        if (!(playerLeft < objectRight) || !(playerRight > objectLeft)) {
                            p.getPosition().x = 0;
                        } else {
                            p.getPosition().y = objectTop;
                            //isTouchingGround = true;
                            p.setGrounded(true);
                        }
                    }

                    // Check for left wall collision
                    if (playerRight > objectLeft && playerLeft < objectLeft) {
                        if (p.getVelocity().x > 0 && playerRight <= objectLeft + p.getVelocity().x) {
                            if (p.isGrounded() && playerBottom < objectTop + 5f && playerTop - 50f > objectTop) {
                                p.getVelocity().y = 0;
                            } else {
                                //p.getPosition().x = objectLeft - p.getWidth();
                                p.getVelocity().x = 0;
                            }

                        } else if (p.isGrounded() && (playerBottom < objectLeft)) {
                            /*
                            This mess of code is a little much.
                            Uncommenting the velocity code prevents the player from jumping.
                             */
                            System.out.println("Pushing wall");
                            //p.getVelocity().y = 0; // Stop the player's horizontal movement
                            //p.getPosition().x = oldX - 1; // Reset the player's position to the previous x-coordinate
                        } else {
                            p.getPosition().x = oldX - 1; // Reset the player's position to the previous x-coordinate
                        }
                        //p.setTouchingWall(true);
                        p.setTouchingLeftWall(true);
                        //isTouchingLeftWall = true;
                        //System.out.println("Touching left wall");
                    }

                    // If you're reading this, and you're not the owner of this repository, don't try to make sense of it
                    // because I currently don't know how it works
                    // Check for right wall collision
                    if (playerLeft < objectRight && playerRight > objectRight) {
                        if (p.getVelocity().x < 0 && playerLeft >= objectRight + p.getVelocity().x) {
                            if (p.isGrounded() && playerBottom < objectTop + 5f && playerTop - 50f > objectTop) {
                                p.getVelocity().y = 0;
                            } else {
                                //p.getPosition().x = objectRight;
                                p.getVelocity().x = 0;
                            }
                        } else if (p.isGrounded() && (playerBottom > objectRight)) {
                            /*
                            This mess of code is a little much.
                            Uncommenting the velocity code prevents the player from jumping.
                             */
                            System.out.println("Pushing wall");
                            //p.getVelocity().y = 0; // Stop the player's horizontal movement
                            //p.getPosition().x = oldX + 1; // Reset the player's position to the previous x-coordinate
                        } else {
                            p.getPosition().x = oldX + 1; // Reset the player's position to the previous x-coordinate
                        }
                        p.setTouchingRightWall(true);
                        //isTouchingRightWall = true;
                    }

                    // Check for ceiling collision
                    if (playerTop > objectBottom - 5f && playerBottom + 50f < objectBottom) {
                        if (!p.isGrounded()) {
                            System.out.println("Touching ceiling");
                            p.getPosition().y = objectBottom - p.getHeight();
                            p.getVelocity().y = 0;
                            p.setTouchingCeiling(true);
                            //isTouchingCeiling = true;
                        }
                    }
                }
            }
        }

        if (p.getVelocity().y > 1) {
            p.setGrounded(false);
            //isTouchingGround = false;
        }
        if (p.getVelocity().x > 1) {
            //isTouchingLeftWall = false;
            p.setTouchingLeftWall(false);
        }

        if (p.getVelocity().x < -1) {
            //isTouchingRightWall = false;
            p.setTouchingRightWall(false);
        }
        if (!p.isTouchingLeftWall() || !p.isTouchingRightWall()) {
            //isTouchingWall = false;
            p.setTouchingWall(false);
        }
        if (p.getVelocity().y < 1) {
            p.setTouchingCeiling(false);
            //isTouchingCeiling = false;
        }

        if (!isTouchingWall(p)) {
            p.setTouchingWall(false);
            p.setTouchingLeftWall(false);
            p.setTouchingRightWall(false);
        }

        if (p.isTouchingLeftWall() || p.isTouchingRightWall()) {
            p.setTouchingWall(true);
        }

        if (!isTouchingAnything(p)) {
            p.setGrounded(false);
            //isTouchingGround = false;
        }
        //System.out.println(p.isGrounded());

        // Update the player's collision status
        //p.setGrounded();
        //System.out.println(isTouchingWall(p));
        //System.out.println(isTouchingAnything(p));
        //p.setTouchingLeftWall(isTouchingLeftWall);
        //p.setTouchingRightWall(isTouchingRightWall);
        //p.setTouchingWall(isTouchingWall);
        //p.setTouchingCeiling(isTouchingCeiling);
        //System.out.println(isTouchingEndGoal());
        //System.out.println(isTouchingAnything());
    }

    /**
     * Applies gravity to player object
     * @param delta
     * @param p player
     */
    public void applyGravity(float delta, Player p)
    {
        // Apply gravity
        if (p.isGrounded()) {
            p.getVelocity().y = 0;
            //player.getPosition().y = oldY;
        } else {
            if (p.getVelocity().y >= MAX_FALL) {
                p.getVelocity().add(0, GRAVITY * delta);
            } else {
                p.getVelocity().y = MAX_FALL;
            }
        }
    }

    public void applyFriction(float delta, Player p)
    {
        if (p.getVelocity().x > 0) {
            p.getVelocity().sub(FRICTION, 0);
        }

        if (p.getVelocity().x < 0) {
            p.getVelocity().add(FRICTION, 0);
        }
    }

    /**
     * checkCollisions()
     * <p>
     * A method that is used to test items for collisions
     * Using MapLayer, the collision layer is gathered. It then iterates through the objects to see if each object had some
     * form of interaction
     * Currently in progress
     * Note: This method is overloaded. Currently, this has to be modified to fit with items.
     *
     * @param delta
     * @param item
     */
    public void checkCollisions(float delta, Item item) {
        float itemBottom;
        float itemTop;
        float itemLeft;
        float itemRight;

        float objectBottom;
        float objectTop;
        float objectLeft;
        float objectRight;

        float oldX = item.getPosition().x;
        float oldY = item.getPosition().y;

        //isTouchingWall = isTouchingWall(player);
        //System.out.println(isTouchingWall);

        // Iterate through all objects in the collision layer
        for (MapObject object : objects) {
            if (object instanceof RectangleMapObject) {
                RectangleMapObject rectObject = (RectangleMapObject) object;
                Rectangle rect = rectObject.getRectangle();

                itemBottom = item.getPosition().y;
                itemTop = item.getPosition().y + item.getHeight();
                itemLeft = item.getPosition().x;
                itemRight = item.getPosition().x + item.getWidth();

                objectBottom = rect.y;
                objectTop = rect.y + rect.height;
                objectLeft = rect.x;
                objectRight = rect.x + rect.width;

                if (item.getBounds().overlaps(rect)) {
                    // Check for ground collision
                    if (itemBottom < objectTop && itemTop > objectTop) {
                        if (!(itemLeft < objectRight) || !(itemRight > objectLeft)) {
                            item.getPosition().x = 0;
                        } else {
                            item.getPosition().y = objectTop;
                            //isTouchingGround = true;
                            item.setGrounded(true);
                        }
                    }

                    // Check for left wall collision
                    if (itemRight > objectLeft && itemLeft < objectLeft) {
                        if (item.getVelocity().x > 0 && itemRight <= objectLeft + item.getVelocity().x) {
                            if (item.isGrounded() && itemBottom < objectTop + 5f && itemTop - 50f > objectTop) {
                                item.getVelocity().y = 0;
                            } else {
                                //p.getPosition().x = objectLeft - p.getWidth();
                                item.getVelocity().x = 0;
                            }

                        } else if (item.isGrounded() && (itemBottom < objectLeft)) {
                            /*
                            This mess of code is a little much.
                            Uncommenting the velocity code prevents the player from jumping.
                             */
                            System.out.println("Pushing wall");
                            //p.getVelocity().y = 0; // Stop the player's horizontal movement
                            //p.getPosition().x = oldX - 1; // Reset the player's position to the previous x-coordinate
                        } else {
                            item.getPosition().x = oldX - 1; // Reset the player's position to the previous x-coordinate
                        }
                        //p.setTouchingWall(true);
                        item.setTouchingLeftWall(true);
                        //isTouchingLeftWall = true;
                        //System.out.println("Touching left wall");
                    }

                    // If you're reading this, and you're not the owner of this repository, don't try to make sense of it
                    // because I currently don't know how it works
                    // Check for right wall collision
                    if (itemLeft < objectRight && itemRight > objectRight) {
                        if (item.getVelocity().x < 0 && itemLeft >= objectRight + item.getVelocity().x) {
                            if (item.isGrounded() && itemBottom < objectTop + 5f && itemTop - 50f > objectTop) {
                                item.getVelocity().y = 0;
                            } else {
                                //p.getPosition().x = objectRight;
                                item.getVelocity().x = 0;
                            }
                        } else if (item.isGrounded() && (itemBottom > objectRight)) {
                            /*
                            This mess of code is a little much.
                            Uncommenting the velocity code prevents the player from jumping.
                             */
                            System.out.println("Pushing wall");
                            //p.getVelocity().y = 0; // Stop the player's horizontal movement
                            //p.getPosition().x = oldX + 1; // Reset the player's position to the previous x-coordinate
                        } else {
                            item.getPosition().x = oldX + 1; // Reset the player's position to the previous x-coordinate
                        }
                        item.setTouchingRightWall(true);
                        //isTouchingRightWall = true;
                    }

                    // Check for ceiling collision
                    if (itemTop > objectBottom - 5f && itemBottom + 50f < objectBottom) {
                        if (!item.isGrounded()) {
                            System.out.println("Touching ceiling");
                            item.getPosition().y = objectBottom - item.getHeight();
                            item.getVelocity().y = 0;
                            item.setTouchingCeiling(true);
                            //isTouchingCeiling = true;
                        }
                    }
                }
            }
        }

        if (item.getVelocity().y > 1) {
            item.setGrounded(false);
            //isTouchingGround = false;
        }
        if (item.getVelocity().x > 1) {
            //isTouchingLeftWall = false;
            item.setTouchingLeftWall(false);
        }

        if (item.getVelocity().x < -1) {
            //isTouchingRightWall = false;
            item.setTouchingRightWall(false);
        }
        if (!item.isTouchingLeftWall() || !item.isTouchingRightWall()) {
            //isTouchingWall = false;
            item.setTouchingWall(false);
        }
        if (item.getVelocity().y < 1) {
            item.setTouchingCeiling(false);
            //isTouchingCeiling = false;
        }

        //Apply Friction
        if (item.getVelocity().x > 0) {
            item.getVelocity().sub(FRICTION, 0);
        }

        if (item.getVelocity().x < 0) {
            item.getVelocity().add(FRICTION, 0);
        }

        if (item.isGrounded()) {
            item.getPosition().x += item.getVelocity().x * delta;
        } else {
            item.getPosition().add(item.getVelocity().x * delta, item.getVelocity().y * delta);
        }

        if (!isTouchingWall(item)) {
            item.setTouchingWall(false);
            item.setTouchingLeftWall(false);
            item.setTouchingRightWall(false);
        }

        if (item.isTouchingLeftWall() || item.isTouchingRightWall()) {
            item.setTouchingWall(true);
        }

        if (!isTouchingAnything(item)) {
            item.setGrounded(false);
            //isTouchingGround = false;
        }
        //System.out.println(p.isGrounded());

        // Update the player's collision status
        //p.setGrounded();
        //System.out.println(isTouchingWall(p));
        //System.out.println(isTouchingAnything(p));
        //p.setTouchingLeftWall(isTouchingLeftWall);
        //p.setTouchingRightWall(isTouchingRightWall);
        //p.setTouchingWall(isTouchingWall);
        //p.setTouchingCeiling(isTouchingCeiling);
        //System.out.println(isTouchingEndGoal());
        //System.out.println(isTouchingAnything());
    }

    /**
     * Applies gravity to an item
     * @param delta
     * @param i Item
     */
    public void applyGravity(float delta, Item i)
    {
        // Apply gravity
        if (i.isGrounded()) {
            i.getVelocity().y = 0;
            //player.getPosition().y = oldY;
        } else {
            if (i.getVelocity().y >= MAX_FALL) {
                i.getVelocity().add(0, GRAVITY * delta);
            } else {
                i.getVelocity().y = MAX_FALL;
            }
        }
    }

    /**
     * isTouchingAnything returns true if the player is currently colliding with a level object, false if otherwise.
     * Note that this only checks for level floors, ceilings and walls. This does not check for objects such as the
     * end level
     *
     * @param player
     * @return
     */
    public boolean isTouchingAnything(Player player) {
        float tolerance = 1f;

        boolean touching = false;

        for (MapObject object : objects) {
            if (object instanceof RectangleMapObject) {
                RectangleMapObject rectObject = (RectangleMapObject) object;
                Rectangle rect = rectObject.getRectangle();

                float left = rect.x - tolerance;
                float right = rect.x + rect.width + tolerance;
                float bottom = rect.y - tolerance;
                float top = rect.y + rect.height + tolerance;

                // Check if any part of the player's bounding box overlaps with the object's rectangle
                if (player.getPosition().x + player.getWidth() > left &&
                        player.getPosition().x < right &&
                        player.getPosition().y + player.getHeight() > bottom &&
                        player.getPosition().y < top) {
                    touching = true; // Player is touching the object
                    break; // Exit the loop, no need to check further
                }
            }
        }

        // Check if the player is not touching any object and is in the air
        if (!touching && !player.isGrounded()) {
            return false;
        }

        return touching; // Return the result
    }

    public boolean isTouchingAnything(Item item) {
        float tolerance = 1f;

        boolean touching = false;

        for (MapObject object : objects) {
            if (object instanceof RectangleMapObject) {
                RectangleMapObject rectObject = (RectangleMapObject) object;
                Rectangle rect = rectObject.getRectangle();

                float left = rect.x - tolerance;
                float right = rect.x + rect.width + tolerance;
                float bottom = rect.y - tolerance;
                float top = rect.y + rect.height + tolerance;

                // Check if any part of the player's bounding box overlaps with the object's rectangle
                if (item.getPosition().x + item.getWidth() > left &&
                        item.getPosition().x < right &&
                        item.getPosition().y + item.getHeight() > bottom &&
                        item.getPosition().y < top) {
                    touching = true; // Player is touching the object
                    break; // Exit the loop, no need to check further
                }
            }
        }

        // Check if the player is not touching any object and is in the air
        if (!touching && !item.isGrounded()) {
            return false;
        }

        return touching; // Return the result
    }

    /**
     * isTouchingWall returns true if the player is touching a wall, returns false if otherwise.
     *
     * @param player
     * @return True if the player is touching wall, false if otherwise.
     */
    public boolean isTouchingWall(Player player) {
        float tolerance = 1f;

        for (MapObject object : objects) {
            if (object instanceof RectangleMapObject) {
                RectangleMapObject rectObject = (RectangleMapObject) object;
                Rectangle rect = rectObject.getRectangle();

                float left = rect.x - tolerance;
                float right = rect.x + rect.width + tolerance;
                float bottom = rect.y - tolerance;
                float top = rect.y + rect.height + tolerance;

                boolean touchingLeftWall = player.getPosition().x + player.getWidth() >= left && player.getPosition().x <= left;
                boolean touchingRightWall = player.getPosition().x <= right && player.getPosition().x + player.getWidth() >= right;
                boolean aboveTop = player.getPosition().y + player.getHeight() >= top;

                if ((touchingLeftWall || touchingRightWall) && player.getPosition().y > bottom && player.getPosition().y < top && !aboveTop) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isTouchingWall(Item item) {
        float tolerance = 1f;

        for (MapObject object : objects) {
            if (object instanceof RectangleMapObject) {
                RectangleMapObject rectObject = (RectangleMapObject) object;
                Rectangle rect = rectObject.getRectangle();

                float left = rect.x - tolerance;
                float right = rect.x + rect.width + tolerance;
                float bottom = rect.y - tolerance;
                float top = rect.y + rect.height + tolerance;

                boolean touchingLeftWall = item.getPosition().x + item.getWidth() >= left && item.getPosition().x <= left;
                boolean touchingRightWall = item.getPosition().x <= right && item.getPosition().x + item.getWidth() >= right;
                boolean aboveTop = item.getPosition().y + item.getHeight() >= top;

                if ((touchingLeftWall || touchingRightWall) && item.getPosition().y > bottom && item.getPosition().y < top && !aboveTop) {
                    return true;
                }
            }
        }

        return false;
    }


    /**
     * isTouchingEndGoal returns true if the player has collided with the end goal object. Returns false if otherwise.
     *
     * @return True if player has collided with the end game object, false if otherwise
     */
    public void isTouchingEndGoal() {
        float tolerance = 1f;
        for (MapObject object : endGameObject) {
            if (object instanceof RectangleMapObject) {
                RectangleMapObject rectObject = (RectangleMapObject) object;
                Rectangle rect = rectObject.getRectangle();

                float left = rect.x - tolerance;
                float right = rect.x + rect.width + tolerance;
                float bottom = rect.y - tolerance;
                float top = rect.y + rect.height + tolerance;

                if (player.getPosition().x < right && player.getPosition().x + player.getWidth() > left &&
                        player.getPosition().y < top && player.getPosition().y + player.getHeight() > bottom) {
                    playerReachedEnd = true; // Player is touching the end goal
                }
            }
        }
         // Player is not touching the end goal
    }

    /**
     * Returns true if the player is colliding with an enemy, false if otherwise
     *
     * @return True if the player is colliding with an enemy, false if otherwise
     * <p>
     */
    public boolean isCollidingWithEnemy() {
        try {
            for (int i = 0; i < enemies.size(); i++ )
            {
                if (player.getBounds().overlaps(enemies.get(i).getBounds())) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            // Try catch is here to prevent null exceptions when an enemy is missing.
            return false;
        }
    }

    public boolean isCollidingWithNPC(NonPlayableCharacter n) {
        //npcLocation.set(npcLocation.x - 10f, npcLocation.y, npcLocation.getWidth() + 10f, npcLocation.getHeight());
        if (player.getBounds().overlaps(n.getInteractionBound())) {
            n.setTouchingPlayer(true);
            player.setInteraction(true);
            return true;
        } else
        {
            n.setTouchingPlayer(false);
        }
        return false;
    }

    public boolean isCollidingWithObject(Item item) {
        //item.setCollected(true);
        if (player.getBounds().overlaps(item.getBounds())) {
            item.setTouchingPlayer(true);
            player.setInteraction(true);
            return true;
        } else
        {
            item.setTouchingPlayer(false);
        }
        return false;
    }

    public int isAttackingEnemy() {
        for (int i = 0; i < enemies.size(); i++) {
            if (player.getPlayerAttack().getAttackHitbox() != null && player.getPlayerAttack().getAttackHitbox().overlaps(enemies.get(i).getBounds())) {
                return i;
            }
        }
        return -1;
    }

    public int isAttackingPlayer()
    {
        for (int i = 0; i < enemies.size(); i++)
        {
            if (enemies.get(i).getBounds().overlaps(player.getBounds())) {
                return i;
            }
        }
        return -1;
    }

    public void hurtCheck(int i)
    {
        int chosenEnemyHurt = isAttackingPlayer();
        if (chosenEnemyHurt >= 0 && chosenEnemyHurt == i) {
            if (!player.getPlayerAttack().isAttack()) {
                player.hurt(enemies.get(i));
                System.out.println("Enemy: " + i + " is hurting player");
            }
        }
    }

    private void messageRenderNPC(NonPlayableCharacter nonPlayableCharacter)
    {
        if (player.isDisplayMessage()) {
            nonPlayableCharacter.setDisplayMessage(true);
        }
        if (nonPlayableCharacter.isDisplayMessage()) {
            //p.Message(player);
            nonPlayableCharacter.dialogue.Message(player,nonPlayableCharacter);
            nonPlayableCharacter.setDisplayMessage(false);
        }
    }

    private void messageRenderItem(Item i)
    {
        if (player.isDisplayMessage())
        {
            i.setDisplayMessage(true);
        }
        if (i.isDisplayMessage())
        {
            System.out.println("AA");
            i.interact(player);
            i.setDisplayMessage(false);
            //roulette.interact(game, player);
            //i.interact(player);
        }
    }

    public void loadEnemies(LinkedList<Enemy> e)
    {
        enemies = e;
    }

    public void loadEnemy(Enemy e)
    {
        enemies.add(e);
    }

    public void loadNPCs(LinkedList<NonPlayableCharacter> n)
    {
        nonPlayableCharacters = n;
    }

    public void loadNPC(NonPlayableCharacter n)
    {
        nonPlayableCharacters.add(n);
    }

    public void loadItems(LinkedList<Item> i)
    {
        for (int j = 0; j < i.size(); j++)
        {
            items.add(i.get(j));
        }
    }

    public void loadItem(Item i)
    {
        items.add(i);
    }


    /**
     * Map renderer
     * Important to note that where the item is located depends on what is in the front of the map and what is in the
     * back
     * This might need to be refactored. Enemy render has too many nested if statements
     * @param delta
     */
    public void render(float delta)
    {
        // Clear screen
        Gdx.gl.glClearColor(37f/255f, 79f/255f, 126f/255f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update the camera's view
        camera.update();
        mapRenderer.setView(camera);
        spriteBatch.setProjectionMatrix(camera.combined);

        // Set the background position to match the camera's bottom-left corner
        backgroundSprite.setPosition(camera.position.x - camera.viewportWidth / 2,
                camera.position.y - camera.viewportHeight / 2);

        // Background
        spriteBatch.begin();
        backgroundSprite.draw(spriteBatch);
        spriteBatch.end();

        // Set the camera's position to follow the player, considering half of the screen size
        camera.position.x = player.getPosition().x + player.getWidth() / SCALE;
        camera.position.y = player.getPosition().y + player.getHeight() / SCALE;

        mapRenderer.render();

        // Enemy render
        for (int i = 0; i < enemies.size(); i++) {
            if (enemies.get(i) != null) {
                if (enemies.get(i).getHealth() > 0) {
                    checkCollisions(delta, enemies.get(i));
                    applyGravity(delta, enemies.get(i));
                    applyFriction(delta, enemies.get(i));
                    enemies.get(i).updateCamera(camera);
                    enemies.get(i).render(spriteBatch, delta);
                    enemies.get(i).playerUpdate(player);

                        //Attack Check
                        int chosenEnemyAttack = isAttackingEnemy();
                        if (chosenEnemyAttack >= 0 && chosenEnemyAttack == i) {
                            player.getPlayerAttack().deployAttack(enemies.get(chosenEnemyAttack));
                            enemies.get(chosenEnemyAttack).setAttacked(true);
                            enemies.get(chosenEnemyAttack).setHitStunDuration(0);
                            System.out.println("Attacking " + i);
                        }

                    try {
                        hurtCheck(i);
                    } catch (Exception e)
                    {
                        System.out.println("Enemy not present");
                    }

                } else {
                    // Enemy is removed from the world.
                    loadItems(enemies.get(i).lootDrop());
                    enemies.remove(i);
                }
            }
        }

        // Item render
        boolean touching = false;
        try {
            for (int i = 0; i < items.size(); i++) {

                checkCollisions(delta, items.get(i));
                applyGravity(delta, items.get(i));
                items.get(i).updateCamera(camera);
                items.get(i).render(spriteBatch, delta);
                //collectables.get(i).interact(player);
                //messageRenderItem(items.get(i));
                if (isCollidingWithObject(items.get(i))) {
                    if (!items.get(i).isCollected() && items.get(i).isAllowedToBeCollected()) {
                        items.get(i).setCollected(true);
                        items.get(i).collectedAction(player);
                        player.itemCollected(items.get(i));
                        items.remove(i);
                    }
                    touching = true;
                    messageRenderItem(items.get(i));
                }
            }
            if (!touching) {
                player.setInteraction(false);
            }
        } catch (Exception e)
        {
            System.out.println("Items not present");
        }

        // NPC render
        // When the NPC displays their UI, I need it to be in front of things such as items. Fow now this is an easy implementation
        for (int i = 0; i < nonPlayableCharacters.size(); i++) {
            nonPlayableCharacters.get(i).updateCamera(camera);
            checkCollisions(delta, nonPlayableCharacters.get(i));
            applyGravity(delta, nonPlayableCharacters.get(i));
            applyFriction(delta, nonPlayableCharacters.get(i));
            nonPlayableCharacters.get(i).updateCamera(camera);
            nonPlayableCharacters.get(i).render(spriteBatch, delta);
            //System.out.println(player.nextMessage);
            //System.out.println(isCollidingWithNPC());

            //isCollidingWithNPC(nonPlayableCharacters.get(0)); // WHYYYYYYYY? WHEN I SET THIS TO 0 IT WORKS FINE BUT WHEN IT'S IN THE LOOP IT BREAKS
            if (isCollidingWithNPC(nonPlayableCharacters.get(i)))
            {
                touching = true;
                messageRenderNPC(nonPlayableCharacters.get(i));
            }
        }
        if (!touching)
        {
            player.setInteraction(false);
        }

        // Player render
        checkCollisions(delta, player);
        applyGravity(delta, player);
        applyFriction(delta, player);
        player.updateCamera(camera);
        player.render(spriteBatch,delta);

        isTouchingEndGoal();

        // render debug rectangles
        if (debug) renderDebug();
    }

    /**
     * Map debug renderer. Draws lines around key objects such as the player or the world objects.
     */
    private void renderDebug ()
    {


        debugRenderer.setProjectionMatrix(camera.combined);

        debugBatch.begin();
        try {
            debugFont.draw(debugBatch, "Velocity: " + player.getVelocity(), camera.viewportWidth * .05f, camera.viewportHeight * .95f);
            debugFont.draw(debugBatch, "Position: " + player.getPosition(), camera.viewportWidth * .05f, camera.viewportHeight * .85f);
            debugFont.draw(debugBatch, "Items Collected: " + player.getCollectedItems().size(), camera.viewportWidth * .05f, camera.viewportHeight * .75f);
            debugFont.draw(debugBatch, "Facing Right: " + player.isFacingRight(), camera.viewportWidth * .05f, camera.viewportHeight * .65f);
            debugFont.draw(debugBatch, "Player Health: " + player.getHealth(), camera.viewportWidth * .05f, camera.viewportHeight * .55f);
            debugFont.draw(debugBatch, "FPS: " + Gdx.graphics.getFramesPerSecond(), camera.viewportWidth * .05f, camera.viewportHeight * .45f);
            //debugFont.draw(debugBatch, "Enemy Health: " + enemy.health, Gdx.graphics.getWidth() * .05f, Gdx.graphics.getHeight() * .45f);
            //debugFont.draw(debugBatch, "Jump Time (Not Actually):" + player.jumpTime, Gdx.graphics.getWidth() * .05f, Gdx.graphics.getHeight() * .35f);
        } catch (Exception e)
        {
            //
        }
        debugBatch.end();

        debugRenderer.begin(ShapeRenderer.ShapeType.Line);

        //Player Debug
        debugRenderer.setColor(Color.RED);
        debugRenderer.rect(player.getPosition().x, player.getPosition().y, player.getBounds().getWidth(), player.getBounds().getHeight());

        debugRenderer.setColor(Color.LIME);
        try {
            debugRenderer.rect(player.getPlayerAttack().getAttackHitbox().x, player.getPlayerAttack().getAttackHitbox().y, player.getPlayerAttack().getAttackHitbox().getWidth(), player.getPlayerAttack().getAttackHitbox().getHeight());
        } catch (Exception e)
        {
            // uhhh
        }
        //System.out.println("debugRender X:" + player.getPosition().x + " debugRender Y:" + player.getPosition().y);

        //Enemy Debug
        try
        {
            for (int i = 0; i < enemies.size(); i++) {
                debugRenderer.setColor(Color.CYAN);
                debugRenderer.rect(enemies.get(i).getPosition().x, enemies.get(i).getPosition().y, enemies.get(i).getBounds().getWidth(), enemies.get(i).getBounds().getHeight());
                debugRenderer.setColor(Color.RED);
                debugRenderer.rect(enemies.get(i).attackBounds.x,enemies.get(i).attackBounds.y, enemies.get(i).attackBounds.getWidth(), enemies.get(i).attackBounds.getHeight());
            }
        } catch (Exception e)
        {
            // Handle enemy removal
            // Try catch is here to handle when the enemy is missing
        }

        //NPC Debug
        for (int i = 0; i < nonPlayableCharacters.size(); i++) {
            debugRenderer.setColor(Color.PURPLE);
            debugRenderer.rect(nonPlayableCharacters.get(i).getPosition().x, nonPlayableCharacters.get(i).getPosition().y, nonPlayableCharacters.get(i).getBounds().getWidth(), nonPlayableCharacters.get(i).getBounds().getHeight());
            debugRenderer.setColor(Color.CYAN);
            debugRenderer.rect(nonPlayableCharacters.get(i).getInteractionBound().getX(), nonPlayableCharacters.get(i).getInteractionBound().getY(), nonPlayableCharacters.get(i).getInteractionBound().getWidth(), nonPlayableCharacters.get(i).getInteractionBound().getHeight());
        }

        //Item Debug
        for (Item collectable : items) {
            try {
                debugRenderer.setColor(Color.GREEN);
                debugRenderer.rect(collectable.getPosition().x, collectable.getPosition().y,
                        collectable.getWidth(), collectable.getHeight());
            } catch (Exception e) {
                // Handle item removal
            }
        }

        debugRenderer.setColor(Color.YELLOW);
        for (MapObject object : objects)
        {
            if (object instanceof RectangleMapObject)
            {
                RectangleMapObject rectObject = (RectangleMapObject) object;
                debugRenderer.rect(rectObject.getRectangle().x,rectObject.getRectangle().y,
                        rectObject.getRectangle().getWidth(),rectObject.getRectangle().getHeight());
            }
        }

        debugRenderer.end();
    }

    public void dispose()
    {
        mapRenderer.dispose();
        map.dispose();
        //player.dispose();
        for (int i = 0; i < enemies.size(); i++) {
            enemies.get(i).dispose();
        }
        for (Item collectable : items) {
            collectable.dispose();
        }
        spriteBatch.dispose();
    }

    //                      //
    // Getters and Setters  //
    //                      //


    public boolean isPlayerReachedEnd() {
        return playerReachedEnd;
    }

    public void setPlayerReachedEnd(boolean playerReachedEnd) {
        this.playerReachedEnd = playerReachedEnd;
    }

    public MapProperties getMapProperties() {
        return mapProperties;
    }

    public void setMapProperties(MapProperties mapProperties) {
        this.mapProperties = mapProperties;
    }

    public LinkedList<MapObject> getLoadingZones() {
        return loadingZones;
    }

    public void setLoadingZones(LinkedList<MapObject> loadingZones) {
        this.loadingZones = loadingZones;
    }
}
