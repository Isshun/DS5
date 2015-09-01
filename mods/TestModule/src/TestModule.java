import org.smallbox.faraway.game.module.GameModule;

/**
 * Created by Alex on 30/08/2015.
 */
public class TestModule extends GameModule {

    @Override
    protected void onLoaded() {

    }

    @Override
    protected void onUpdate(int tick) {
        printInfo("TestModule here !");
    }
}
