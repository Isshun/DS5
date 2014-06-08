package alone.in.deepspace.model.room;

import org.jsfml.graphics.Sprite;

import alone.in.deepspace.engine.ui.OnClickListener;

public class RoomOption {
	public String 			label;
	public Sprite			icon;
	public OnClickListener 	onClickListener;

	public RoomOption(String label, Sprite icon, OnClickListener onClickListener) {
		this.label = label;
		this.icon = icon;
		this.onClickListener = onClickListener;
	}

}
