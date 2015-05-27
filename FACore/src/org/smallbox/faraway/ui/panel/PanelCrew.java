package org.smallbox.faraway.ui.panel;

import java.util.ArrayList;
import java.util.List;

import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard.Key;

import org.smallbox.faraway.Color;
import org.smallbox.faraway.Game;
import org.smallbox.faraway.Strings;
import org.smallbox.faraway.engine.ui.ButtonView;
import org.smallbox.faraway.engine.ui.Colors;
import org.smallbox.faraway.engine.ui.FrameLayout;
import org.smallbox.faraway.engine.ui.ImageView;
import org.smallbox.faraway.engine.ui.OnClickListener;
import org.smallbox.faraway.engine.ui.OnFocusListener;
import org.smallbox.faraway.engine.ui.TextView;
import org.smallbox.faraway.engine.ui.View;
import org.smallbox.faraway.manager.CharacterManager;
import org.smallbox.faraway.manager.SpriteManager;
import org.smallbox.faraway.model.character.Character;
import org.smallbox.faraway.ui.UserInterface;
import org.smallbox.faraway.ui.UserInterface.Mode;
import org.smallbox.faraway.engine.util.Constant;

public class PanelCrew extends BaseRightPanel {
	private static class ViewHolder {
		public TextView 	lbName;
		public TextView 	lbProfession;
		public ImageView	thumb;
		public FrameLayout 	frame;
		public TextView 	lbStatus;
		public TextView 	lbJob;
		public TextView 	lbStatusShort;
	}

	private static final int	MODE_SMALL = 0;
	private static final int	MODE_DETAIL = 1;

	private static final int 	CREW_DETAIL_SPACING = 10;
	private static final int 	CREW_LINE_SPACING = 2;
	private static final int 	CREW_DETAIL_HEIGHT = 52;
	private static final int 	CREW_LINE_HEIGHT = 22;
	private static final int 	CREW_LINE_WIDTH  = FRAME_WIDTH - Constant.UI_PADDING * 2;

	private CharacterManager    		_characterManager;
	private List<ViewHolder> 			_viewHolderList;
	private TextView 					_lbCount;
	protected int 						_mode;

	public PanelCrew(Mode mode, Key shortcut) {
		super(mode, shortcut);
	}

	@Override
	protected void onCreate() {
		_viewHolderList = new ArrayList<ViewHolder>();
		_characterManager = Game.getCharacterManager();

		// Button small
		ButtonView btModeSmall = new ButtonView(50, 20);
		btModeSmall.setString("small");
		btModeSmall.setCharacterSize(FONT_SIZE);
		btModeSmall.setPosition(300, -32);
		btModeSmall.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				setMode(MODE_SMALL);
			}
		});
		addView(btModeSmall);

		// Button detail
		ButtonView btModeDetail = new ButtonView(50, 20);
		btModeDetail.setString("detail");
		btModeDetail.setCharacterSize(FONT_SIZE);
		btModeDetail.setPosition(360, -32);
		btModeDetail.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				setMode(MODE_DETAIL);
			}
		});
		addView(btModeDetail);

		// Name
		_lbCount = new TextView(10, 10);
		_lbCount.setCharacterSize(FONT_SIZE_TITLE);
		_lbCount.setColor(Color.WHITE);
		_lbCount.setPosition(20, 22);
		addView(_lbCount);
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
		viewHolder.frame.resetPos();
	}

	protected void setModeDetail(ViewHolder viewHolder, int i) {
		viewHolder.frame.setPosition(20, 96 + ((CREW_DETAIL_HEIGHT + CREW_DETAIL_SPACING) * i));
		viewHolder.lbName.setPosition(32, 6);
		viewHolder.lbJob.setVisible(true);
		viewHolder.thumb.setVisible(true);
		viewHolder.lbStatus.setVisible(true);
		viewHolder.lbStatus.setPosition(32, Constant.UI_PADDING + 16);
		viewHolder.lbStatusShort.setVisible(false);
		viewHolder.frame.resetPos();
	}

	void  addCharacter(int index, final Character character) {
		if (index >= _viewHolderList.size()) {
			final ViewHolder viewHolder = new ViewHolder();

			// Frame
			viewHolder.frame = new FrameLayout(CREW_LINE_WIDTH, CREW_DETAIL_HEIGHT);
			viewHolder.frame.setOnFocusListener(new OnFocusListener() {
				@Override
				public void onExit(View view) {
					viewHolder.lbName.setColor(Colors.LINK_INACTIVE);
					viewHolder.lbName.setStyle(TextView.REGULAR);
					//view.setBackgroundColor(null);
				}

				@Override
				public void onEnter(View view) {
					viewHolder.lbName.setColor(Colors.LINK_ACTIVE);
					viewHolder.lbName.setStyle(TextView.UNDERLINED);
					//view.setBackgroundColor(new Color(40, 40, 80));
				}
			});
			addView(viewHolder.frame);

			// Name
			viewHolder.lbName = new TextView();
			viewHolder.lbName.setCharacterSize(FONT_SIZE);
			viewHolder.lbName.setPosition(0, 6);
			viewHolder.frame.addView(viewHolder.lbName);

			// Status
			viewHolder.lbStatus = new TextView();
			viewHolder.lbStatus.setCharacterSize(FONT_SIZE);
			viewHolder.frame.addView(viewHolder.lbStatus);

			// Status short
			viewHolder.lbStatusShort = new TextView(80, 20);
			viewHolder.lbStatusShort.setCharacterSize(FONT_SIZE);
			viewHolder.lbStatusShort.setVisible(false);

			viewHolder.frame.addView(viewHolder.lbStatusShort);

			// Job
			viewHolder.lbJob = new TextView();
			viewHolder.lbJob.setCharacterSize(12);
			viewHolder.lbJob.setPosition(260, 6);
			viewHolder.frame.addView(viewHolder.lbJob);

			// Profession
			viewHolder.lbProfession = new TextView();
			viewHolder.lbProfession.setCharacterSize(14);
			viewHolder.lbProfession.setVisible(false);
			viewHolder.lbProfession.setPosition(CREW_LINE_WIDTH - Constant.UI_PADDING - 100, Constant.UI_PADDING);
			viewHolder.frame.addView(viewHolder.lbProfession);

			viewHolder.thumb = new ImageView(SpriteManager.getInstance().getCharacter(character.getProfession(), 0, 0, 0));
			viewHolder.thumb.setPosition(0, 5);
			viewHolder.frame.addView(viewHolder.thumb);

			if (_mode == MODE_SMALL) {
				setModeSmall(viewHolder, index);
			} else {
				setModeDetail(viewHolder, index);
			}

			//		  // Function
			//		  Profession function = character.getProfession();
			//		  text.setString(function.getName());
			//		  text.setPosition(_posX + Constant.UI_PADDING + Constant.CHAR_WIDTH + Constant.UI_PADDING + (CREW_LINE_WIDTH * x),
			//						   _posY + Constant.UI_PADDING + (CREW_LINE_HEIGHT * y) + 22);
			//		  text.setColor(function.getColor());
			//		  _app.draw(text, _renderEffect);

			_viewHolderList.add(viewHolder);
		} else {
			final ViewHolder viewHolder = _viewHolderList.get(index);
			viewHolder.frame.setVisible(true);

			// Action
			viewHolder.frame.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					close();
					_ui.select(character);
				}
			});

			// Name
			viewHolder.lbName.setDashedString(character.getName(), "", NB_COLUMNS);
			viewHolder.lbName.setColor(viewHolder.frame.isFocus() ? Colors.LINK_ACTIVE : new Color(120, 255, 255));

			// Status
			viewHolder.lbStatus.setString(character.getStatus().getThoughts());
			viewHolder.lbStatus.setColor(character.getStatus().getColor());
			viewHolder.lbStatusShort.setString(character.getStatus().getThoughtsShort());
			viewHolder.lbStatusShort.setColor(character.getStatus().getColor());
			viewHolder.lbStatusShort.setPosition(376 - character.getStatus().getThoughtsShort().length() * 8, 2);

			// Job
			if (character.getJob() != null) {
				viewHolder.lbJob.setString(character.getJob().getShortLabel());
				viewHolder.lbJob.setColor(new Color(255, 255, 255));
				viewHolder.lbJob.setPosition(376 - character.getJob().getShortLabel().length() * 8, 6);
			} else {
				viewHolder.lbJob.setString(Strings.LB_NO_JOB);
				viewHolder.lbJob.setColor(new Color(255, 255, 255, 100));
				viewHolder.lbJob.setPosition(376 - Strings.LB_NO_JOB.length() * 8, 6);
			}
			// Profession
			viewHolder.lbProfession.setString(character.getProfession().getName());
		}

	}

	@Override
	public void onRefresh(int frame) {
		if (frame % 2 == 0) {
			
			for (ViewHolder holder: _viewHolderList) {
				holder.frame.setVisible(false);
			}
			
			List<Character> characters = _characterManager.getList();
			int i = 0;
			for (Character c: characters) {
				addCharacter(i++, c);
			}

			_lbCount.setDashedString("Count", String.valueOf(characters.size()), NB_COLUMNS_TITLE);
		}
	}

	public void setUI(UserInterface userInterface) {
		_ui = userInterface;
	}
}
