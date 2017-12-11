package com.platformer.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.platformer.Game;
import com.platformer.handlers.Box2DConstants;
import com.platformer.handlers.ContactListener;
import com.platformer.handlers.GameInput;
import com.platformer.handlers.GameStateManager;

import static com.platformer.handlers.Box2DConstants.GROUND_BIT;
import static com.platformer.handlers.Box2DConstants.PLAYER_BIT;
import static com.platformer.handlers.Box2DConstants.PPM;


//inherits batch, game, camera and hud from game state;

public class Play extends GameState {

    private BitmapFont font = new BitmapFont();
    private World world;
    private Box2DDebugRenderer box2dDebug;
    private OrthographicCamera box2dCam;
    private Body playerBody;
    private ContactListener cl;
    private float tileSize;

    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer tilemapRenderer;

    public Play(GameStateManager gsm) {
        super(gsm);
        world=new World(new Vector2(0, -9.81f), true);
        cl = new ContactListener();
        world.setContactListener(cl);
        box2dDebug = new Box2DDebugRenderer();

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(5 / PPM, 5/ PPM);

        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(160 / PPM,120/ PPM);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        playerBody = world.createBody(bodyDef);

        //create player
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.filter.categoryBits = Box2DConstants.PLAYER_BIT;
        fixtureDef.filter.maskBits = Box2DConstants.GROUND_BIT;
        fixtureDef.shape = shape;
        fixtureDef.friction = 0;
        playerBody.createFixture(fixtureDef).setUserData("player");
        //create ground

        //ground sensor
        shape.setAsBox(2/PPM, 2/PPM, new Vector2(0, -5/PPM), 0);
        fixtureDef.isSensor = true;
        fixtureDef.filter.categoryBits = Box2DConstants.PLAYER_BIT;
        fixtureDef.filter.maskBits = Box2DConstants.GROUND_BIT;
        fixtureDef.shape = shape;
        playerBody.createFixture(fixtureDef).setUserData("feet");


        box2dCam = new OrthographicCamera();
        box2dCam.setToOrtho(false, Game.WIDTH / PPM, Game.HEIGHT / PPM);

        //load map
        tiledMap = new TmxMapLoader().load("tmx/map.tmx");
        tilemapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        TiledMapTileLayer terrainLayer = (TiledMapTileLayer) tiledMap.getLayers().get("terrain");
        tileSize = terrainLayer.getTileWidth();

        //create b2d bodies for each tile.
        for(int row = 0; row < terrainLayer.getHeight(); row++) {
            for(int col = 0; col < terrainLayer.getWidth(); col++) {
                TiledMapTileLayer.Cell cell = terrainLayer.getCell(col, row);

                if(cell == null || cell.getTile() == null) continue;

                bodyDef.type = BodyDef.BodyType.StaticBody;
                bodyDef.position.set((col + 0.5f) * tileSize / PPM, (row + 0.5f) * tileSize / PPM);
                ChainShape cshape = new ChainShape();
                Vector2 verticies[] = {
                        new Vector2(-tileSize / 2 / PPM, -tileSize / 2 / PPM),
                        new Vector2(-tileSize / 2 / PPM, tileSize / 2 / PPM),
                        new Vector2(tileSize / 2 / PPM, tileSize / 2 /PPM),
                    };
                cshape.createChain(verticies);

                fixtureDef.friction = 0;
                fixtureDef.shape = cshape;
                fixtureDef.filter.categoryBits = GROUND_BIT;
                fixtureDef.filter.maskBits = PLAYER_BIT;
                fixtureDef.isSensor = false;
                world.createBody(bodyDef).createFixture(fixtureDef).setUserData("ground");
            }
        }


    }

    @Override
    public void handleInput() {
        if(GameInput.isPressed(GameInput.JUMP)) {
            System.out.println("canjump" + cl.canPlayerJump());
            if(cl.canPlayerJump()) {
                playerBody.applyForceToCenter(0,200,true);
            }
        }
        if(GameInput.isDown(GameInput.RIGHT)) {
            playerBody.setTransform(playerBody.getPosition().x +1 / PPM,  playerBody.getPosition().y, 0 );
        }

        if(GameInput.isDown(GameInput.LEFT)) {
            playerBody.setTransform(playerBody.getPosition().x -1 / PPM,  playerBody.getPosition().y, 0 );

        }

    }

    @Override
    public void update(float dt) {
        handleInput();
        world.step(dt, 6, 2);
    }

    @Override
    public void render() {

        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        //draw map
        tilemapRenderer.setView(camera);
        tilemapRenderer.render();

        box2dDebug.render(world, box2dCam.combined);
    }

    @Override
    public void dispose() {

    }


}