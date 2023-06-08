package cchase.platformergame;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class Battle {
    Player player;
    Enemy enemy;
    PlatformerInput platformerInput;

    ShapeRenderer shapeRenderer;
    SpriteBatch spriteBatch;
    private Rectangle rectangle;
    BitmapFont font;
    float mouseX;
    float mouseY;
    private boolean magicClicked;
    private Rectangle attackButtonBounds;
    private Rectangle defendButtonBounds;
    private Rectangle magicButtonBounds;
    private float width = 100;
    private float height = 50;

    public Battle(Player player, Enemy enemy)
    {
        this.player = player;
        this.enemy = enemy;
        spriteBatch = new SpriteBatch();
        font = new BitmapFont();
        shapeRenderer = new ShapeRenderer();
        platformerInput = new PlatformerInput();
        magicClicked = false;

        attackButtonBounds = new Rectangle(100, 150, width, height);
        defendButtonBounds = new Rectangle(100, 100, width, height);
        magicButtonBounds = new Rectangle(100, 50, width, height);
    }

    public void render()
    {
        if (platformerInput.isLeftMouseClicked())
        {
            System.out.println(platformerInput.getLeftMouseClickedX());
            System.out.println(platformerInput.getLeftMouseClickedY());
            mouseX = platformerInput.getLeftMouseClickedX();
            mouseY = (Gdx.graphics.getHeight() - platformerInput.getLeftMouseClickedY()); //lol y is inverted so this is to un-invert it.
            if (mouseX > 100 && mouseX < 150
            && mouseY > 200 &&  mouseY < 250)
            {
                magicClicked = true;
            }
        }
        renderUI();
        platformerInput.update();
    }

    public void renderUI()
    {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.CYAN);
        shapeRenderer.rect(attackButtonBounds.x, attackButtonBounds.y, attackButtonBounds.width, attackButtonBounds.height); // Attack
        shapeRenderer.rect(defendButtonBounds.x, defendButtonBounds.y, defendButtonBounds.width, defendButtonBounds.height); // Defend
        shapeRenderer.rect(magicButtonBounds.x, magicButtonBounds.y, magicButtonBounds.width, magicButtonBounds.height); // Magic
        shapeRenderer.end();

        spriteBatch.begin();
        // Render player and enemy information
        font.draw(spriteBatch, "Player HP: " + 100/* HP */, 100, 500);
        font.draw(spriteBatch, "Enemy HP: " + 100 /* HP */, 100, 450);

        if (!magicClicked)
        {
            // Render action buttons
            if (100 /* HP */ > 0)
            {
                font.draw(spriteBatch, "Attack", 100, 175);
                font.draw(spriteBatch, "Defend", 100, 125);
                font.draw(spriteBatch, "Magic", 100, 75);
            } else
            {
                font.draw(spriteBatch, "Game Over", 100, 300);
            }
        } else
        {
            font.draw(spriteBatch, "Magic1", 100, 300);
            font.draw(spriteBatch, "Magic2", 100, 250);
            font.draw(spriteBatch, "Back", 100, 200);
        }
        spriteBatch.end();
    }

    public void dispose() {
        spriteBatch.dispose();
        font.dispose();
        shapeRenderer.dispose();
    }
}
