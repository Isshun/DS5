#include <SFML/Graphics.hpp>

#include "Game.h"
#include "defines.h"
#include "Options.h"
#include "Settings.h"
#include "MainRenderer.h"
#include "HomeScreen.h"

int main(int argc, char *argv[]) {
  sf::RenderWindow app(sf::VideoMode(WINDOW_WIDTH, WINDOW_HEIGHT, 32), NAME);

  MainRenderer::getInstance()->setWindow(&app);

  // app.setSize(sf::Vector2u(Settings::getInstance()->getResX(), Settings::getInstance()->getResY()));


  // _app->clear(sf::Color(0, 0, 50));

  HomeScreen home = HomeScreen(&app);
  home.run();

  // sf::Text lbSave = sf::Text();
  // lbSave.setFont(font);
  // lbSave.setString("Save");
  // lbSave.setStyle(sf::Text::Regular);
  // lbSave.setColor(sf::Color(255, 255, 255));
  // lbSave.setPosition(posX + 22, posY + 36 + 50 * 1);
  // lbSave.setCharacterSize(38);
  // UIFrame frameSave = UIFrame(&lbSave);
  // group.addView(&frameSave);

  // sf::Text lbLoad = sf::Text();
  // lbLoad.setFont(font);
  // lbLoad.setString("Load");
  // lbLoad.setStyle(sf::Text::Regular);
  // lbLoad.setColor(sf::Color(255, 255, 255));
  // lbLoad.setPosition(posX + 22, posY + 36 + 50 * 2);
  // lbLoad.setCharacterSize(38);
  // UIFrame frameLoad = UIFrame(&lbLoad);
  // group.addView(&frameLoad);

  // sf::Text lbOptions = sf::Text();
  // lbOptions.setFont(font);
  // lbOptions.setString("Options");
  // lbOptions.setStyle(sf::Text::Regular);
  // lbOptions.setColor(sf::Color(255, 255, 255));
  // lbOptions.setPosition(posX + 22, posY + 36 + 50 * 3);
  // lbOptions.setCharacterSize(38);
  // UIFrame frameOptions = UIFrame(&lbOptions);
  // group.addView(&frameOptions);

  // sf::Text lbQuit = sf::Text();
  // lbQuit.setFont(font);
  // lbQuit.setString("Quit");
  // lbQuit.setStyle(sf::Text::Regular);
  // lbQuit.setColor(sf::Color(255, 255, 255));
  // lbQuit.setPosition(posX + 22, posY + 36 + 50 * 4);
  // lbQuit.setCharacterSize(38);
  // UIFrame frameQuit = UIFrame(&lbQuit);
  // group.addView(&frameQuit);


  return EXIT_SUCCESS;
}

