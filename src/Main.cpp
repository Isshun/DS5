#include <SFML/Graphics.hpp>

#include "Game.h"
#include "defines.h"

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
		  filePath.append("1.sav");
		  game = new Game(&app);
		  game->load(filePath.c_str());
		  game->loop();
		  break;
		}

		case sf::Keyboard::S: {
		  if (game != NULL) {
			string filePath(FileManager::SAVE_DIRECTORY);
			filePath.append("1.sav");
			game->save(filePath.c_str());
		  }
		  break;
		}

		case sf::Keyboard::N: {
		  if (game != NULL) {
			delete game;
		  }
		  game = new Game(&app);
		  game->loop();
		  break;
		}

		case sf::Keyboard::Escape: {
		  if (game != NULL) {
			game->loop();
		  }
		}

		case sf::Keyboard::K: {
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
	}
  }

  return EXIT_SUCCESS;
}

