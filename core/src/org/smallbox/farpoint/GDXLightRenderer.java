package org.smallbox.farpoint;

import box2dLight.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
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
import org.smallbox.faraway.LightRenderer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 04/06/2015.
 */
public class GDXLightRenderer extends LightRenderer {
    static final int RAYS_PER_BALL = 128;
    static final float LIGHT_DISTANCE = 16f;
    static final float RADIUS = 1f;

    static final float viewportWidth = 48;
    static final float viewportHeight = 32;
    private final SpriteBatch batch;

    OrthographicCamera camera;

    BitmapFont font;
    TextureRegion textureRegion;
    Texture bg;

    /** our box2D world **/
    World world;

    /** our boxes **/
    ArrayList<Body> balls = new ArrayList<Body>();

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

    public GDXLightRenderer() {
        camera = new OrthographicCamera(viewportWidth, viewportHeight);
        camera.position.set(0, viewportHeight / 2f, 0);
        camera.update();
        this.batch = new SpriteBatch();
        textureRegion = new TextureRegion(new Texture(Gdx.files.internal("data/minerals_blue-128.png")));
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

        initPointLights();
        /** BOX2D LIGHT STUFF END */
    }

    @Override
    public void onDraw(GFXRenderer renderer) {
//        Gdx.gl.glClearColor(1, 1, 1, 1);
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

//        batch.setProjectionMatrix(camera.combined);
//        batch.disableBlending();
//        batch.begin();
//        {
//            batch.draw(bg, -viewportWidth / 2f, 0, viewportWidth, viewportHeight);
//            batch.enableBlending();
//            for (int i = 0; i < BALLSNUM; i++) {
//                Body ball = balls.get(i);
//                Vector2 position = ball.getPosition();
//                float angle = MathUtils.radiansToDegrees * ball.getAngle();
//                batch.draw(
//                        textureRegion,
//                        position.x - RADIUS, position.y - RADIUS,
//                        RADIUS, RADIUS,
//                        RADIUS * 2, RADIUS * 2,
//                        1f, 1f,
//                        angle);
//            }
//        }
//        batch.end();

        /** BOX2D LIGHT STUFF BEGIN */
        rayHandler.setCombinedMatrix(camera);

//        if (stepped) rayHandler.update();
        rayHandler.update();
        rayHandler.render();
        /** BOX2D LIGHT STUFF END */
    }

    void clearLights() {
        if (lights.size() > 0) {
            for (Light light : lights) {
                light.remove();
            }
            lights.clear();
        }
        groundBody.setActive(true);
    }

    void initPointLights() {
        clearLights();

        PointLight sun = new PointLight(rayHandler, RAYS_PER_BALL, null, 1000, 0f, 0f);
        sun.setSoft(false);
        sun.attachToBody(balls.get(0), RADIUS / 2f, RADIUS / 2f);
        sun.setColor(
                255,
                255,
                255,
                0.15f);
        lights.add(sun);

        for (Body ball: balls) {
            PointLight light = new PointLight(rayHandler, RAYS_PER_BALL, null, LIGHT_DISTANCE, 0f, 0f);
            light.setSoft(false);
            light.attachToBody(ball, RADIUS / 2f, RADIUS / 2f);
            light.setColor(
                    MathUtils.random(),
                    MathUtils.random(),
                    MathUtils.random(),
                    1f);
            lights.add(light);
        }
    }
//
//    void initConeLights() {
//        clearLights();
//        for (int i = 0; i < BALLSNUM; i++) {
//            ConeLight light = new ConeLight(
//                    rayHandler, RAYS_PER_BALL, null, LIGHT_DISTANCE,
//                    0, 0, 0f, MathUtils.random(15f, 40f));
//            light.attachToBody(
//                    balls.get(i),
//                    RADIUS / 2f, RADIUS / 2f, MathUtils.random(0f, 360f));
//            light.setColor(
//                    MathUtils.random(),
//                    MathUtils.random(),
//                    MathUtils.random(),
//                    1f);
//            lights.add(light);
//        }
//    }
//
//    void initChainLights() {
//        clearLights();
//        for (int i = 0; i < BALLSNUM; i++) {
//            ChainLight light = new ChainLight(
//                    rayHandler, RAYS_PER_BALL, null, LIGHT_DISTANCE, 1,
//                    new float[]{-5, 0, 0, 3, 5, 0});
//            light.attachToBody(
//                    balls.get(i),
//                    MathUtils.random(0f, 360f));
//            light.setColor(
//                    MathUtils.random(),
//                    MathUtils.random(),
//                    MathUtils.random(),
//                    1f);
//            lights.add(light);
//        }
//    }

    private final static int MAX_FPS = 30;
    private final static int MIN_FPS = 15;
    public final static float TIME_STEP = 1f / MAX_FPS;
    private final static float MAX_STEPS = 1f + MAX_FPS / MIN_FPS;
    private final static float MAX_TIME_PER_FRAME = TIME_STEP * MAX_STEPS;
    private final static int VELOCITY_ITERS = 6;
    private final static int POSITION_ITERS = 2;

    float physicsTimeLeft;
    long aika;
    int times;

    private boolean fixedStep(float delta) {
        physicsTimeLeft += delta;
        if (physicsTimeLeft > MAX_TIME_PER_FRAME)
            physicsTimeLeft = MAX_TIME_PER_FRAME;

        boolean stepped = false;
        while (physicsTimeLeft >= TIME_STEP) {
            world.step(TIME_STEP, VELOCITY_ITERS, POSITION_ITERS);
            physicsTimeLeft -= TIME_STEP;
            stepped = true;
        }
        return stepped;
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
        chainBodyDef.type = BodyDef.BodyType.StaticBody;
        groundBody = world.createBody(chainBodyDef);
        groundBody.createFixture(chainShape, 0);
        chainShape.dispose();
        createBoxes();
    }

    private void createBoxes() {
//        PolygonShape ballShape = new PolygonShape();
        CircleShape ballShape = new CircleShape();
//        ballShape.setAsBox(100, 100);
        ballShape.setRadius(RADIUS);

        FixtureDef def = new FixtureDef();
        def.restitution = 0.9f;
        def.friction = 0.01f;
        def.shape = ballShape;
        def.density = 1f;
        BodyDef boxBodyDef = new BodyDef();
        boxBodyDef.type = BodyDef.BodyType.DynamicBody;

        createLight(boxBodyDef, def, 0, 10);


        PolygonShape ballShape2 = new PolygonShape();
        ballShape2.setAsBox(1, 1);
//        ballShape2.setRadius(RADIUS);

        FixtureDef def2 = new FixtureDef();
        def2.restitution = 0.9f;
        def2.friction = 0.01f;
        def2.shape = ballShape2;
        def2.density = 1f;
        BodyDef boxBodyDef2 = new BodyDef();
        boxBodyDef2.type = BodyDef.BodyType.StaticBody;

        createBox(boxBodyDef2, def2, 5, 15);
//        createBox(boxBodyDef, def, 20, 20);
//        createBox(boxBodyDef, def, 30, 30);
//        createBox(boxBodyDef, def, 40, 40);
//        createBox(boxBodyDef, def, 50, 50);
//        for (int i = 0; i < BALLSNUM; i++) {
//            // Create the BodyDef, set a random position above the
//            // ground and create a new body
//            boxBodyDef.position.x = -20 + (float) (Math.random() * 40);
//            boxBodyDef.position.y = 10 + (float) (Math.random() * 15);
//            Body boxBody = world.createBody(boxBodyDef);
//            boxBody.createFixture(def);
//            balls.add(boxBody);
//        }
        ballShape2.dispose();
    }

    private void createBox(BodyDef boxBodyDef, FixtureDef def, int x, int y) {
        boxBodyDef.position.x = x;
        boxBodyDef.position.y = y;
        Body boxBody = world.createBody(boxBodyDef);
        boxBody.createFixture(def);
    }

    private void createLight(BodyDef boxBodyDef, FixtureDef def, int x, int y) {
        boxBodyDef.position.x = x;
        boxBodyDef.position.y = y;
        Body boxBody = world.createBody(boxBodyDef);
        boxBody.createFixture(def);
        balls.add(boxBody);
    }

    /**
     * we instantiate this vector and the callback here so we don't irritate the
     * GC
     **/
    Vector3 testPoint = new Vector3();
    QueryCallback callback = new QueryCallback() {
        @Override
        public boolean reportFixture(Fixture fixture) {
            if (fixture.getBody() == groundBody)
                return true;

            if (fixture.testPoint(testPoint.x, testPoint.y)) {
                hitBody = fixture.getBody();
                return false;
            } else
                return true;
        }
    };
}
