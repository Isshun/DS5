#include "HomeScreen.h"

#define REFRESH_INTERVAL		20

void	HomeScreen::actionNew() {
  if (game != NULL) {
	delete game;
  }
  game = new Game(app);
  game->create();
  game->loop();
  // anim = 0;
}

void	HomeScreen::actionSave() {
  if (game != NULL) {
	string filePath(FileManager::SAVE_DIRECTORY);
	filePath.append("2.sav");
	game->save(filePath.c_str());
  } else {
	Error() << "Save failed: no game running";
  }
}

void	HomeScreen::actionLoad() {
  if (game != NULL) {
	delete game;
  }
  string filePath(FileManager::SAVE_DIRECTORY);
  filePath.append("2.sav");
  game = new Game(app);
  game->load(filePath.c_str());
  game->loop();
  // anim = 0;
}

void	HomeScreen::actionQuit() {
  app->close();
}

void	HomeScreen::actionResume() {
  if (game != NULL) {
	game->loop();
	// anim = 0;
  }
}

void	HomeScreen::run() {
  // Background
  Debug() << "Game background";

  game = NULL;

  Options* options = new Options(app);

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

  UIViewGroup group = UIViewGroup();

  int anim = 0;

  // Resume
  UILabel lbResume = UILabel("Resume");
  lbResume.setPosition(posX + 22, posY + 36 + 50 * 0);
  lbResume.setOnClickListener([this]() {
	  actionResume();
  	});
  group.addView(&lbResume);

  // New
  UILabel lbNew = UILabel("New");
  lbNew.setPosition(posX + 22, posY + 36 + 50 * 1);
  lbNew.setOnClickListener([this]() {
	  actionNew();
  	});
  group.addView(&lbNew);

  // Load
  UILabel lbLoad = UILabel("Load");
  lbLoad.setPosition(posX + 22, posY + 36 + 50 * 2);
  lbLoad.setOnClickListener([this]() {
	  actionLoad();
  	});
  group.addView(&lbLoad);

  // Save
  UILabel lbSave = UILabel("Save");
  lbSave.setPosition(posX + 22, posY + 36 + 50 * 3);
  lbSave.setOnClickListener([this]() {
	  actionSave();
  	});
  group.addView(&lbSave);

  // Quit
  UILabel lbQuit = UILabel("Exit");
  lbQuit.setPosition(posX + 22, posY + 36 + 50 * 4);
  lbQuit.setOnClickListener([this]() {
	  actionQuit();
  	});
  group.addView(&lbQuit);


  while (app->isOpen()) {
	sf::Event event;

	// Events
	while (app->pollEvent(event)) {

	  if (event.type == sf::Event::MouseButtonReleased) {
		group.click(event.mouseButton.button, event.mouseButton.x, event.mouseButton.y);
	  }

	  if (event.type == sf::Event::MouseMoved) {
		group.mouseMove(event.mouseMove.x, event.mouseMove.y);
	  }

	  if (event.type == sf::Event::KeyReleased) {
		if (options->isOpen) {
		  options->checkKey(event.key.code);
		}

		else {

		  switch (event.key.code) {

		  case sf::Keyboard::L: {
			actionLoad();
			break;
		  }

		  case sf::Keyboard::S: {
			actionSave();
			break;
		  }

		  case sf::Keyboard::N: {
			actionNew();
			break;
		  }

		  case sf::Keyboard::Escape: {
			actionResume();
			break;
		  }

		  case sf::Keyboard::O: {
			options->isOpen = true;
			break;
		  }

		  case sf::Keyboard::Q: {
			actionQuit();
			break;
		  }

		  }
		}
	  }

	}

	_time_elapsed = display_timer.getElapsedTime();
	if (_time_elapsed.asMilliseconds() > REFRESH_INTERVAL) {
	  display_timer.restart();
	  app->display();
	  app->draw(background);

	  group.draw(app);

	  if (options->isOpen) {
		options->draw(app);
	  } else {

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
			  app->draw(bgMenu);
			}
		  }
		}

		// // New
		// if (anim > 0) {
		//   app.draw(text);
		//   text.setString("N");
		//   text.setStyle(sf::Text::Underlined);
		//   text.setColor(sf::Color(255, 255, 0));
		//   text.setPosition(posX + 22, posY + 36 + 50 * 0);
		//   app.draw(text);
		// }

		// // Load
		// if (anim > 0) {
		//   text.setString("Load");
		//   text.setStyle(sf::Text::Regular);
		//   text.setColor(sf::Color(255, 255, 255));
		//   text.setPosition(posX + 22, posY + 36 + 50 * 1);
		//   text.setCharacterSize(38);
		//   app.draw(text);
		//   text.setString("L");
		//   text.setStyle(sf::Text::Underlined);
		//   text.setColor(sf::Color(255, 255, 0));
		//   text.setPosition(posX + 22, posY + 36 + 50 * 1);
		//   app.draw(text);
		// }

		// // Save
		// if (anim > 0) {
		//   text.setString("Save");
		//   text.setStyle(sf::Text::Regular);
		//   text.setColor(game != NULL ? sf::Color(255, 255, 255) : sf::Color(255, 255, 255, 50));
		//   text.setPosition(posX + 22, posY + 36 + 50 * 2);
		//   text.setCharacterSize(38);
		//   app.draw(text);
		//   text.setString("S");
		//   text.setStyle(sf::Text::Underlined);
		//   text.setColor(game != NULL ? sf::Color(255, 255, 0) : sf::Color(255, 255, 0, 50));
		//   text.setPosition(posX + 22, posY + 36 + 50 * 2);
		//   app.draw(text);
		// }

		// // Options
		// if (anim > 0) {
		//   text.setString("Options");
		//   text.setStyle(sf::Text::Regular);
		//   text.setColor(sf::Color(255, 255, 255));
		//   text.setPosition(posX + 22, posY + 36 + 50 * 3);
		//   text.setCharacterSize(38);
		//   app.draw(text);
		//   text.setString("O");
		//   text.setStyle(sf::Text::Underlined);
		//   text.setColor(sf::Color(255, 255, 0));
		//   text.setPosition(posX + 22, posY + 36 + 50 * 3);
		//   app.draw(text);
		// }

		// // Quit
		// if (anim > 0) {
		//   text.setString("Quit");
		//   text.setStyle(sf::Text::Regular);
		//   text.setColor(sf::Color(255, 255, 255));
		//   text.setPosition(posX + 22, posY + 36 + 50 * 4);
		//   text.setCharacterSize(38);
		//   app.draw(text);
		//   text.setString("Q");
		//   text.setStyle(sf::Text::Underlined);
		//   text.setColor(sf::Color(255, 255, 0));
		//   text.setPosition(posX + 22, posY + 36 + 50 * 4);
		//   app.draw(text);
		// }

		// Name
		text.setString(NAME " - " VERSION);
		text.setStyle(sf::Text::Regular);
		text.setColor(sf::Color(255, 255, 255));
		text.setPosition(WINDOW_WIDTH - 90 - (strlen(VERSION) * 11), WINDOW_HEIGHT - 24);
		text.setCharacterSize(16);
		app->draw(text);

		anim++;
	  }
	}
  }
}
