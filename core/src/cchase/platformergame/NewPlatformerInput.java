package cchase.platformergame;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

/**
 * NewPlatformerInput was created to replace PlatformerInput
 *
 * It uses InputProcessor, which allows for input polling.
 *
 * Note: When calling this, place it within show() or anything similar. Failure to do so results in input not functioning
 */
public class NewPlatformerInput implements InputProcessor
{
    Player p;
    NewPlatformerInput(Player p)
    {
        this.p = p;
    }

    @Override
    public boolean keyDown(int keycode)
    {
        switch (keycode)
        {
            case Input.Keys.Z: // Up controls
                if (!p.isGrounded() && p.isTouchingWall()) // Wall Jump input
                {
                    System.out.println("Wall jump");
                    p.wallJump();
                } else if (!p.isGrounded() && !p.isTouchingWall()) // Double Jump input
                {
                    System.out.println("Double jump");
                    p.doubleJump();
                } else // Jump input
                {
                    System.out.println("Jump");
                    p.jump();
                }
                break;
            case Input.Keys.LEFT:
                System.out.println("Left");
                p.setLeftMove(true);
                break;
            case Input.Keys.RIGHT:
                System.out.println("Right");
                p.setRightMove(true);
                break;
            case Input.Keys.X:
                System.out.println("Dash");
                p.dash();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode)
    {
        switch (keycode)
        {
            case Input.Keys.LEFT:
                p.setLeftMove(false);
                break;
            case Input.Keys.RIGHT:
                p.setRightMove(false);
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