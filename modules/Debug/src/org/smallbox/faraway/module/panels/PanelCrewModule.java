package org.smallbox.faraway.module.panels;

import org.smallbox.faraway.core.SpriteManager;
import org.smallbox.faraway.engine.Color;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.module.GameUIModule;
import org.smallbox.faraway.game.module.ModuleHelper;
import org.smallbox.faraway.game.module.UIWindow;
import org.smallbox.faraway.game.module.base.CharacterModule;
import org.smallbox.faraway.ui.UserInterface;
import org.smallbox.faraway.ui.engine.Colors;
import org.smallbox.faraway.ui.engine.OnFocusListener;
import org.smallbox.faraway.ui.engine.ViewFactory;
import org.smallbox.faraway.ui.engine.views.UIFrame;
import org.smallbox.faraway.ui.engine.views.UIImage;
import org.smallbox.faraway.ui.engine.views.UILabel;
import org.smallbox.faraway.ui.engine.views.View;
import org.smallbox.faraway.util.Constant;
import org.smallbox.faraway.util.Strings;

import java.util.ArrayList;
import java.util.List;

public class PanelCrewModule extends GameUIModule {
    private static final int    MODE_SMALL = 0;
    private static final int    MODE_DETAIL = 1;

    private static final int     CREW_DETAIL_SPACING = 10;
    private static final int     CREW_LINE_SPACING = 2;
    private static final int     CREW_DETAIL_HEIGHT = 52;
    private static final int     CREW_LINE_HEIGHT = 22;
    private static final int     CREW_LINE_WIDTH = Constant.PANEL_WIDTH - Constant.UI_PADDING * 2;
    private static final int     FONT_SIZE = 14;
    private static final int     FONT_SIZE_TITLE = 22;
    private static final int    NB_COLUMNS = Constant.NB_COLUMNS;
    private static final int    NB_COLUMNS_TITLE = Constant.NB_COLUMNS_TITLE;

    private static class ViewHolder {
        public UIFrame frame;
        public UILabel      lbName;
        public UILabel      lbProfession;
        public UIImage      thumb;
        public UILabel      lbStatus;
        public UILabel      lbJob;
        public UILabel      lbStatusShort;
    }

    private class PanelCrewModuleWindow extends UIWindow {
        private CharacterModule         _characterModule;
        private List<ViewHolder>         _viewHolderList;
        private UILabel                 _lbCount;
        protected int                     _mode;

        @Override
        protected void onCreate(UIWindow window, UIFrame content) {
            ViewFactory factory = ViewFactory.getInstance();
            _viewHolderList = new ArrayList<>();
            _characterModule = ModuleHelper.getCharacterModule();

            // Button small
            UILabel btModeSmall = factory.createTextView(50, 20);
            btModeSmall.setText("small");
            btModeSmall.setTextSize(FONT_SIZE);
            btModeSmall.setPosition(300, -32);
            btModeSmall.setOnClickListener(view -> setMode(MODE_SMALL));
            addView(btModeSmall);

            // Button detail
            UILabel btModeDetail = factory.createTextView(50, 20);
            btModeDetail.setText("detail");
            btModeDetail.setTextSize(FONT_SIZE);
            btModeDetail.setPosition(360, -32);
            btModeDetail.setOnClickListener(view -> setMode(MODE_DETAIL));
            addView(btModeDetail);

            // Name
            _lbCount = factory.createTextView(10, 10);
            _lbCount.setTextSize(FONT_SIZE_TITLE);
            _lbCount.setTextColor(Color.WHITE);
            _lbCount.setPosition(20, 22);
            addView(_lbCount);
        }

        @Override
        protected String getContentLayout() {
            return "panels/crew";
        }

        protected void setMode(int mode) {
            _mode = mode;

            int i = 0;
            for (ViewHolder viewHolder: _viewHolderList) {
                if (mode == MODE_SMALL) {
                    setModeSmall(viewHolder, i);
                } else {
                    setModeDetail(viewHolder, i);
                }
                i++;
            }
        }

        protected void setModeSmall(ViewHolder viewHolder, int i) {
            viewHolder.frame.setPosition(20, 96 + ((CREW_LINE_HEIGHT + CREW_LINE_SPACING) * i));
            viewHolder.frame.setSize(CREW_LINE_WIDTH, CREW_LINE_HEIGHT);
            viewHolder.lbName.setPosition(0, 2);
            viewHolder.lbJob.setVisible(false);
            viewHolder.lbProfession.setVisible(false);
            viewHolder.thumb.setVisible(false);
            viewHolder.lbStatus.setVisible(false);
            viewHolder.lbStatus.setPosition(20, Constant.UI_PADDING + 16);
            viewHolder.lbStatusShort.setVisible(true);
        }

        protected void setModeDetail(ViewHolder viewHolder, int i) {
            viewHolder.frame.setPosition(20, 96 + ((CREW_DETAIL_HEIGHT + CREW_DETAIL_SPACING) * i));
            viewHolder.lbName.setPosition(32, 6);
            viewHolder.lbJob.setVisible(true);
            viewHolder.thumb.setVisible(true);
            viewHolder.lbStatus.setVisible(true);
            viewHolder.lbStatus.setPosition(32, Constant.UI_PADDING + 16);
            viewHolder.lbStatusShort.setVisible(false);
        }

        void  addCharacter(int index, final CharacterModel character) {
            if (index >= _viewHolderList.size()) {
                final ViewHolder viewHolder = new ViewHolder();

                // Frame
                viewHolder.frame = ViewFactory.getInstance().createFrameLayout(CREW_LINE_WIDTH, CREW_DETAIL_HEIGHT);
                viewHolder.frame.setOnFocusListener(new OnFocusListener() {
                    @Override
                    public void onExit(View view) {
                        viewHolder.lbName.setTextColor(Colors.LINK_INACTIVE);
                        viewHolder.lbName.setStyle(UILabel.REGULAR);
                        //view.setBackgroundColor(null);
                    }

                    @Override
                    public void onEnter(View view) {
                        viewHolder.lbName.setTextColor(Colors.LINK_ACTIVE);
                        viewHolder.lbName.setStyle(UILabel.UNDERLINED);
                        //view.setBackgroundColor(new Color(40, 40, 80));
                    }
                });
                addView(viewHolder.frame);

                // Name
                viewHolder.lbName = ViewFactory.getInstance().createTextView();
                viewHolder.lbName.setTextSize(FONT_SIZE);
                viewHolder.lbName.setPosition(0, 6);
                viewHolder.frame.addView(viewHolder.lbName);

                // Status
                viewHolder.lbStatus = ViewFactory.getInstance().createTextView();
                viewHolder.lbStatus.setTextSize(FONT_SIZE);
                viewHolder.frame.addView(viewHolder.lbStatus);

                // Status short
                viewHolder.lbStatusShort = ViewFactory.getInstance().createTextView(80, 20);
                viewHolder.lbStatusShort.setTextSize(FONT_SIZE);
                viewHolder.lbStatusShort.setVisible(false);

                viewHolder.frame.addView(viewHolder.lbStatusShort);

                // Job
                viewHolder.lbJob = ViewFactory.getInstance().createTextView();
                viewHolder.lbJob.setTextSize(12);
                viewHolder.lbJob.setPosition(260, 6);
                viewHolder.frame.addView(viewHolder.lbJob);

                // Profession
                viewHolder.lbProfession = ViewFactory.getInstance().createTextView();
                viewHolder.lbProfession.setTextSize(14);
                viewHolder.lbProfession.setVisible(false);
                viewHolder.lbProfession.setPosition(CREW_LINE_WIDTH - Constant.UI_PADDING - 100, Constant.UI_PADDING);
                viewHolder.frame.addView(viewHolder.lbProfession);

                viewHolder.thumb = ViewFactory.getInstance().createImageView();
                viewHolder.thumb.setImage(SpriteManager.getInstance().getCharacter(character, 0, 0));
                viewHolder.thumb.setPosition(0, 5);
                viewHolder.frame.addView(viewHolder.thumb);

                if (_mode == MODE_SMALL) {
                    setModeSmall(viewHolder, index);
                } else {
                    setModeDetail(viewHolder, index);
                }

                //          // Function
                //          Profession function = characters.getProfession();
                //          text.setText(function.getName());
                //          text.setPosition(_x + Constant.UI_PADDING + Constant.CHAR_WIDTH + Constant.UI_PADDING + (CREW_LINE_WIDTH * x),
                //                           _y + Constant.UI_PADDING + (CREW_LINE_HEIGHT * y) + 22);
                //          text.setTextColor(function.getColor());
                //          _app.draw(text, _renderEffect);

                _viewHolderList.add(viewHolder);
            } else {
                final ViewHolder viewHolder = _viewHolderList.get(index);
                viewHolder.frame.setVisible(true);

                // Action
                viewHolder.frame.setOnClickListener(view -> UserInterface.getInstance().getSelector().select(character));

                // Name
                viewHolder.lbName.setDashedString(character.getInfo().getName(), "", NB_COLUMNS);
                viewHolder.lbName.setTextColor(viewHolder.frame.isFocus() ? Colors.LINK_ACTIVE : new Color(120, 255, 255));

                // Job
                if (character.getJob() != null) {
                    viewHolder.lbJob.setText(character.getJob().getShortLabel());
                    viewHolder.lbJob.setTextColor(new Color(255, 255, 255));
                    viewHolder.lbJob.setPosition(376 - character.getJob().getShortLabel().length() * 8, 6);
                } else {
                    viewHolder.lbJob.setText(Strings.LB_NO_JOB);
                    viewHolder.lbJob.setTextColor(new Color(255, 255, 255, 100));
                    viewHolder.lbJob.setPosition(376 - Strings.LB_NO_JOB.length() * 8, 6);
                }
//            // Profession
//            viewHolder.lbProfession.setText(characters.getProfession().getName());
            }

        }

        @Override
        public void onRefresh(int frame) {
            if (frame % 2 == 0) {

                for (ViewHolder holder: _viewHolderList) {
                    holder.frame.setVisible(false);
                }

                int i = 0;
                for (CharacterModel c: _characterModule.getCharacters()) {
                    addCharacter(i++, c);
                }

                _lbCount.setDashedString("Count", String.valueOf(_characterModule.getCharacters().size()), NB_COLUMNS_TITLE);
            }
        }
    }

    @Override
    protected void onLoaded() {
//        ((PanelModule)ModuleManager.getInstance().getModule(PanelModule.class)).addShortcut("Crew", new PanelCrewModuleWindow());
    }

    @Override
    protected void onUpdate(int tick) {
    }
}
