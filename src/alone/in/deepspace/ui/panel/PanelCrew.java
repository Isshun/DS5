package alone.in.deepspace.ui.panel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard;

import alone.in.deepspace.Strings;
import alone.in.deepspace.engine.ui.ButtonView;
import alone.in.deepspace.engine.ui.FrameLayout;
import alone.in.deepspace.engine.ui.ImageView;
import alone.in.deepspace.engine.ui.OnClickListener;
import alone.in.deepspace.engine.ui.OnFocusListener;
import alone.in.deepspace.engine.ui.TextView;
import alone.in.deepspace.engine.ui.View;
import alone.in.deepspace.manager.CharacterManager;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.manager.SpriteManager;
import alone.in.deepspace.model.character.Character;
import alone.in.deepspace.ui.UserInterface;
import alone.in.deepspace.ui.UserSubInterface;
import alone.in.deepspace.util.Constant;

public class PanelCrew extends UserSubInterface {

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

	private static final int 	FRAME_WIDTH = Constant.PANEL_WIDTH;
	private static final int	FRAME_HEIGHT = Constant.WINDOW_HEIGHT;
	private static final int 	CREW_DETAIL_SPACING = 10;
	private static final int 	CREW_LINE_SPACING = 2;
	private static final int 	CREW_DETAIL_HEIGHT = 52;
	private static final int 	CREW_LINE_HEIGHT = 22;
	private static final int 	CREW_LINE_WIDTH  = FRAME_WIDTH - Constant.UI_PADDING * 2;

	private CharacterManager    _characterManager;
	private List<ViewHolder> 	_viewHolderList;
	private TextView 			_lbCount;
	private UserInterface 		_ui;
	protected int 				_mode;

	public PanelCrew(RenderWindow app, int tileIndex) throws IOException {
		super(app, tileIndex, new Vector2f(Constant.WINDOW_WIDTH - FRAME_WIDTH, 32), new Vector2f(FRAME_WIDTH, FRAME_HEIGHT - 32), null);

		_viewHolderList = new ArrayList<ViewHolder>();
		_characterManager = ServiceManager.getCharacterManager();

		// Button small
		ButtonView btModeSmall = new ButtonView(new Vector2f(50, 20));
		btModeSmall.setString("small");
		btModeSmall.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				setMode(MODE_SMALL);
			}
		});
		addView(btModeSmall);

		// Button detail
		ButtonView btModeDetail = new ButtonView(new Vector2f(50, 20));
		btModeDetail.setString("detail");
		btModeDetail.setPosition(80, 0);
		btModeDetail.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				setMode(MODE_DETAIL);
			}
		});
		addView(btModeDetail);

		// Name
		_lbCount = new TextView(new Vector2f(10, 10));
		_lbCount.setCharacterSize(20);
		_lbCount.setColor(Color.WHITE);
		_lbCount.setPosition(new Vector2f(10, 22));
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
		viewHolder.frame.setPosition(Constant.UI_PADDING, 38 + Constant.UI_PADDING + ((CREW_LINE_HEIGHT + CREW_LINE_SPACING) * i));
		viewHolder.frame.setSize(new Vector2f(CREW_LINE_WIDTH, CREW_LINE_HEIGHT));
		viewHolder.lbName.setPosition(6, 2);
		viewHolder.lbJob.setVisible(false);
		viewHolder.lbProfession.setVisible(false);
		viewHolder.thumb.setVisible(false);
		viewHolder.lbStatus.setVisible(false);
		viewHolder.lbStatusShort.setVisible(true);
		viewHolder.frame.resetPos();
	}

	protected void setModeDetail(ViewHolder viewHolder, int i) {
		viewHolder.frame.setPosition(Constant.UI_PADDING, 38 + Constant.UI_PADDING + ((CREW_DETAIL_HEIGHT + CREW_DETAIL_SPACING) * i));
		viewHolder.lbName.setPosition(Constant.UI_PADDING + 32, 6);
		viewHolder.lbJob.setVisible(true);
		viewHolder.thumb.setVisible(true);
		viewHolder.lbStatus.setVisible(true);
		viewHolder.lbStatusShort.setVisible(false);
		viewHolder.frame.resetPos();
	}

	void  addCharacter(int index, final Character character) {
		if (index >= _viewHolderList.size()) {
			final ViewHolder viewHolder = new ViewHolder();

			// Frame
			viewHolder.frame = new FrameLayout(new Vector2f(CREW_LINE_WIDTH, CREW_DETAIL_HEIGHT));
			viewHolder.frame.setPosition(Constant.UI_PADDING, 38 + Constant.UI_PADDING + ((CREW_DETAIL_HEIGHT + CREW_DETAIL_SPACING) * index));
			viewHolder.frame.setOnFocusListener(new OnFocusListener() {
				@Override
				public void onExit(View view) {
					view.setBackgroundColor(null);
				}

				@Override
				public void onEnter(View view) {
					view.setBackgroundColor(new Color(40, 40, 80));
				}
			});
			addView(viewHolder.frame);

			// Name
			viewHolder.lbName = new TextView();
			viewHolder.lbName.setCharacterSize(14);
			viewHolder.lbName.setPosition(Constant.UI_PADDING + 32, 6);
			viewHolder.frame.addView(viewHolder.lbName);

			// Status
			viewHolder.lbStatus = new TextView();
			viewHolder.lbStatus.setCharacterSize(14);
			viewHolder.lbStatus.setPosition(Constant.UI_PADDING + 32, Constant.UI_PADDING + 16);
			viewHolder.frame.addView(viewHolder.lbStatus);

			// Status short
			viewHolder.lbStatusShort = new TextView(new Vector2f(80, 20));
			viewHolder.lbStatusShort.setCharacterSize(14);
			viewHolder.lbStatusShort.setVisible(false);
			viewHolder.lbStatusShort.setPosition(Constant.UI_PADDING + 300, 2);

			viewHolder.frame.addView(viewHolder.lbStatusShort);

			// Job
			viewHolder.lbJob = new TextView();
			viewHolder.lbJob.setCharacterSize(12);
			viewHolder.lbJob.setPosition(Constant.UI_PADDING + 260, 6);
			viewHolder.frame.addView(viewHolder.lbJob);

			// Profession
			viewHolder.lbProfession = new TextView();
			viewHolder.lbProfession.setCharacterSize(14);
			viewHolder.lbProfession.setVisible(false);
			viewHolder.lbProfession.setPosition(CREW_LINE_WIDTH - Constant.UI_PADDING - 100, Constant.UI_PADDING);
			viewHolder.frame.addView(viewHolder.lbProfession);

			viewHolder.thumb = new ImageView(SpriteManager.getInstance().getCharacter(character.getProfession(), 0, 0, 0));
			viewHolder.thumb.setPosition(8, 5);
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
			//		  _app.draw(text, _render);

			_viewHolderList.add(viewHolder);
		} else {
			final ViewHolder viewHolder = _viewHolderList.get(index);
			viewHolder.frame.setVisible(true);

			// Action
			viewHolder.frame.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					close();
					_ui.setCharacter(character);
				}
			});

			// Name
			viewHolder.lbName.setString(character.getName());
			//viewHolder.lbName.setColor(character.getColor());
			viewHolder.lbName.setColor(new Color(120, 255, 255));

			// Status
			viewHolder.lbStatus.setString(character.getStatus().getThoughts());
			viewHolder.lbStatus.setColor(character.getStatus().getColor());
			viewHolder.lbStatusShort.setString(character.getStatus().getThoughtsShort());
			viewHolder.lbStatusShort.setBackgroundColor(character.getStatus().getColor());

			// Job
			if (character.getJob() != null) {
				viewHolder.lbJob.setString(character.getJob().getShortLabel());
				viewHolder.lbJob.setColor(new Color(255, 255, 255));
			} else {
				viewHolder.lbJob.setString(Strings.LB_NO_JOB);
				viewHolder.lbJob.setColor(new Color(255, 255, 255, 100));
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

			_lbCount.setString("Count: " + characters.size());

		}
	}

	public void setUI(UserInterface userInterface) {
		_ui = userInterface;
	}
}
