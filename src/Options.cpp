#include <stdio.h>
#include <string.h>

#include "Options.h"
#include "Settings.h"

#define MENU_WIDTH		400

OptionsEntryValue resolution169Values[] = {
  {1, "1 152 x 768"},
  {2, "1 281 x 854"},
  {3, "1 350 x 900"},
  {4, "1 440 x 960"},
  {5, "1 575 x 1 050"},
  {6, "1 800 x 1 200"},
  {7, "2 160 x 1 440"},
  {8, "2 304 x 1 536"},
  {9, "2 400 x 1 600"},
  {10, "2 561 x 1 707"},
  {11, "2 880 x 1 920"}
};

OptionsEntryValue resolution43Values[] = {
  {1, "800 x 600"},
  {2, "960 x 720"},
  {3, "1 024 x 768"},
  {4, "1 200 x 900"},
  {5, "1 280 x 960"},
  {6, "1 400 x 1 050"},
  {7, "1 600 x 1 200"},
  {8, "1 920 x 1 440"},
  {9, "2 048 x 1 536"},
  {10, "2 276 x 1 707"},
  {11, "2 560 x 1 920"}
};

OptionsEntryValue boolValues[] = {
  {0, "no"},
  {1, "yes"}
};

OptionsEntryValue ratioValues[] = {
  {0, "4/3"},
  {1, "16/9"}
};

OptionsEntry entries[] = {
  {Settings::RESOLUTION, "Resolution", resolution169Values},
  {Settings::FULLSCREEN, "Fullscreen", boolValues},
  {Settings::RATIO, "Ratio", ratioValues}
};

Options::Options(sf::RenderWindow *app) {
  _app = app;
  isOpen = false;
  _line = 0;
  _nbEntries = sizeof(entries) / sizeof(entries[0]);
  memset(_buf, 0, 32 * sizeof(_buf[0]));
}

void	Options::draw(sf::RenderWindow* app) {
  int posX = WINDOW_WIDTH / 2 - MENU_WIDTH / 2;
  int posY = 100;

  sf::Text text;
  text.setCharacterSize(16);
  text.setFont(SpriteManager::getInstance()->getFont());
  text.setStyle(sf::Text::Regular);

  for (int i = 0; i < _nbEntries; i++) {
	text.setString(entries[i].label);
	text.setPosition(posX, posY + 20 * i);
	text.setColor(_line == i ? sf::Color(255, 255, 0) : sf::Color(255, 255, 255));
	app->draw(text);

	const char* valueLabel = entries[i].values[_buf[i]].label;
	if (i == Settings::RESOLUTION) {
	  valueLabel = _buf[Settings::RATIO] == 1 ? resolution169Values[_buf[i]].label : resolution43Values[_buf[i]].label;
	}

	text.setString(valueLabel);
	text.setPosition(posX + 200, posY + 20 * i);
	app->draw(text);
  }

  text.setString("[Backspace] Cancel");
  text.setPosition(posX - 200, posY + 400);
  text.setColor(sf::Color(255, 255, 255));
  text.setCharacterSize(32);
  app->draw(text);

  text.setString("[Backspace]");
  text.setPosition(posX - 200, posY + 400);
  text.setColor(sf::Color(255, 255, 0));
  app->draw(text);

  text.setString("[Enter] Apply");
  text.setPosition(posX + 400, posY + 400);
  text.setColor(sf::Color(255, 255, 255));
  app->draw(text);

  text.setString("[Enter]");
  text.setPosition(posX + 400, posY + 400);
  text.setColor(sf::Color(255, 255, 0));
  app->draw(text);

}

  void	Options::checkKey(sf::Keyboard::Key code) {
	switch (code) {
	case sf::Keyboard::Left:
	  _buf[_line] = max(_buf[_line]-1, 0);
	  break;
	case sf::Keyboard::Right: {
	  int max = 1;
	  switch (_line) {
	  case Settings::RESOLUTION:
		if (_buf[Settings::RATIO] == 1) {
		  max = (int)(sizeof(resolution169Values) / sizeof(resolution169Values[0])) - 1;
		} else {
		  max = (int)(sizeof(resolution43Values) / sizeof(resolution43Values[0])) - 1;
		}
		break;
	  }
	  _buf[_line] = min(_buf[_line]+1, max);
	  break;
	}
	case sf::Keyboard::Up:
	  _line = (_line - 1) % _nbEntries;
	  break;
	case sf::Keyboard::Down:
	  _line = (_line + 1) % _nbEntries;
	  break;
	case sf::Keyboard::BackSpace:
	  isOpen = false;
	  break;
	case sf::Keyboard::Return:
	  for (int i = 0; i < _nbEntries; i++) {
		Settings::getInstance()->set(i, _buf[i]);
	  }

	  _app->setSize(sf::Vector2u(Settings::getInstance()->getResX(), Settings::getInstance()->getResY()));

	  isOpen = false;
	  break;
	}

  }
