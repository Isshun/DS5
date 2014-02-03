#ifndef UI_LABEL_H
#define UI_LABEL_H

#include <SFML/System.hpp>
#include <SFML/Window.hpp>
#include <SFML/Graphics.hpp>
#include <condition_variable>
#include <future>
#include <functional>
#include <stdexcept>

#include "SpriteManager.h"
#include "UIFrame.h"

class UILabel : public UIFrame {

 public:

  UILabel(const char* str) {
	_text.setFont(SpriteManager::getInstance()->getFont());
	_text.setString(str);
	_text.setStyle(sf::Text::Regular);
	_text.setColor(sf::Color(255, 255, 255));
	_text.setCharacterSize(38);

	setOnMouseEnterListener([](UILabel* lbNew) {
		lbNew->setColor(sf::Color(255, 255, 0));
	  });
	setOnMouseExitListener([](UILabel* lbNew) {
		lbNew->setColor(sf::Color(255, 255, 255));
	  });
  }

  void	draw(sf::RenderWindow* app) {
	app->draw(_text);
  }

  sf::Text	getText() const {
	return _text;
  }

  void		onMouseEnter(OnMouseEnterListener listener) {
	listener(this);
  }

  void		onMouseExit(OnMouseExitListener listener) {
	listener(this);
  }

  void	setPosition(int x, int y) {
	_text.setPosition(x, y);
  }

  void	setColor(const sf::Color color) {
	_text.setColor(color);
  }

  bool	isHover(int x, int y);

  void	onClick();
  void	onMouseEnter();
  void	onMouseExit();

 private:

  sf::Text		_text;

};

#endif
