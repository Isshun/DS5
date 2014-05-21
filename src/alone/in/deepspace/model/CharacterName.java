package alone.in.deepspace.model;

import alone.in.deepspace.model.Character.Gender;

public class CharacterName {

	final static String[] firstname = {
			// male
			"Galen",
			"Lewis",
			"Benjamin",
			"Michael",
			"Jonathan",
			"Gaius",
			"Samuel",
			"Wesley",
			// female
			"Jadzia",
			"Janice",
			"Alice",
			"Kathryn",
			"Beverly",
			"Willow",
			"Tasha",
			"Samantha"
	};

	final static String[] shortFirstname = {
			// male
			"Matt",
			"Jack",
			"Adam",
			"Bill",
			"Tom",
			"Saul",
			"Lee",
			"Tom",
			// female
			"Tory",
			"Vic",
			"Ezri",
			"Ellen",
			"Kara",
			"Emma",
			"Wade",
			"Amy"
	};

	final static String[] middlename = {
			"Crashdown",
			"Hardball",
			"Apollo",
			"Boomer",
			"Doc",
			"Starbuck",
			"Hotdog",
			"Jammer",
			"Trip",
			"Helo",
			"Dee",
			"Oz",
			"Klaus",
			"Mac",
			"Betty",
			"Six"
	};

	final static String[] shortLastname = {
			"Mudd",
			"Dax",
			"Nerys",
			"Laren",
			"Rand",
			"McCoy",
			"Adama",
			"Tyrol",
			"Reed",
			"Sisko",
			"Riker",
			"Wells",
			"Quinn",
			"Weir",
			"Rush",
			"Tyler"
	};

	final static String[] lastname = {
			"Zimmerman",
			"Anders",
			"Barclay",
			"Archer",
			"Thrace",
			"Summers",
			"Holmes",
			"Wildman",
			"Lawton",
			"Mallory",
			"Beckett",
			"Hammond",
			"O'Neill",
			"Sheppard",
			"Cooper",
			"Hartness"
	};

	public static String getShortFirstname(Gender gender) {
		int offset = (gender == Character.Gender.GENDER_FEMALE ? 8 : 0);
		return shortFirstname[(int)(Math.random() * 1000) % 8 + offset];
	}

	public static String getMiddlename() {
		return middlename[(int)(Math.random() * 1000) % 16];
	}

	public static String getShortLastName() {
		return shortLastname[(int)(Math.random() * 1000) % 16];
	}

	public static String getFirstname(Gender gender) {
		int offset = (gender == Character.Gender.GENDER_FEMALE ? 8 : 0);
		return firstname[(int)(Math.random() * 1000) % 8 + offset];
	}

	public static String getLastName() {
		return lastname[(int)(Math.random() * 1000) % 16];
	}

}
