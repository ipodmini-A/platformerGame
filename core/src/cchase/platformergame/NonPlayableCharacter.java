package cchase.platformergame;

public class NonPlayableCharacter extends Player
{
    private boolean touchingPLayer = false;
    public NonPlayableCharacter(float x, float y)
    {
        super(x,y);
    }

    @Override
    public void input()
    {
        //Currently this is here to override input.
    }

    public boolean isTouchingPLayer() {
        return touchingPLayer;
    }

    public void setTouchingPLayer(boolean touchingPLayer) {
        this.touchingPLayer = touchingPLayer;
    }
}
