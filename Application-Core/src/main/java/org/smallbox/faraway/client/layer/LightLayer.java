package org.smallbox.faraway.client.layer;

import box2dLight.Light;
import box2dLight.RayHandler;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import org.smallbox.faraway.client.LayerLevel;
import org.smallbox.faraway.client.renderer.BaseRenderer;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.client.renderer.WorldCameraManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnInit;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.game.item.ItemModule;
import org.smallbox.faraway.game.item.UsableItem;
import org.smallbox.faraway.game.weather.WeatherModule;
import org.smallbox.faraway.util.Constant;

import java.util.ArrayList;

@GameObject
@GameLayer(level = LayerLevel.LIGHT_LAYER_LEVEL, visible = true)
public class LightLayer extends BaseLayer implements GameObserver {
    @Inject protected WorldCameraManager worldCameraManager;
    @Inject protected WeatherModule weatherModule;
    @Inject protected ItemModule itemModule;

    private RayHandler rayHandler;
    private World world;
    private Body groundBody;
    ArrayList<Body> balls = new ArrayList<>(42);
    float sunDirection = -90f;
    ArrayList<Light> lights = new ArrayList<>(42);
    static final int RAYS_PER_BALL = 128;

    @OnInit
    private void init() {
        createPhysicsWorld();

        RayHandler.setGammaCorrection(true);
        RayHandler.useDiffuseLight(true);

        rayHandler = new RayHandler(world);
        rayHandler.setAmbientLight(weatherModule.getAmbientLight());
        rayHandler.setBlurNum(3);
        initDirectionalLight();
//        new PointLight(rayHandler, 100, new Color(1,1,1,1), 5000, 1500, 1200);

    }

    @Override
    protected void onDraw(BaseRenderer renderer, Viewport viewport, double animProgress, int frame) {
//        sunDirection += Gdx.graphics.getDeltaTime() * 4f;
//        lights.get(0).setDirection(sunDirection);

        rayHandler.setAmbientLight(weatherModule.getAmbientLight());
//        rayHandler.setAmbientLight(0.5f, 0.5f, 0.5f, 1);
        rayHandler.setCombinedMatrix(worldCameraManager.getCamera());
        rayHandler.updateAndRender();
    }

    @Override
    public void onGameLongUpdate(Game game) {
        itemModule.getAll().forEach(this::createBoxes);
    }

    void initDirectionalLight() {
        clearLights();

//        {
////            groundBody.setActive(false);
////            sunDirection = MathUtils.random(0f, 360f);
//
//            DirectionalLight light = new DirectionalLight(rayHandler, 4 * RAYS_PER_BALL, null, sunDirection);
//            light.setColor(0.2f, 0.2f, 0.5f, 0.5f);
//            lights.add(light);
//        }
//
//        {
//            PointLight light = new PointLight(
//                    rayHandler, RAYS_PER_BALL, null, 300, 0f, 0f);
//            light.setPosition(400, 400);
////            light.attachToBody(balls.get(i), 400 / 2f, 400 / 2f);
//            light.setColor(
//                    MathUtils.random(),
//                    MathUtils.random(),
//                    MathUtils.random(),
//                    1f);
//            lights.add(light);
//        }
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

    private void createPhysicsWorld() {
        world = new World(new Vector2(0, 0), true);

        float halfWidth = 3000 / 2f;
        ChainShape chainShape = new ChainShape();
        chainShape.createLoop(new Vector2[]{
                new Vector2(-halfWidth, 0f),
                new Vector2(halfWidth, 0f),
                new Vector2(halfWidth, 2000),
                new Vector2(-halfWidth, 2000)});
        BodyDef chainBodyDef = new BodyDef();
        chainBodyDef.type = BodyDef.BodyType.StaticBody;
        groundBody = world.createBody(chainBodyDef);
        groundBody.createFixture(chainShape, 0);
        chainShape.dispose();
//        createBoxes(item);
    }

    private void createBoxes(UsableItem item) {
        PolygonShape ballShape = new PolygonShape();
        ballShape.setAsBox(100, 100);

        FixtureDef def = new FixtureDef();
        def.restitution = 0.9f;
        def.friction = 0.01f;
        def.shape = ballShape;
        def.density = 1f;
        BodyDef boxBodyDef = new BodyDef();
        boxBodyDef.type = BodyDef.BodyType.DynamicBody;

        // Create the BodyDef, set a random position above the
        // ground and create a new body
        boxBodyDef.position.x = item.getParcel().x * Constant.TILE_SIZE;
        boxBodyDef.position.y = item.getParcel().y * Constant.TILE_SIZE;
        Body boxBody = world.createBody(boxBodyDef);
        boxBody.createFixture(def);
        balls.add(boxBody);

        ballShape.dispose();
    }

}
