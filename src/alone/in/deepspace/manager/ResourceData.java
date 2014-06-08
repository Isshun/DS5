package alone.in.deepspace.manager;

import alone.in.deepspace.model.ToolTips.ToolTip;

public class ResourceData {

	final public String label;
	public int 			value;
	public ToolTip 		tooltip;
	
	public ResourceData(String label, ToolTip tooltip) {
		this.label = label;
		this.tooltip = tooltip;
	}

}
