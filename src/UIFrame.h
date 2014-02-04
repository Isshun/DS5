#ifndef FRAME_H
#define FRAME_H

#include <SFML/System.hpp>
#include <SFML/Window.hpp>
#include <SFML/Graphics.hpp>
#include <condition_variable>
#include <future>
#include <functional>
#include <stdexcept>

#include "UIClickListener.h"

class UIFrame : public UIClickListener {

 public:

  virtual void	draw(sf::RenderWindow* app) = 0;
  virtual bool	isHover(int x, int y) = 0;

 protected:
  int	_posX;
  int	_posY;


 /* public: */
 /*  UIFrame() { _drawable = NULL; } */

 /*  sf::Drawable*	getDrawable() { return _drawable; } */

 /*  sf::Drawable*	setDrawable(sf::Drawable drawable) { _drawable = drawable; } */

 /* protected: */

 /*  sf::Drawable* _drawable; */
};

#endif
