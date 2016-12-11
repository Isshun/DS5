//package org.smallbox.faraway.renders;
//
//import box2dLight.Light;
//import box2dLight.PointLight;
//import box2dLight.RayHandler;
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.graphics.Color;
//import com.badlogic.gdx.graphics.OrthographicCamera;
//import com.badlogic.gdx.math.Vector2;
//import com.badlogic.gdx.physics.box2d.*;
//import org.smallbox.faraway.client.renderer.Viewport;
//import org.smallbox.faraway.client.renderer.GDXRenderer;
//import org.smallbox.faraway.client.renderer.LightRenderer;
//import org.smallbox.faraway.core.game.Game;
//import org.smallbox.faraway.core.game.model.GameConfig;
//import UsableItem;
//import org.smallbox.faraway.core.module.world.model.ParcelModel;
//import org.smallbox.faraway.core.module.world.model.resource.ResourceModel;
//import org.smallbox.faraway.core.module.world.model.StructureItem;
//import org.smallbox.faraway.core.engine.module.java.ModuleHelper;
//import org.smallbox.faraway.util.Constant;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by Alex on 04/06/2015.
// */
//public class GDXLightRenderer extends LightRenderer {
//    static final int RAYS_PER_BALL = 128;
//
//    static final float viewportWidth = 1920;
//    static final float viewportHeight = 1200;
//
//    OrthographicCamera _camera;
//
//    /** our box2D _world **/
//    World _world;
//
//    /** our boxes **/
//    ArrayList<Body> _balls = new ArrayList<>();
//
//    /** our ground box **/
//    Body groundBody;
//
//    /** BOX2D LIGHT STUFF */
//    RayHandler _rayHandler;
//    List<Light> lights = new ArrayList<>();
//    private List<Body>  _bodies = new ArrayList<>();
//    private boolean     _needRefresh;
//
//    public GDXLightRenderer() {
//        _camera = new OrthographicCamera();
//        _camera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//        _camera.position.set(4000, viewportHeight / 2f, 0);
//        _camera.updateGame();
//
//        createPhysicsWorld();
//
//        RayHandler.setGammaCorrection(true);
//        RayHandler.useDiffuseLight(true);
//
//        _rayHandler = new RayHandler(_world);
//        _rayHandler.setAmbientLight(0f, 0f, 0f, 0.5f);
//        _rayHandler.setBlurNum(3);
//    }
//
//    @Override
//    public void onRefresh(int frame) {
//    }
//
//    @Override
//    public boolean isActive(GameConfig config) {
//        return false;
//    }
//
//    @Override
//    public void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress) {
//        if (_needRefresh) {
//            _needRefresh = false;
//            init();
//        }
//
//        _camera.position.set(
//                -viewport.getPosX() * viewport.getScale(),
//                -viewport.getPosY() * viewport.getScale() + viewportHeight / 2f, 0);
//        _camera.updateGame();
//
////        _batch.setProjectionMatrix(_camera.combined);
////        _batch.disableBlending();
////        _batch.begin();
////        {
////            _batch.enableBlending();
////            for (Body body: _bodies) {
////                Vector2 position = body.getPosition();
////                float angle = MathUtils.radiansToDegrees * body.getAngle();
////                _batch.draw(
////                        textureRegion,
////                        position.x - RADIUS, position.y - RADIUS,
////                        RADIUS, RADIUS,
////                        RADIUS * 2, RADIUS * 2,
////                        1f, 1f,
////                        angle);
////            }
////            for (Body body: _balls) {
////                Vector2 position = body.getPosition();
////                float angle = MathUtils.radiansToDegrees * body.getAngle();
////                _batch.draw(
////                        textureRegion,
////                        position.x - RADIUS, position.y - RADIUS,
////                        RADIUS, RADIUS,
////                        RADIUS * 2, RADIUS * 2,
////                        1f, 1f,
////                        angle);
////            }
////        }
////        _batch.end();
//
//        /** BOX2D LIGHT STUFF BEGIN */
//        _rayHandler.setCombinedMatrix(_camera);
//        _rayHandler.updateGame();
//        _rayHandler.render();
//        /** BOX2D LIGHT STUFF END */
//    }
//
//    @Override
//    public void init() {
//        createWallObjects();
//        createLightObjects();
//        initPointLights();
//    }
//
//    @Override
//    public void setSunColor(org.smallbox.faraway.core.engine.Color color) {
//        _rayHandler.setAmbientLight(new Color(color.r / 255f * 0.75f + 0.25f, color.g / 255f * 0.75f + 0.25f, color.b / 255f * 0.75f + 0.25f, 1f));
//    }
//
//    void clearLights() {
//        if (lights.size() > 0) {
//            lights.forEach(box2dLight.Light::remove);
//            lights.clear();
//        }
//        groundBody.setActive(true);
//    }
//
//    void initPointLights() {
//        clearLights();
//
//        for (Body ball: _balls) {
//            PointLight light = new PointLight(_rayHandler, RAYS_PER_BALL, null, (int)ball.getUserData() * 2, 0f, 0f);
//            light.attachToBody(ball, 32 / 2f, 32 / 2f);
//            lights.add(light);
//        }
//    }
//
//    private void createPhysicsWorld() {
//        _world = new World(new Vector2(0, 0), true);
//
//        float halfWidth = viewportWidth / 2f;
//        ChainShape chainShape = new ChainShape();
//        chainShape.createLoop(new Vector2[] {
//                new Vector2(-halfWidth, 0f),
//                new Vector2(halfWidth, 0f),
//                new Vector2(halfWidth, viewportHeight),
//                new Vector2(-halfWidth, viewportHeight) });
//        BodyDef chainBodyDef = new BodyDef();
//        chainBodyDef.type = BodyDef.BodyType.DynamicBody;
//        groundBody = _world.createBody(chainBodyDef);
//        groundBody.createFixture(chainShape, 0);
//        chainShape.dispose();
//    }
//
//    private void createLightObjects() {
//        _balls.forEach(body -> _world.destroyBody(body));
//        _balls.clear();
//
//        CircleShape ballShape = new CircleShape();
//        ballShape.setRadius(0);
////        ballShape.setRadius(RADIUS);
//
//        FixtureDef def = new FixtureDef();
//        def.restitution = 0.9f;
//        def.friction = 0.01f;
//        def.shape = ballShape;
//        def.density = 1f;
//        BodyDef boxBodyDef = new BodyDef();
//        boxBodyDef.type = BodyDef.BodyType.DynamicBody;
//
//        if (ModuleHelper.getWorldModule() != null) {
//            int width = Application.gameManager.getGame().getInfo().worldWidth;
//            int height = Application.gameManager.getGame().getInfo().worldHeight;
//            ParcelModel[][][] areas = ModuleHelper.getWorldModule().getParcels();
//            for (int x = 0; x < width; x++) {
//                for (int y = 0; y < height; y++) {
//                    if (areas[x][y][0] != null && areas[x][y][0].getItem() != null && areas[x][y][0].getItem().getInfo().light > 0) {
//                        int posX = x * 32 - 0 - Constant.WINDOW_WIDTH / 2;
//                        int posY = y * 32 - 0;
//                        Body body = createLight(boxBodyDef, def, posX, posY);
//                        body.setUserData(areas[x][y][0].getItem().getInfo().light);
//                        _balls.add(body);
//                    }
//                    if (areas[x][y][0] != null && areas[x][y][0].hasResource() && areas[x][y][0].getResource().getInfo().light > 0) {
//                        int posX = x * 32 - 0 - Constant.WINDOW_WIDTH / 2;
//                        int posY = y * 32 - 0;
//                        Body body = createLight(boxBodyDef, def, posX, posY);
//                        body.setUserData(areas[x][y][0].getResource().getInfo().light);
//                        _balls.add(body);
//                    }
//                }
//            }
//        }
//    }
//
//    private void createWallObjects() {
//        _bodies.forEach(body -> _world.destroyBody(body));
//        _bodies.clear();
//
//        PolygonShape boxShape = new PolygonShape();
//        boxShape.setAsBox(16, 16);
//
//        FixtureDef fixtureDef = new FixtureDef();
//        fixtureDef.restitution = 0.1f;
//        fixtureDef.friction = 0.5f;
//        fixtureDef.shape = boxShape;
//        fixtureDef.density = 1f;
//        BodyDef bodyDef = new BodyDef();
//        bodyDef.type = BodyDef.BodyType.StaticBody;
//
//        if (ModuleHelper.getWorldModule() != null) {
//            int width = Application.gameManager.getGame().getInfo().worldWidth;
//            int height = Application.gameManager.getGame().getInfo().worldHeight;
//            ParcelModel[][][] areas = ModuleHelper.getWorldModule().getParcels();
//            for (int x = 0; x < width; x++) {
//                for (int y = 0; y < height; y++) {
//                    if (areas[x][y][0] != null && areas[x][y][0].getStructure() != null && areas[x][y][0].getStructure().isSolid()) {
//                        int posX = x * 32 - 0 - Constant.WINDOW_WIDTH / 2 + 16;
//                        int posY = y * 32 - 0 + 24;
//                        createBox(bodyDef, fixtureDef, posX, posY);
//                    }
//                    if (areas[x][y][0] != null && areas[x][y][0].hasResource() && areas[x][y][0].getResource().isRock()) {
//                        int posX = x * 32 - 0 - Constant.WINDOW_WIDTH / 2 + 16;
//                        int posY = y * 32 - 0 + 24;
//                        createBox(bodyDef, fixtureDef, posX, posY);
//                    }
//                }
//            }
//        }
//
//        boxShape.dispose();
//    }
//
//    private void createBox(BodyDef boxBodyDef, FixtureDef def, int x, int y) {
//        boxBodyDef.position.x = x;
//        boxBodyDef.position.y = y;
//        Body boxBody = _world.createBody(boxBodyDef);
//        boxBody.createFixture(def);
//        _bodies.add(boxBody);
//    }
//
//    private Body createLight(BodyDef boxBodyDef, FixtureDef def, int x, int y) {
//        boxBodyDef.position.x = x;
//        boxBodyDef.position.y = y;
//        Body boxBody = _world.createBody(boxBodyDef);
//        boxBody.createFixture(def);
//        return boxBody;
//    }
//
//    @Override
//    public void onAddItem(UsableItem item) {
//        if (item.isLight()) {
//            _needRefresh = true;
//        }
//    }
//
//    @Override
//    public void onRemoveItem(UsableItem item) {
//        if (item.isLight()) {
//            _needRefresh = true;
//        }
//    }
//
//    @Override
//    public void onAddResource(ResourceModel resource) {
//        if (resource.isLight()) {
//            _needRefresh = true;
//        }
//    }
//
//    @Override
//    public void onRemovePlant(ResourceModel resource) {
//        if (resource.isLight()) {
//            _needRefresh = true;
//        }
//    }
//
//    @Override
//    public void onAddStructure(StructureItem structure) {
//        if (structure.isSolid()) {
//            _needRefresh = true;
//        }
//    }
//}
