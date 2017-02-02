package com.quadtree.simulation;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


public class MyMain extends Game
{
    static float WIDTH_WORLD = 1280;
    static float HEIGHT_WORLD = 720;

    static int NUM_RECTANGLES = 25000;

    long numCollision;

	SpriteBatch batch;
	Texture img, bad;
	Sprite sprite;
	ShapeRenderer shape;

	Viewport viewport, viewport2;
	OrthographicCamera camera, camera2;

	boolean move;

	//  QuadTree
	QuadTree quadTree;
	Sprite player1, player2;
	Rectangle rPlayer;
	Array<Rectangle> allRectangles, list;
	Array<Rectangle> allSprites;
	Array<Rectangle> allZones;


	//  Scene2d
    Stage stage;
    TextButton buttonMove;
    TextButton buttonFPS;
    TextButton buttonNumElementos;
    TextButton buttonNumCompara;
    TextButton buttonNumCollision;

	
	@Override
	public void create ()
    {
        numCollision = 0;
        move = false;
        shape = new ShapeRenderer();
        camera = new OrthographicCamera();
        camera.translate(WIDTH_WORLD*0.5f, HEIGHT_WORLD*0.5f, 0);
        camera.update();

        viewport = new FitViewport(WIDTH_WORLD, HEIGHT_WORLD, camera);
		batch = new SpriteBatch();
		bad = new Texture("badlogic.jpg");
		sprite = new Sprite(img);

		player1 = new Sprite(bad);
		player1.setPosition(900, 200);

		allRectangles = new Array<Rectangle>();
		quadTree = new QuadTree(0, new Rectangle(50, 50, WIDTH_WORLD-250, HEIGHT_WORLD-100));
		list = new Array<Rectangle>();

		allSprites = new Array<Rectangle>();
		allZones = new Array<Rectangle>();

		rPlayer = new Rectangle(700, 400, 5, 5);

		camera2 = new OrthographicCamera();
		viewport2 = new FitViewport(WIDTH_WORLD, HEIGHT_WORLD, camera2);

		stage = new Stage(viewport2);
		TextButton.TextButtonStyle tbs = new TextButton.TextButtonStyle();
		tbs.fontColor = Color.BLACK;
		tbs.font = new BitmapFont(Gdx.files.internal("font.fnt"));
		Texture t = new Texture(Gdx.files.internal("boton.png"));

		tbs.down = new TextureRegionDrawable(new TextureRegion(t));
        tbs.font.getData().setScale(1f/1.05f);

		buttonMove = new TextButton("Move", tbs);
        buttonMove.setPosition(1100, 10);

        buttonFPS = new TextButton("FPS", tbs);
        buttonFPS.setPosition(1100, 100);

        buttonNumElementos = new TextButton("NumElementos", tbs);
        buttonNumElementos.setPosition(1080, 200);

        buttonNumCompara = new TextButton("NumCompara", tbs);
        buttonNumCompara.setPosition(1080, 300);

        buttonNumCollision = new TextButton("Collision", tbs);
        buttonNumCollision.setPosition(1100, 400);

        Gdx.input.setInputProcessor(stage);

        buttonMove.addListener(new InputListener()
        {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                moveObjects();
                return super.touchDown(event, x, y, pointer, button);
            }
        });

        stage.addActor(buttonMove);
        stage.addActor(buttonFPS);
        stage.addActor(buttonNumElementos);
        stage.addActor(buttonNumCompara);
        stage.addActor(buttonNumCollision);

        createRectangles();


	}

	private void createRectangles()
    {
        for (int i = 0; i < NUM_RECTANGLES; i++)
        {
            Rectangle r = new Rectangle(MathUtils.random(-20, WIDTH_WORLD/3), MathUtils.random(50, HEIGHT_WORLD-50), 3, 3);
            allRectangles.add(r);
            quadTree.insert(r);
        }
    }

	@Override
	public void render ()
    {
		update();
		draw();
	}

	private void update()
    {
        controlCamera();
        controlRectangle();
        numCollision = 0;
        camera.update();
        addRectangle();


        //  Add all rectangles of own game into quadtree
        quadTree.clear();
        for(Rectangle s : allRectangles)
        {
            Rectangle r = new Rectangle(s.getX(), s.getY(), s.getWidth(), s.getHeight());
            quadTree.insert(r);
        }

        //  Get all possible collision elements in list
        list.clear();
        quadTree.retrieveFast(list, new Rectangle(rPlayer.getX(), rPlayer.getY(), rPlayer.getWidth(), rPlayer.getHeight()));

        //  Checking if some rectangle collides with the player
        for ( Rectangle s : list)
        {
            if (rPlayer.overlaps(s))
                numCollision++;
        }

        //  If you press Move buttom, then all rectangles will be moved
        if (move)
        {
            for (Rectangle s : allRectangles)
                s.x += (15 * Gdx.graphics.getDeltaTime());
        }


        allZones.clear();
        quadTree.getZones(allZones);
    }

    private void controlRectangle()
    {
        int speed = 70;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
            rPlayer.x -= speed * Gdx.graphics.getDeltaTime();

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            rPlayer.x += speed * Gdx.graphics.getDeltaTime();

        if (Gdx.input.isKeyPressed(Input.Keys.UP))
            rPlayer.y += speed * Gdx.graphics.getDeltaTime();

        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))
            rPlayer.y -= speed * Gdx.graphics.getDeltaTime();
    }


    private void addRectangle()
    {
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT))
        {
            Vector3 click = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(click);

            if (click.x <= WIDTH_WORLD-200)
            {
                Rectangle r = new Rectangle(click.x, click.y, 5, 5);
                r.setPosition(click.x, click.y);
                allRectangles.add(r);
            }
        }
    }

    private void moveObjects()
    {
        move = !move;
    }

    private void draw()
    {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        shape.setProjectionMatrix(camera.combined);
        shape.begin(ShapeRenderer.ShapeType.Line);

        for (Rectangle z : allZones)
            shape.rect(z.getX(), z.getY(), z.getWidth(), z.getHeight(), Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK);


        shape.setAutoShapeType(true);
        shape.set(ShapeRenderer.ShapeType.Filled);

        for (Rectangle z : allRectangles)
            shape.rect(z.getX(), z.getY(), z.getWidth(), z.getHeight(), Color.PURPLE, Color.PURPLE, Color.PURPLE, Color.PURPLE);

        shape.setColor(Color.BROWN);
        shape.rect(rPlayer.getX(), rPlayer.getY(), rPlayer.getWidth(), rPlayer.getHeight());

        Color c = new Color(Color.ORANGE);
        for (Rectangle s : list)
            shape.rect(s.getX(), s.getY(), s.getWidth(), s.getHeight(), c, c, c, c);

        shape.end();

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

        buttonFPS.setText("FPS:"+Gdx.graphics.getFramesPerSecond());
        buttonNumElementos.setText("Total Points:\n"+ allRectangles.size);
        buttonNumCompara.setText("Checking:\n" + list.size);
        buttonNumCollision.setText("Collisions:\n"+numCollision);
    }

    private void controlCamera()
    {
        if (Gdx.input.isKeyPressed(Input.Keys.Z))
            camera.zoom += 2 * Gdx.graphics.getDeltaTime();

        if (Gdx.input.isKeyPressed(Input.Keys.X))
            camera.zoom -= 2 * Gdx.graphics.getDeltaTime();
    }
	
	@Override
	public void dispose ()
    {
		batch.dispose();
		img.dispose();
		bad.dispose();
		shape.dispose();

	}

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        stage.getViewport().update(width, height);
        viewport2.update(width, height);
    }
}
