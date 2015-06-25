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
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import org.smallbox.faraway.engine.Color;
import org.smallbox.faraway.engine.GFXRenderer;
import org.smallbox.faraway.engine.RenderEffect;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.engine.renderer.LightRenderer;
import org.smallbox.faraway.util.Constant;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.game.model.item.ItemModel;
import org.smallbox.faraway.game.model.item.ResourceModel;
import org.smallbox.faraway.game.model.item.StructureModel;
import org.smallbox.faraway.util.Log;

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
    private final SpriteBatch _batch;

    OrthographicCamera _camera;

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
    List<Light> _sunLights = new ArrayList<>();
    float sunDirection = -90f;
    private List<Body>  _bodies = new ArrayList<>();
    private Body        _sunBody;
    private PointLight  _sunLight1;
    private PointLight  _sunLight2;
    private List<Body>  _sunBodies = new ArrayList<>();

    public GDXLightRenderer() {
//        _camera = new OrthographicCamera(viewportWidth, viewportHeight);
        _camera = new OrthographicCamera();
//        _camera.setToOrtho(true);
        _camera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        _camera.position.set(4000, viewportHeight / 2f, 0);
//        _camera.translate(4000, 0);
        _camera.update();
        _batch = new SpriteBatch();
//        textureRegion = new TextureRegion(new Texture(Gdx.files.internal("data/items/chest.png")));
//        bg = new Texture(Gdx.files.internal("data/tilea4mackeditFBU.png"));
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
    public void onRefresh(int frame) {

    }

    @Override
    public void onDraw(GFXRenderer renderer, RenderEffect effect, double animProgress) {
        _camera.position.set(
                -effect.getViewport().getPosX() * effect.getViewport().getScale(),
                -effect.getViewport().getPosY() * effect.getViewport().getScale() + viewportHeight / 2f, 0);
        _camera.update();

//        _batch.setProjectionMatrix(_camera.combined);
//        _batch.disableBlending();
//        _batch.begin();
//        {
//            _batch.enableBlending();
//            for (Body body: _bodies) {
//                Vector2 position = body.getPosition();
//                float angle = MathUtils.radiansToDegrees * body.getAngle();
//                _batch.draw(
//                        textureRegion,
//                        position.x - RADIUS, position.y - RADIUS,
//                        RADIUS, RADIUS,
//                        RADIUS * 2, RADIUS * 2,
//                        1f, 1f,
//                        angle);
//            }
//            for (Body body: balls) {
//                Vector2 position = body.getPosition();
//                float angle = MathUtils.radiansToDegrees * body.getAngle();
//                _batch.draw(
//                        textureRegion,
//                        position.x - RADIUS, position.y - RADIUS,
//                        RADIUS, RADIUS,
//                        RADIUS * 2, RADIUS * 2,
//                        1f, 1f,
//                        angle);
//            }
//        }
//        _batch.end();

        /** BOX2D LIGHT STUFF BEGIN */
//        rayHandler.setBlur(false);
        rayHandler.setCombinedMatrix(_camera);

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

    @Override
    public void setSunColor(Color color) {
//        for (Light light: _sunLights) {
//            light.setColor(new com.badlogic.gdx.graphics.Color(color.r / 255f, color.g / 255f, color.b / 255f, color.a / 255f));
//        }
        if (_sunLight1 != null) {
            _sunLight1.setColor(new com.badlogic.gdx.graphics.Color(color.r / 255f, color.g / 255f, color.b / 255f, color.a / 255f * 0.75f));
        }

        if (_sunLight2 != null) {
            _sunLight2.setColor(new com.badlogic.gdx.graphics.Color(color.r / 255f, color.g / 255f, color.b / 255f, color.a / 255f * 0.25f));
        }

        if (_sunLight1 == null || _sunLight2 == null) {
            Log.error("SunLight missing");
        }
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

//        for (Body sunBody: _sunBodies) {
//            _sunLight = new PointLight(rayHandler, RAYS_PER_BALL, null, 1000, sunBody.getPosition().x, sunBody.getPosition().y);
//            _sunLight.setSoft(false);
//            _sunLight.setPosition(sunBody.getPosition().x, sunBody.getPosition().y);
////            _sunLight.setXray(true);
//            _sunLight.attachToBody(sunBody, RADIUS / 2f, RADIUS / 2f);
////            _sunLight.attachToBody(_sunBody, RADIUS / 2f, RADIUS / 2f);
////                _sunLight.setPosition(viewportWidth / 2, viewportHeight / 2);
//            _sunLight.setColor(255, 255, 255, 0.11f);
//            _sunLights.add(_sunLight);
//            lights.add(_sunLight);
//        }
        _sunLight1 = new PointLight(rayHandler, RAYS_PER_BALL, null, 20000, 0, 0);
        _sunLight1.setSoft(false);
        _sunLight1.setXray(true);
        _sunLight1.attachToBody(_sunBody, RADIUS / 2f, RADIUS / 2f);
        _sunLight1.setColor(255, 255, 255, 0.11f);
        lights.add(_sunLight1);

        _sunLight2 = new PointLight(rayHandler, RAYS_PER_BALL, null, 20000, 0, 0);
        _sunLight2.setSoft(false);
        _sunLight2.setXray(false);
        _sunLight2.attachToBody(_sunBody, RADIUS / 2f, RADIUS / 2f);
        _sunLight2.setColor(255, 255, 255, 0.11f);
        lights.add(_sunLight2);

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

        for (int x = 0; x < Game.getWorldManager().getWidth(); x += 5) {
            for (int y = 0; y < Game.getWorldManager().getHeight(); y += 5) {
                ParcelModel parcel = Game.getWorldManager().getParcel(x, y);
                if (parcel != null) {
                    if (parcel.getRoom() != null && parcel.getRoom().isExterior()) {
                        if (parcel.getStructure() == null || parcel.getStructure().isFloor()) {
                            int posX = x * 32 - Constant.WINDOW_WIDTH / 2;
                            int posY = y * 32;
                            _sunBody = createLight(boxBodyDef, def, posX, posY);
                            _sunBodies.add(_sunBody);
                        }
                    }
                }
            }
        }

        _sunBody = createLight(boxBodyDef, def, 0, 0);

        if (Game.getWorldManager() != null) {
            int width = Game.getWorldManager().getWidth();
            int height = Game.getWorldManager().getHeight();
            ParcelModel[][][] areas = Game.getWorldManager().getParcels();
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    if (areas[x][y][0] != null && areas[x][y][0].getItem() != null && areas[x][y][0].getItem().getInfo().light > 0) {
                        int posX = x * 32 - 0 - Constant.WINDOW_WIDTH / 2;
                        int posY = y * 32 - 0;
                        Body body = createLight(boxBodyDef, def, posX, posY);
                        body.setUserData(areas[x][y][0].getItem().getInfo().light);
                        balls.add(body);
                    }
                    if (areas[x][y][0] != null && areas[x][y][0].getResource() != null && areas[x][y][0].getResource().getInfo().light > 0) {
                        int posX = x * 32 - 0 - Constant.WINDOW_WIDTH / 2;
                        int posY = y * 32 - 0;
                        Body body = createLight(boxBodyDef, def, posX, posY);
                        body.setUserData(areas[x][y][0].getResource().getInfo().light);
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
            ParcelModel[][][] areas = Game.getWorldManager().getParcels();
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    if (areas[x][y][0] != null && areas[x][y][0].getStructure() != null && areas[x][y][0].getStructure().isSolid()) {
                        int posX = x * 32 - 0 - Constant.WINDOW_WIDTH / 2 + 16;
                        int posY = y * 32 - 0 + 24;
                        createBox(bodyDef, fixtureDef, posX, posY);
                    }
                    if (areas[x][y][0] != null && areas[x][y][0].getResource() != null && areas[x][y][0].getResource().isRock()) {
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

    @Override
    public void onAddItem(ItemModel item) {
        if (item.isLight()) {
            init();
        }
    }

    @Override
    public void onAddResource(ResourceModel resource) {
        if (resource.isLight()) {
            init();
        }
    }

    @Override
    public void onAddStructure(StructureModel structure) {
        if (structure.isSolid()) {
            init();
        }
    }
}
