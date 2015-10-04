//package org.smallbox.faraway.ui.panel;
//
//import org.smallbox.faraway.engine.Color;
//import org.smallbox.faraway.game.module.ModuleManager;
//import org.smallbox.faraway.ui.UserInterface.Mode;
//import org.smallbox.faraway.ui.engine.ViewFactory;
//import org.smallbox.faraway.ui.engine.view.UILabel;
//import org.smallbox.faraway.util.Constant;
//
//public class PanelResource extends BasePanel {
//	private static final int 	FRAME_WIDTH = Constant.PANEL_WIDTH;
//	private static final int 	FRAME_HEIGHT = 32;
//
//	private UILabel _spice;
//	private UILabel _energy;
//	private UILabel _matter;
//	private UILabel _o2;
//	private ResourceModule _resourceModule;
//
//	public PanelResource() {
//		super(Mode.NONE, null, Constant.WINDOW_WIDTH - FRAME_WIDTH, 0, FRAME_WIDTH, FRAME_HEIGHT, null);
//	}
//
//	@Override
//	protected void onCreate(ViewFactory viewFactory) {
//		_resourceModule = (ResourceModule) ModuleManager.getInstance().getModule(ResourceModule.class);
//
//		_spice = viewFactory.createTextView(10, 10);
//		_spice.setTextSize(14);
//		_spice.setTextColor(Color.WHITE);
//		_spice.setPosition(10, 6);
//		addView(_spice);
//
//		_energy = viewFactory.createTextView(10, 10);
//		_energy.setTextSize(14);
//		_energy.setTextColor(Color.WHITE);
//		_energy.setPosition(110, 6);
//		addView(_energy);
//
//		_matter = viewFactory.createTextView(10, 10);
//		_matter.setTextSize(14);
//		_matter.setTextColor(Color.WHITE);
//		_matter.setPosition(210, 6);
//		addView(_matter);
//
//		_o2 = viewFactory.createTextView(10, 10);
//		_o2.setTextSize(14);
//		_o2.setTextColor(Color.WHITE);
//		_o2.setPosition(332, 6);
//		addView(_o2);
//
//		setAlwaysVisible(true);
//	}
//
//	@Override
//	public void onRefresh(int frame) {
//        _spice.setText("Food: " + String.valueOf(_resourceModule.getFood().value));
//        _o2.setText("O2: " + String.valueOf(_resourceModule.getO2().value));
//        _energy.setText("PW: " + String.valueOf(_resourceModule.getPower().value));
//        _matter.setText("M: " + String.valueOf(_resourceModule.getScience().value));
//	}
//}
