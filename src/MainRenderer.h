#ifndef MAINRENDERER_H_
#define MAINRENDERER_H_

#include <SFML/System.hpp>
#include <SFML/Window.hpp>
#include <SFML/Graphics.hpp>

class MainRenderer {
 public:
  static MainRenderer*		getInstance() { return _self; }
  void setWindow(sf::RenderWindow* app) { _app = app; }
  sf::RenderWindow* getWindow() { return _app; }
  void draw(sf::Sprite sprite, sf::RenderStates render) { _app->draw(sprite, render); }

 private:
  static MainRenderer* 		_self;
  sf::RenderWindow*			_app;
};

#endif /* MAINRENDERER_H_ */
