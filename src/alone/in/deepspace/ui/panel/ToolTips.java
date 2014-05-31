package alone.in.deepspace.ui.panel;




public class ToolTips {

	public static class ToolTip {
		public final String title;
		public final String content;
		
		public ToolTip(String title, String content) {
			this.title = title;
			this.content = content;
		}
	}

	public static class ToolTipCategory {
		public final String 		title;
		public final ToolTip[]		tooltips;
		
		public ToolTipCategory(String title, ToolTip[] tooltips) {
			this.title = title;
			this.tooltips = tooltips;
		}

		public boolean contains(ToolTip toolTip) {
			for (ToolTip t: tooltips) {
				if (t.equals(toolTip)) {
					return true;
				}
			}
			return false;
		}
	}
	
	public static final ToolTip PROFESSION_CHILD = new ToolTip("Children", "play and study, can help for basic task like deliveries or gathering");
	public static final ToolTip PROFESSION_DOCTOR = new ToolTip("Doctor", "A polyvalent medic, cure your people and make some medicines");
	public static final ToolTip PROFESSION_ENGINEER = new ToolTip("Engineer", "Engineer are experts in building and technical crafting");
	public static final ToolTip PROFESSION_NONE = new ToolTip("Unqualified", "This character as no qualification");
	public static final ToolTip PROFESSION_OPERATION = new ToolTip("Technician", "People from operation assits others by refill dispenser and delivering items");
	public static final ToolTip PROFESSION_SCIENCE = new ToolTip("Scientist", "Scientist use there brains to ensure the future of the colony");
	public static final ToolTip PROFESSION_SECURITY = new ToolTip("Guard", "Help to secure the perimeter and fight zoombies");
	public static final ToolTip PROFESSION_STUDENT = new ToolTip("Student", "Student can become an engineer, scientist or doctor, it's take a lot of time");
	public static final ToolTip STATE_STARVING = new ToolTip("Starving", "Your people are starving.\n- Make sure your storages contain enough ingredients (vegetable, seaweed, fish).\n- Launch food production from Caretaker menu");
	
	public static final ToolTipCategory[] categories = {
		new ToolTipCategory("Profession", new ToolTip[] {PROFESSION_CHILD, PROFESSION_DOCTOR, PROFESSION_ENGINEER, PROFESSION_NONE, PROFESSION_OPERATION, PROFESSION_SCIENCE, PROFESSION_SECURITY, PROFESSION_STUDENT}),
		new ToolTipCategory("State", new ToolTip[] {STATE_STARVING})
	};
	

}
