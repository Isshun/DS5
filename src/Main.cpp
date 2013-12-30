#include <SFML/Graphics.hpp>

#include "Game.h"
#include "defines.h"

#define REFRESH_INTERVAL		20

int main(int argc, char *argv[]) {
  sf::RenderWindow app(sf::VideoMode(WINDOW_WIDTH, WINDOW_HEIGHT, 32), NAME);

  // _app->clear(sf::Color(0, 0, 50));

  // Background
  Debug() << "Game background";

  Game* game = NULL;

  sf::Time _time_elapsed;
  sf::Clock display_timer;

  sf::Texture texture;
  sf::Sprite background;
  texture.loadFromFile("../res/background.png");
  background.setTexture(texture);
  background.setTextureRect(sf::IntRect(0, 0, 1920, 1080));

  int posX = WINDOW_WIDTH / 2 - 380 / 2;
  int posY = WINDOW_HEIGHT / 2 - 420 / 2;

  sf::Texture textureMenu;
  sf::Sprite bgMenu;
  textureMenu.loadFromFile("../res/menu1.png");
  bgMenu.setTexture(textureMenu);

  sf::Font font;
  sf::Text text;
  font.loadFromFile("../snap/xolonium/Xolonium-Regular.otf");
  text.setFont(font);
  text.setCharacterSize(38);

  int anim = 0;

  while (app.isOpen()) {
	sf::Event event;

	// Events
	while (app.pollEvent(event)) {

	  if (event.type == sf::Event::KeyReleased) {
		switch (event.key.code) {

		case sf::Keyboard::L: {
		  if (game != NULL) {
			delete game;
		  }
		  string filePath(FileManager::SAVE_DIRECTORY);
		  filePath.append("2.sav");
		  game = new Game(&app);
		  game->load(filePath.c_str());
		  game->loop();
		  anim = 0;
		  break;
		}

		case sf::Keyboard::S: {
		  if (game != NULL) {
			string filePath(FileManager::SAVE_DIRECTORY);
			filePath.append("2.sav");
			game->save(filePath.c_str());
		  } else {
			Error() << "Save failed: no game running";
		  }
		  break;
		}

		case sf::Keyboard::N: {
		  if (game != NULL) {
			delete game;
		  }
		  game = new Game(&app);
		  game->create();
		  game->loop();
		  anim = 0;
		  break;
		}

		case sf::Keyboard::Escape: {
		  if (game != NULL) {
			game->loop();
			anim = 0;
		  }
		  break;
		}

		case sf::Keyboard::Q: {
		  app.close();
		  break;
		}

		}
	  }

	}

	_time_elapsed = display_timer.getElapsedTime();
	if (_time_elapsed.asMilliseconds() > REFRESH_INTERVAL) {
	  display_timer.restart();
	  app.display();
	  app.draw(background);

	  // // Anim center
	  // int width = min(380/2, 16 * anim);
	  // int height = min(420/2, 16 * anim);
	  // bgMenu.setPosition(posX + 380/2 - width, posY + 420/2 - height);
	  // bgMenu.setTextureRect(sf::IntRect(380/2-width, 420/2-height, width*2, height*2));

	  // Anim tile
	  int div = 1;
	  int width = min(380/2, 16 * anim);
	  int height = min(420/2, 16 * anim);
	  for (int j= 0; j < 14; j++) {
		for (int i = 0; i < 12; i++) {
		  if (anim / div > i + j) {
			bgMenu.setPosition(posX + i * 32, posY + j * 32);
			int fromX = i * 32;
			int fromY = j * 32;
			int toX = i == 11 ? 28 : 32;
			int toY = j == 13 ? 4 : 32;
			bgMenu.setTextureRect(sf::IntRect(fromX, fromY, toX, toY));
			app.draw(bgMenu);
		  }
		}
	  }

	  // New
	  text.setString("New");
	  text.setColor(sf::Color(255, 255, 255));
	  text.setPosition(posX + 22, posY + 36 + 50 * 0);
	  app.draw(text);
	  text.setString("N");
	  text.setColor(sf::Color(255, 255, 0));
	  text.setPosition(posX + 22, posY + 36 + 50 * 0);
	  app.draw(text);

	  // Load
	  text.setString("Load");
	  text.setColor(sf::Color(255, 255, 255));
	  text.setPosition(posX + 22, posY + 36 + 50 * 1);
	  app.draw(text);
	  text.setString("L");
	  text.setColor(sf::Color(255, 255, 0));
	  text.setPosition(posX + 22, posY + 36 + 50 * 1);
	  app.draw(text);

	  // Save
	  text.setString("Save");
	  text.setColor(game != NULL ? sf::Color(255, 255, 255) : sf::Color(255, 255, 255, 50));
	  text.setPosition(posX + 22, posY + 36 + 50 * 2);
	  app.draw(text);
	  text.setString("S");
	  text.setColor(game != NULL ? sf::Color(255, 255, 0) : sf::Color(255, 255, 0, 50));
	  text.setPosition(posX + 22, posY + 36 + 50 * 2);
	  app.draw(text);

	  // Quit
	  text.setString("Quit");
	  text.setColor(sf::Color(255, 255, 255));
	  text.setPosition(posX + 22, posY + 36 + 50 * 3);
	  app.draw(text);
	  text.setString("Q");
	  text.setColor(sf::Color(255, 255, 0));
	  text.setPosition(posX + 22, posY + 36 + 50 * 3);
	  app.draw(text);

	  anim++;
	}
  }

  return EXIT_SUCCESS;
}

