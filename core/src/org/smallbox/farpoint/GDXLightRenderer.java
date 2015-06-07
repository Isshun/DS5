package org.smallbox.farpoint;

import box2dLight.Light;
import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import org.smallbox.faraway.GFXRenderer;
import org.smallbox.faraway.Game;
import org.smallbox.faraway.LightRenderer;
import org.smallbox.faraway.engine.util.Constant;
import org.smallbox.faraway.model.item.WorldArea;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 04/06/2015.
 */
public class GDXLightRenderer extends LightRenderer {
    static final int RAYS_PER_BALL = 128;
    static final float RADIUS = 1f;

    static final float viewportWidth = 1920;
    static final float viewportHeight = 1200;
    private final SpriteBatch batch;

    OrthographicCamera camera;

    BitmapFont font;
    TextureRegion textureRegion;
    Texture bg;

    /** our box2D world **/
    World world;

    /** our boxes **/
    ArrayList<Body> balls = new ArrayList<>();

    /** our ground box **/
    Body groundBody;

    /** our mouse joint **/
    MouseJoint mouseJoint = null;

    /** a hit body **/
    Body hitBody = null;

    /** pixel perfect projection for font rendering */
    Matrix4 normalProjection = new Matrix4();
    boolean showText = true;

    /** BOX2D LIGHT STUFF */
    RayHandler rayHandler;
    List<Light> lights = new ArrayList<>();
    float sunDirection = -90f;
    private List<Body>  _bodies = new ArrayList<>();
    private Body        _sunBody;
    private PointLight  _sunLight;

    public GDXLightRenderer() {
//        camera = new OrthographicCamera(viewportWidth, viewportHeight);
        camera = new OrthographicCamera();
//        camera.setToOrtho(true);
        camera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(4000, viewportHeight / 2f, 0);
//        camera.translate(4000, 0);
        camera.update();
        this.batch = new SpriteBatch();
        textureRegion = new TextureRegion(new Texture(Gdx.files.internal("data/items/chest.png")));
        bg = new Texture(Gdx.files.internal("data/tilea4mackeditFBU.png"));
        createPhysicsWorld();

        normalProjection.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        /** BOX2D LIGHT STUFF BEGIN */
        RayHandler.setGammaCorrection(true);
        RayHandler.useDiffuseLight(true);

        rayHandler = new RayHandler(world);
        rayHandler.setAmbientLight(0f, 0f, 0f, 0.5f);
        rayHandler.setBlurNum(3);
        rayHandler.pointAtShadow(testPoint.x, testPoint.y);

//        initPointLights();
        /** BOX2D LIGHT STUFF END */
    }

    @Override
    public void onDraw(GFXRenderer renderer, int x, int y) {
        camera.position.set(x, y + viewportHeight / 2f, 0);
        camera.update();

        batch.setProjectionMatrix(camera.combined);
        batch.disableBlending();
        batch.begin();
        {
            batch.enableBlending();
            for (Body body: _bodies) {
                Vector2 position = body.getPosition();
                float angle = MathUtils.radiansToDegrees * body.getAngle();
                batch.draw(
                        textureRegion,
                        position.x - RADIUS, position.y - RADIUS,
                        RADIUS, RADIUS,
                        RADIUS * 2, RADIUS * 2,
                        1f, 1f,
                        angle);
            }
            for (Body body: balls) {
                Vector2 position = body.getPosition();
                float angle = MathUtils.radiansToDegrees * body.getAngle();
                batch.draw(
                        textureRegion,
                        position.x - RADIUS, position.y - RADIUS,
                        RADIUS, RADIUS,
                        RADIUS * 2, RADIUS * 2,
                        1f, 1f,
                        angle);
            }
        }
        batch.end();

        /** BOX2D LIGHT STUFF BEGIN */
//        rayHandler.setBlur(false);
        rayHandler.setCombinedMatrix(camera);

//        if (stepped) rayHandler.update();
        rayHandler.update();
        rayHandler.render();
        /** BOX2D LIGHT STUFF END */
    }

    @Override
    public void init() {
        createWallObjects();
        createLightObjects();
        initPointLights();
    }

    void clearLights() {
        if (lights.size() > 0) {
            lights.forEach(box2dLight.Light::remove);
            lights.clear();
        }
        groundBody.setActive(true);
    }

    void initPointLights() {
        clearLights();

        _sunLight = new PointLight(rayHandler, RAYS_PER_BALL, null, 100000, 0f, 0f);
        _sunLight.setSoft(false);
        _sunLight.setPosition(0, 0);
        _sunLight.setXray(true);
        _sunLight.attachToBody(_sunBody, RADIUS / 2f, RADIUS / 2f);
        _sunLight.setPosition(viewportWidth / 2, viewportHeight / 2);
        _sunLight.setColor(255, 255, 255, 0.11f);
        lights.add(_sunLight);

        for (Body ball: balls) {
            PointLight light = new PointLight(rayHandler, RAYS_PER_BALL, null, (int)ball.getUserData(), 0f, 0f);
            light.attachToBody(ball, 32 / 2f, 32 / 2f);
//            light.setColor((float)Math.random(), (float)Math.random(), (float)Math.random(), 1);
            lights.add(light);
        }
    }

    private void createPhysicsWorld() {

        world = new World(new Vector2(0, 0), true);

        float halfWidth = viewportWidth / 2f;
        ChainShape chainShape = new ChainShape();
        chainShape.createLoop(new Vector2[] {
                new Vector2(-halfWidth, 0f),
                new Vector2(halfWidth, 0f),
                new Vector2(halfWidth, viewportHeight),
                new Vector2(-halfWidth, viewportHeight) });
        BodyDef chainBodyDef = new BodyDef();
        chainBodyDef.type = BodyDef.BodyType.DynamicBody;
        groundBody = world.createBody(chainBodyDef);
        groundBody.createFixture(chainShape, 0);
        chainShape.dispose();
    }

    private void createLightObjects() {
        balls.forEach(body -> world.destroyBody(body));
        balls.clear();

        CircleShape ballShape = new CircleShape();
        ballShape.setRadius(0);
//        ballShape.setRadius(RADIUS);

        FixtureDef def = new FixtureDef();
        def.restitution = 0.9f;
        def.friction = 0.01f;
        def.shape = ballShape;
        def.density = 1f;
        BodyDef boxBodyDef = new BodyDef();
        boxBodyDef.type = BodyDef.BodyType.DynamicBody;

        _sunBody = createLight(boxBodyDef, def, 0, 0);

        if (Game.getWorldManager() != null) {
            int width = Game.getWorldManager().getWidth();
            int height = Game.getWorldManager().getHeight();
            WorldArea[][][] areas = Game.getWorldManager().getAreas();
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    if (areas[x][y][0] != null && areas[x][y][0].getItem() != null && areas[x][y][0].getItem().getInfo().light > 0) {
                        int posX = x * 32 - 0 - Constant.WINDOW_WIDTH / 2;
                        int posY = y * 32 - 0;
                        Body body = createLight(boxBodyDef, def, posX, posY);
                        body.setUserData(areas[x][y][0].getItem().getInfo().light);
                        balls.add(body);
                    }
                }
            }
        }
    }

    private void createWallObjects() {
        _bodies.forEach(body -> world.destroyBody(body));
        _bodies.clear();

        PolygonShape boxShape = new PolygonShape();
        boxShape.setAsBox(16, 16);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.restitution = 0.1f;
        fixtureDef.friction = 0.5f;
        fixtureDef.shape = boxShape;
        fixtureDef.density = 1f;
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;

        if (Game.getWorldManager() != null) {
            int width = Game.getWorldManager().getWidth();
            int height = Game.getWorldManager().getHeight();
            WorldArea[][][] areas = Game.getWorldManager().getAreas();
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    if (areas[x][y][0] != null && areas[x][y][0].getStructure() != null && areas[x][y][0].getStructure().isSolid()) {
                        int posX = x * 32 - 0 - Constant.WINDOW_WIDTH / 2 + 16;
                        int posY = y * 32 - 0 + 24;
                        createBox(bodyDef, fixtureDef, posX, posY);
                    }
                }
            }
        }

        boxShape.dispose();
    }

    private void createBox(BodyDef boxBodyDef, FixtureDef def, int x, int y) {
        boxBodyDef.position.x = x;
        boxBodyDef.position.y = y;
        Body boxBody = world.createBody(boxBodyDef);
        boxBody.createFixture(def);
        _bodies.add(boxBody);
    }

    private Body createLight(BodyDef boxBodyDef, FixtureDef def, int x, int y) {
        boxBodyDef.position.x = x;
        boxBodyDef.position.y = y;
        Body boxBody = world.createBody(boxBodyDef);
        boxBody.createFixture(def);
        return boxBody;
    }

    /**
     * we instantiate this vector and the callback here so we don't irritate the
     * GC
     **/
    Vector3 testPoint = new Vector3();

    public Light getSun() {
        return _sunLight;
    }
}
