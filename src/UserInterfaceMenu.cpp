#include "UserInterfaceMenu.h"
#include "BaseItem.h"

Entry	entries_main[] = {
  {UserInterfaceMenu::CODE_BUILD,	"build",		"b",	sf::Keyboard::B,		0},
  {UserInterfaceMenu::CODE_ZONE,	"zone",			"z",	sf::Keyboard::Z,		0},
  {UserInterfaceMenu::CODE_ERASE,	"erase",		"e",	sf::Keyboard::E,		0},
  {UserInterfaceMenu::CODE_CREW,	"crew",			"c",	-1,		0},
  {UserInterfaceMenu::CODE_NONE,	NULL,			NULL,	0,		0}
};

Entry	entries_zone[] = {
  {UserInterfaceMenu::CODE_ZONE_SICKBAY,	"sickbay",		"s",	sf::Keyboard::S,		0},
  {UserInterfaceMenu::CODE_ZONE_ENGINE,		"engine",		"e",	sf::Keyboard::E,		0},
  {UserInterfaceMenu::CODE_ZONE_HOLODECK,	"holodeck",		"h",	sf::Keyboard::H,		0},
  {UserInterfaceMenu::CODE_ZONE_QUARTER,	"quarter",		"q",	sf::Keyboard::Q,		0},
  {UserInterfaceMenu::CODE_ZONE_BAR,		"bar",			"b",	sf::Keyboard::B,		0},
  {UserInterfaceMenu::CODE_ZONE_OPERATION,	"operation",	"o",	sf::Keyboard::O,		0},
  {UserInterfaceMenu::CODE_NONE,			NULL,			NULL,	0,						0}
};

Entry	entries_build[] = {
  {UserInterfaceMenu::CODE_BUILD_STRUCTURE,	"structure",		"s",	sf::Keyboard::S,		0},
  {UserInterfaceMenu::CODE_BUILD_SICKBAY,	"sickbay",			"si",	sf::Keyboard::I,		0},
  {UserInterfaceMenu::CODE_BUILD_ENGINE,	"engine",			"e",	sf::Keyboard::E,		0},
  {UserInterfaceMenu::CODE_BUILD_HOLODECK,	"holodeck",			"h",	sf::Keyboard::H,		0},
  // {UserInterfaceMenu::CODE_BUILD_ARBORETUM,	"arboretum",		"a",	sf::Keyboard::A,		0},
  // {UserInterfaceMenu::CODE_BUILD_GYMNASIUM,	"gymnasium",		"g",	sf::Keyboard::G,		0},
  // {UserInterfaceMenu::CODE_BUILD_SCHOOL,	"school",			"s",	sf::Keyboard::W,		0},
  {UserInterfaceMenu::CODE_BUILD_BAR,		"bar",				"b",	sf::Keyboard::B,		0},
  // {UserInterfaceMenu::CODE_BUILD_AMPHITHEATER,	"entertainment","en",	sf::Keyboard::N,		0},
  {UserInterfaceMenu::CODE_BUILD_QUARTER,	"residence",		"r",	sf::Keyboard::R,		0},
  {UserInterfaceMenu::CODE_BUILD_ENVIRONMENT,	"environment",	"env",	sf::Keyboard::V,		0},
  {UserInterfaceMenu::CODE_BUILD_TRANSPORTATION,"transportation","t",	sf::Keyboard::T,		0},
  // {UserInterfaceMenu::CODE_BUILD_TACTICAL,	"defense",			"d",	sf::Keyboard::D,		0},
  {UserInterfaceMenu::CODE_BUILD_SCIENCE,	"science",			"sc",	sf::Keyboard::C,		0},
  {UserInterfaceMenu::CODE_NONE,	NULL,			NULL,	0,		0}
};

Entry	entries_build_structure[] = {
  {UserInterfaceMenu::CODE_BUILD_ITEM,	"room",		"r",	sf::Keyboard::R,		BaseItem::STRUCTURE_ROOM},
  {UserInterfaceMenu::CODE_BUILD_ITEM,	"door",     "d",	sf::Keyboard::D,		BaseItem::STRUCTURE_DOOR},
  {UserInterfaceMenu::CODE_BUILD_ITEM,	"floor",	"f",	sf::Keyboard::F,		BaseItem::STRUCTURE_FLOOR},
  {UserInterfaceMenu::CODE_BUILD_ITEM,	"wall",		"w",	sf::Keyboard::W,		BaseItem::STRUCTURE_WALL},
  {UserInterfaceMenu::CODE_BUILD_ITEM,	"hull",		"h",	sf::Keyboard::H,		BaseItem::STRUCTURE_HULL},
  {UserInterfaceMenu::CODE_BUILD_ITEM,	"window",	"wi",	sf::Keyboard::I,		BaseItem::STRUCTURE_WINDOW},
  {UserInterfaceMenu::CODE_NONE,	NULL,			NULL,	0,		0}
};

Entry	entries_build_sickbay[] = {
  {UserInterfaceMenu::CODE_BUILD_ITEM,	"biobed",	"b",	sf::Keyboard::B,		BaseItem::SICKBAY_BIOBED},
  {UserInterfaceMenu::CODE_BUILD_ITEM,	"lab",		"l",	sf::Keyboard::I,		BaseItem::SICKBAY_LAB},
  {UserInterfaceMenu::CODE_BUILD_ITEM,	"emergency shelters", "e",	sf::Keyboard::E,		BaseItem::SICKBAY_EMERGENCY_SHELTERS},
  {UserInterfaceMenu::CODE_NONE,	NULL,			NULL,	0,		0}
};

Entry	entries_build_engine[] = {
  {UserInterfaceMenu::CODE_BUILD_ITEM,	"control center",	"c",	sf::Keyboard::C,	BaseItem::ENGINE_CONTROL_CENTER},
  {UserInterfaceMenu::CODE_BUILD_ITEM,	"reaction chamber",	"r",	sf::Keyboard::R,	BaseItem::ENGINE_REACTION_CHAMBER},
  {UserInterfaceMenu::CODE_NONE,	NULL,			NULL,	0,		0}
};

Entry	entries_build_holodeck[] = {
  {UserInterfaceMenu::CODE_BUILD_ITEM,	"hologrid",	"h",	sf::Keyboard::H,	BaseItem::HOLODECK_GRID},
  {UserInterfaceMenu::CODE_NONE,	NULL,			NULL,	0,		0}
};

Entry	entries_build_arboretum[] = {
  {UserInterfaceMenu::CODE_BUILD_ITEM,	"tree 1",	"1",	sf::Keyboard::Num1,	BaseItem::ARBORETUM_TREE_1},
  {UserInterfaceMenu::CODE_BUILD_ITEM,	"tree 2",	"2",	sf::Keyboard::Num2,	BaseItem::ARBORETUM_TREE_2},
  {UserInterfaceMenu::CODE_BUILD_ITEM,	"tree 3",	"3",	sf::Keyboard::Num3,	BaseItem::ARBORETUM_TREE_3},
  {UserInterfaceMenu::CODE_BUILD_ITEM,	"tree 4",	"4",	sf::Keyboard::Num4,	BaseItem::ARBORETUM_TREE_4},
  {UserInterfaceMenu::CODE_BUILD_ITEM,	"tree 5",	"5",	sf::Keyboard::Num5,	BaseItem::ARBORETUM_TREE_5},
  {UserInterfaceMenu::CODE_BUILD_ITEM,	"tree 6",	"6",	sf::Keyboard::Num6,	BaseItem::ARBORETUM_TREE_6},
  {UserInterfaceMenu::CODE_BUILD_ITEM,	"tree 7",	"7",	sf::Keyboard::Num7,	BaseItem::ARBORETUM_TREE_7},
  {UserInterfaceMenu::CODE_BUILD_ITEM,	"tree 8",	"8",	sf::Keyboard::Num8,	BaseItem::ARBORETUM_TREE_8},
  {UserInterfaceMenu::CODE_BUILD_ITEM,	"tree 9",	"9",	sf::Keyboard::Num9,	BaseItem::ARBORETUM_TREE_9},
  {UserInterfaceMenu::CODE_NONE,	NULL,			NULL,	0,		0}
};

Entry	entries_build_gymnasium[] = {
  {UserInterfaceMenu::CODE_BUILD_ITEM,	"gymnasium stuff 1",	"1",	sf::Keyboard::Num1,	BaseItem::GYMNASIUM_STUFF_1},
  {UserInterfaceMenu::CODE_BUILD_ITEM,	"gymnasium stuff 2",	"2",	sf::Keyboard::Num2,	BaseItem::GYMNASIUM_STUFF_2},
  {UserInterfaceMenu::CODE_BUILD_ITEM,	"gymnasium stuff 3",	"3",	sf::Keyboard::Num3,	BaseItem::GYMNASIUM_STUFF_3},
  {UserInterfaceMenu::CODE_BUILD_ITEM,	"gymnasium stuff 4",	"4",	sf::Keyboard::Num4,	BaseItem::GYMNASIUM_STUFF_4},
  {UserInterfaceMenu::CODE_BUILD_ITEM,	"gymnasium stuff 5",	"5",	sf::Keyboard::Num5,	BaseItem::GYMNASIUM_STUFF_5},
  {UserInterfaceMenu::CODE_NONE,	NULL,			NULL,	0,		0}
};

Entry	entries_build_bar[] = {
  {UserInterfaceMenu::CODE_BUILD_ITEM,	"pub",	"p",	sf::Keyboard::P,	BaseItem::BAR_PUB},
  {UserInterfaceMenu::CODE_NONE,	NULL,			NULL,	0,		0}
};

Entry	entries_build_amphitheater[] = {
  {UserInterfaceMenu::CODE_BUILD_ITEM,	"stage",	"s",	sf::Keyboard::S,	BaseItem::AMPHITHEATER_STAGE},
  {UserInterfaceMenu::CODE_NONE,	NULL,			NULL,	0,		0}
};

Entry	entries_build_quarter[] = {
  {UserInterfaceMenu::CODE_BUILD_ITEM,	"bed",	"b",			sf::Keyboard::B,	BaseItem::QUARTER_BED},
  {UserInterfaceMenu::CODE_BUILD_ITEM,	"desk",	"d",			sf::Keyboard::D,	BaseItem::QUARTER_DESK},
  {UserInterfaceMenu::CODE_BUILD_ITEM,	"chair",	"c",		sf::Keyboard::C,	BaseItem::QUARTER_CHAIR},
  {UserInterfaceMenu::CODE_BUILD_ITEM,	"wardrobe",	"w",		sf::Keyboard::W,	BaseItem::QUARTER_WARDROBE},
  {UserInterfaceMenu::CODE_BUILD_ITEM,	"chest of drawers",	"ch",sf::Keyboard::H,	BaseItem::QUARTER_CHEST},
  {UserInterfaceMenu::CODE_BUILD_ITEM,	"bedside table", "be",	sf::Keyboard::E,	BaseItem::QUARTER_BEDSIDE_TABLE},
  {UserInterfaceMenu::CODE_NONE,	NULL,			NULL,	0,		0}
};

Entry	entries_build_environment[] = {
  {UserInterfaceMenu::CODE_BUILD_ITEM,	"O2 recycler",	"O",	sf::Keyboard::O,	BaseItem::ENVIRONMENT_O2_RECYCLER},
  {UserInterfaceMenu::CODE_BUILD_ITEM,	"temperature regulation",	"t",	sf::Keyboard::T,	BaseItem::ENVIRONMENT_TEMPERATURE_REGULATION},
  {UserInterfaceMenu::CODE_NONE,	NULL,			NULL,	0,		0}
};

Entry	entries_build_transportation[] = {
  {UserInterfaceMenu::CODE_BUILD_ITEM,	"shuttlecraft",	"s",	sf::Keyboard::S,	BaseItem::TRANSPORTATION_SHUTTLECRAFT},
  {UserInterfaceMenu::CODE_BUILD_ITEM,	"cargo",		"c",	sf::Keyboard::C,	BaseItem::TRANSPORTATION_CARGO},
  {UserInterfaceMenu::CODE_BUILD_ITEM,	"container",	"co",	sf::Keyboard::C,	BaseItem::TRANSPORTATION_CONTAINER},
  {UserInterfaceMenu::CODE_BUILD_ITEM,	"transporter systems",	"t",	sf::Keyboard::T,	BaseItem::TRANSPORTATION_TRANSPORTER_SYSTEMS},
  {UserInterfaceMenu::CODE_NONE,	NULL,			NULL,	0,		0}
};

Entry	entries_build_tactical[] = {
  {UserInterfaceMenu::CODE_BUILD_ITEM,	"photon torpedo",	"photon t",	sf::Keyboard::T,	BaseItem::TACTICAL_PHOTON_TORPEDO},
  {UserInterfaceMenu::CODE_BUILD_ITEM,	"phaser",			"p",		sf::Keyboard::P,	BaseItem::TACTICAL_PHASER},
  {UserInterfaceMenu::CODE_BUILD_ITEM,	"shield grid",		"s",		sf::Keyboard::S,	BaseItem::TACTICAL_SHIELD_GRID},
  {UserInterfaceMenu::CODE_BUILD_ITEM,	"cloaking device",	"c",		sf::Keyboard::C,	BaseItem::TACTICAL_CLOAKING_DEVICE},
  {UserInterfaceMenu::CODE_NONE,	NULL,			NULL,	0,		0}
};

Entry	entries_build_science[] = {
  {UserInterfaceMenu::CODE_BUILD_ITEM,	"hydroponics",	"h",		sf::Keyboard::H,	BaseItem::SCIENCE_HYDROPONICS},
  {UserInterfaceMenu::CODE_NONE,	NULL,			NULL,	0,		0}
};

UserInterfaceMenu::UserInterfaceMenu(sf::RenderWindow* app, WorldMap* worldmap, Cursor* cursor) {
  Debug() << "UserInterfaceMenu";

  _worldmap = worldmap;
  _cursor = cursor;
  _entries = entries_main;
  _code = CODE_MAIN;
  _app = app;
  _posX = 0;
  _posY = 0;

  // Background
  sf::Texture* texture = new sf::Texture();
  texture->loadFromFile("../res/background_menu.png");
  _background = new sf::Sprite();
  _background->setTexture(*texture);
  _background->setTextureRect(sf::IntRect(0, 0, 200, 200));

  if (!_font.loadFromFile("../snap/xolonium/Xolonium-Regular.otf"))
	throw(std::string("failed to load: ").append("../snap/xolonium/Xolonium-Regular.otf").c_str());
}

UserInterfaceMenu::~UserInterfaceMenu() {
}

void	UserInterfaceMenu::mousePressed(sf::Mouse::Button button, int x, int y) {
  int index = ((y - _posY + UI_PADDING) / UI_FONT_SIZE) - 1;

  switch (button) {
      
  case sf::Mouse::Left:
    for (int i = 0; i <= index && _entries[i].code != UserInterfaceMenu::CODE_NONE; i++) {
      if (i == index) {
        openMenu(_entries[index]);
      }
    }
    break;

  case sf::Mouse::Right:
    openBack();
    break;

  }
}

void UserInterfaceMenu::setBuildMenu(int code) {
  std::cout << "setBuildMenu: " << code << std::endl;

  switch (code) {
  case CODE_BUILD_STRUCTURE:	_entries = entries_build_structure; break;
  case CODE_BUILD_SICKBAY:		_entries = entries_build_sickbay; break;
  case CODE_BUILD_ENGINE:		_entries = entries_build_engine; break;
  case CODE_BUILD_HOLODECK:		_entries = entries_build_holodeck; break;
  case CODE_BUILD_ARBORETUM:	_entries = entries_build_arboretum; break;
  case CODE_BUILD_GYMNASIUM:	_entries = entries_build_gymnasium; break;
  case CODE_BUILD_BAR:			_entries = entries_build_bar; break;
  case CODE_BUILD_AMPHITHEATER: _entries = entries_build_amphitheater; break;
  case CODE_BUILD_QUARTER:		_entries = entries_build_quarter; break;
  case CODE_BUILD_ENVIRONMENT:	_entries = entries_build_environment; break;
  case CODE_BUILD_TRANSPORTATION: _entries = entries_build_transportation; break;
  case CODE_BUILD_TACTICAL:		_entries = entries_build_tactical; break;
  case CODE_BUILD_SCIENCE:		_entries = entries_build_science; break;
  }
}

void	UserInterfaceMenu::openRoot() {
  _entries = entries_main;
  _code = CODE_MAIN;
  _parent_code = CODE_MAIN;
}


void    UserInterfaceMenu::openBack() {  
  std::cout << "CODE BACK: " << _parent_code << std::endl;

  _code = _parent_code;

  switch(_code) {

  case CODE_BUILD_STRUCTURE:
  case CODE_BUILD_SICKBAY:
  case CODE_BUILD_ENGINE:
  case CODE_BUILD_HOLODECK:
  case CODE_BUILD_ARBORETUM:
  case CODE_BUILD_GYMNASIUM:
  case CODE_BUILD_BAR:
  case CODE_BUILD_AMPHITHEATER:
  case CODE_BUILD_QUARTER:
  case CODE_BUILD_ENVIRONMENT:
  case CODE_BUILD_TRANSPORTATION:
  case CODE_BUILD_TACTICAL:
  case CODE_BUILD_SCIENCE:
    setBuildMenu(_code);
    _parent_code = CODE_BUILD;
    break;

  case CODE_BUILD:
    _entries = entries_build;
    _parent_code = CODE_MAIN;
    break;

  case CODE_ZONE:
    _entries = entries_zone;
    _parent_code = CODE_MAIN;
    break;

  case CODE_ERASE:
	drawModeErase();
    _parent_code = CODE_MAIN;
    break;

  case CODE_BUILD_ITEM:
    break;
  
  default:
    _entries = entries_main;
    break;
  }
}

void UserInterfaceMenu::setBuildItem(int code, const char* text, int type) {
  std::cout << "setBuildItem: " << code << std::endl;

  _buildItemType = type;
  _buildItemText = text;
}

void    UserInterfaceMenu::openMenu(Entry entry) {
  switch (_code) {

  case CODE_BUILD:
    _parent_code = _code;
    _code = entry.code;
    setBuildMenu(entry.code);
    break;

  case CODE_BUILD_STRUCTURE:
  case CODE_BUILD_SICKBAY:
  case CODE_BUILD_ENGINE:
  case CODE_BUILD_HOLODECK:
  case CODE_BUILD_ARBORETUM:
  case CODE_BUILD_GYMNASIUM:
  case CODE_BUILD_BAR:
  case CODE_BUILD_AMPHITHEATER:
  case CODE_BUILD_QUARTER:
  case CODE_BUILD_ENVIRONMENT:
  case CODE_BUILD_TRANSPORTATION:
  case CODE_BUILD_TACTICAL:
  case CODE_BUILD_SCIENCE:
    _parent_code = _code;
    _code = entry.code;
    setBuildItem(entry.code, entry.text, entry.data);
    break;

  case CODE_ERASE:
	Info() << "erase" ;
	drawModeErase();
    _parent_code = CODE_MAIN;
    break;

  case CODE_MAIN:
    _parent_code = _code;
    _code = entry.code;

    if (entry.code == CODE_BUILD)
      _entries = entries_build;

    if (entry.code == CODE_ZONE)
      _entries = entries_zone;

	break;
  }
}

bool  UserInterfaceMenu::checkKeyboard(int code, int posX, int posY) {
  Debug() << "checkKeyboard: " << code;

  for (int i = 0; _entries[i].code != UserInterfaceMenu::CODE_NONE; i++) {
    if (_entries[i].key == code) {
	  if (_code == CODE_ZONE) {
		Debug() << "set zone: " << _entries[i].code;
		//_worldmap->setZone(posX, posY, _entries[i].code);
	  } else {
		openMenu(_entries[i]);
	  }
      return true;
    }
  }

  return false;
}

void UserInterfaceMenu::drawModeBuild() {
  sf::Text shortcut;
  shortcut.setString(_buildItemText);
  shortcut.setFont(_font);
  shortcut.setCharacterSize(UI_FONT_SIZE);
  shortcut.setStyle(sf::Text::Underlined);
  shortcut.setColor(sf::Color(255, 255, 0));
  shortcut.setPosition(UI_PADDING + 0, UI_PADDING + 0);
  _app->draw(shortcut);
}

void UserInterfaceMenu::drawModeErase() {
  sf::Text shortcut;
  shortcut.setString("erase");
  shortcut.setFont(_font);
  shortcut.setCharacterSize(UI_FONT_SIZE);
  shortcut.setStyle(sf::Text::Underlined);
  shortcut.setColor(sf::Color(255, 255, 0));
  shortcut.setPosition(UI_PADDING + 0, UI_PADDING + 0);
  _app->draw(shortcut);
}

void	UserInterfaceMenu::refreshMenu(int frame) {

  _app->draw(*_background);

  switch (_code) {
  case CODE_BUILD_ITEM:
	drawModeBuild();
	break;
  case CODE_ERASE:
	drawModeErase();
	break;
  default:
	for (int i = 0; _entries[i].code != UserInterfaceMenu::CODE_NONE; i++) {
	  sf::Text text;
	  text.setString(_entries[i].text);
	  text.setFont(_font);
	  text.setCharacterSize(UI_FONT_SIZE);
	  text.setStyle(sf::Text::Regular);
	  text.setPosition(UI_PADDING + 0, UI_PADDING + i * UI_FONT_SIZE);
	  _app->draw(text);

	  sf::Text shortcut;
	  shortcut.setString(_entries[i].shortcut);
	  shortcut.setFont(_font);
	  shortcut.setCharacterSize(UI_FONT_SIZE);
	  shortcut.setStyle(sf::Text::Underlined);
	  shortcut.setColor(sf::Color(255, 255, 0));
	  shortcut.setPosition(UI_PADDING + 0, UI_PADDING + i * UI_FONT_SIZE);
	  _app->draw(shortcut);
	}
	break;
  }
}
