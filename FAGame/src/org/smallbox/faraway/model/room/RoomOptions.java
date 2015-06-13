package org.smallbox.faraway.model.room;

import org.smallbox.faraway.SpriteModel;
import org.smallbox.faraway.engine.ui.OnClickListener;

import java.util.ArrayList;
import java.util.List;

public class RoomOptions {
	public static class RoomOption {
		public String 			label;
		public SpriteModel icon;
		public OnClickListener 	onClickListener;

		public RoomOption(String label, SpriteModel icon, OnClickListener onClickListener) {
			this.label = label;
			this.icon = icon;
			this.onClickListener = onClickListener;
		}

		public RoomOption(String label, SpriteModel icon) {
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
