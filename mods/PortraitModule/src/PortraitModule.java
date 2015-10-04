import org.smallbox.faraway.core.ui.GDXImageView;
import org.smallbox.faraway.core.ui.UILabel;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.module.GameUIModule;
import org.smallbox.faraway.game.module.UIWindow;
import org.smallbox.faraway.ui.engine.view.FrameLayout;
import org.smallbox.faraway.ui.engine.view.UILabel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 30/08/2015.
 */
public class PortraitModule extends GameUIModule {
    private List<CharacterModel> _characters;
    private PortraitModuleWindow _window;

    private class PortraitModuleWindow extends UIWindow {
        private FrameLayout _content;

        @Override
        protected void onCreate(UIWindow window, FrameLayout content) {
            _content = content;
        }

        @Override
        protected void onRefresh(int update) {
        }

        @Override
        protected String getContentLayout() {
            return null;
        }

        public void setCharacters(List<CharacterModel> characters) {
            for (CharacterModel character: characters) {
                GDXImageView image = new GDXImageView(91, 128);
                image.setImagePath("mods/PortraitModule/data/thumb.png");
                image.setPosition(characters.indexOf(character) * 100, 0);
                image.setOnClickListener(view -> Game.getInstance().notify(obs -> obs.onSelectCharacter(character)));
                _content.addView(image);

                UILabel lbFirstname = new UILabel();
                lbFirstname.setText(character.getInfo().getFirstName());
                lbFirstname.setTextSize(14);
                lbFirstname.setSize(91, 20);
                lbFirstname.setPosition(characters.indexOf(character) * 100, 80);
                lbFirstname.setTextAlign(Align.CENTER);
                _content.addView(lbFirstname);

                UILabel lbLastname = new UILabel();
                lbLastname.setText(character.getInfo().getLastName());
                lbLastname.setTextSize(14);
                lbLastname.setSize(91, 20);
                lbLastname.setPosition(characters.indexOf(character) * 100, 100);
                lbLastname.setTextAlign(Align.CENTER);
                _content.addView(lbLastname);
            }
        }
    }

    @Override
    protected void onLoaded() {
        _characters = new ArrayList<>();
        _window = new PortraitModuleWindow();
        _window.setPosition(500, 20);
        addWindow(_window);
    }

    @Override
    protected void onUpdate(int tick) {
    }

    @Override
    public void onAddCharacter(CharacterModel character) {
        _characters.add(character);
//        _window.setCharacters(_characters);
    }

}
