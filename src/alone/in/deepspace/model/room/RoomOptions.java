package alone.in.deepspace.model.room;

import java.util.ArrayList;
import java.util.List;

import org.jsfml.graphics.Sprite;

import alone.in.deepspace.engine.ui.OnClickListener;

public class RoomOptions {
	public static class RoomOption {
		public String 			label;
		public Sprite			icon;
		public OnClickListener 	onClickListener;

		public RoomOption(String label, Sprite icon, OnClickListener onClickListener) {
			this.label = label;
			this.icon = icon;
			this.onClickListener = onClickListener;
		}

		public RoomOption(String label, Sprite icon) {
			this.label = label;
			this.icon = icon;
		}


		public RoomOption(String label) {
			this.label = label;
		}
	}

	public List<RoomOption>	options;
	public String 			title;
	
	public RoomOptions() {
		this.options = new ArrayList<RoomOption>();
	}
}
