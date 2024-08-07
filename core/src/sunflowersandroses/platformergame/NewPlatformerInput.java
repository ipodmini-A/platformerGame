package sunflowersandroses.platformergame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import sunflowersandroses.platformergame.player.Player;

/**
 * NewPlatformerInput was created to replace PlatformerInput
 * It uses InputProcessor, which allows for input polling.
 * Note: When calling this, place it within show() or anything similar. Failure to do so results in input not functioning
 */
public class NewPlatformerInput implements InputProcessor
{
    private Player p;
    public static boolean debug = true;

    /**
     * Constructor. Sets the inputProcessor to the player
     * @param p Player
     */
    public NewPlatformerInput(Player p)
    {
        this.p = p;

    }

    public NewPlatformerInput()
    {
        p = new Player();
    }

    /**
     * Function that determines the logic when a key is pressed down
     *
     * @param keycode one of the constants in {@link Input.Keys}
     * @return true or false depending on if a key was clicked.
     */
    @Override
    public boolean keyDown(int keycode)
    {
        switch (keycode)
        {
            case Input.Keys.Z: // Up controls
                if (!p.isGrounded() && p.isTouchingWall() && !p.isWallRiding()) // Wall Jump input
                {
                    if (debug) {
                        System.out.println("Wall jump");
                    }
                    p.wallJump();
                } else if (!p.isGrounded() && !p.isTouchingWall()) // Double Jump input
                {
                    p.doubleJump();
                } else if (p.isWallRiding())
                {
                    if (debug) {
                        System.out.println("Jumping off wall from sliding");
                    }
                    p.wallJump();
                    p.setWallRiding(false);
                }
                else // Jump input
                {
                    System.out.println("Jump");
                    p.jumpPerformed = true;
                    p.jumpHeld = true;
                }
                break;
            case Input.Keys.C:
                System.out.println(p.isInteraction());
                if (p.isInteraction())
                {
                    if (!p.isMessageChoiceAvailable()) {
                        if (p.isDisplayMessage()) {
                            p.setNextMessage(true);
                        } else {
                            p.setDisplayMessage(true);
                        }
                    }
                    //p.setNextMessage(true);
                    //p.setNextMessage(false);
                }else
                {
                    p.getPlayerAttack().attack();
                }
                break;
            case Input.Keys.M:
                p.setMenuPressed(true);
                break;
            case Input.Keys.V:
                if (!p.isMessageChoiceAvailable()) {
                    if (p.isDisplayMessage()) {
                        p.setNextMessage(true);
                    } else {
                        p.setDisplayMessage(true);
                    }
                }
                break;
            case Input.Keys.UP:
                if (debug) {
                    System.out.println("Up");
                }
                p.setLookingUp(true);
                break;
            case Input.Keys.LEFT:
                if (debug) {
                    System.out.println("Left");
                }
                p.setLeftMove(true);
                if (!p.isGrounded())
                {
                    p.wallRide();
                }
                break;
            case Input.Keys.RIGHT:
                if (debug) {
                    Gdx.app.debug("Input","Right");
                }
                p.setRightMove(true);
                if (!p.isGrounded())
                {
                    p.wallRide();
                }
                break;
            case Input.Keys.DOWN:
                p.setDownMove(true);
                if (p.isInteraction())
                {
                    if (!p.isMessageChoiceAvailable()) {
                        if (p.isDisplayMessage()) {
                            p.setNextMessage(true);
                        } else {
                            p.setDisplayMessage(true);
                        }
                    }
                    //p.setNextMessage(true);
                    //p.setNextMessage(false);
                }else
                {
                    p.setLookingDown(true);
                }
                break;
            case Input.Keys.X:
                p.dash();
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * Determines the logic when a key is released.
     * @param keycode one of the constants in {@link Input.Keys}
     * @return true or false depending on the state of the key.
     */
    @Override
    public boolean keyUp(int keycode)
    {
        switch (keycode)
        {
            case Input.Keys.Z:
                p.jumpHeld = false;
                p.jumpReleased = true;
                break;
            case Input.Keys.UP:
                p.setLookingUp(false);
                break;
            case Input.Keys.LEFT:
                p.setLeftMove(false);
                p.setWallRiding(false);
                break;
            case Input.Keys.RIGHT:
                p.setRightMove(false);
                p.setWallRiding(false);
                break;
            case Input.Keys.DOWN:
                p.setDownMove(false);
                p.setLookingDown(false);
                break;
        }
        return true;
    }

    @Override
    public boolean keyTyped(char character)
    {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button)
    {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button)
    {
        return false;
    }

    @Override
    public boolean touchCancelled(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer)
    {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY)
    {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY)
    {
        return false;
    }
}
