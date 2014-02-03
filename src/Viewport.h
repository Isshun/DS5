#ifndef _C_VIEWPORT_
#define _C_VIEWPORT_

#include <SFML/System.hpp>
#include <SFML/Window.hpp>
#include <SFML/Graphics.hpp>
#include "defines.h"

class	Viewport {
 public:
  Viewport(sf::RenderWindow* app);
  ~Viewport();

  int   getPosX() { return _posX; }
  int   getPosY() { return _posY; }
  int   getWidth() { return _posX; }
  int   getHeight() { return _height; }
  void  setScale(int delta) { _scaleIndex = min(max(_scaleIndex + delta, -4), 4); }
  float getScale();
  void  update(int x, int y);
  void  startMove(int x, int y);

  sf::Transform  getViewTransform(sf::Transform transform);
  sf::Transform  getViewTransformBackground(sf::Transform transform);

 private:
  int   _posX;
  int   _posY;
  int   _lastPosX;
  int   _lastPosY;
  int   _width;
  int   _height;
  int	_scaleIndex;
};

#endif //_C_VIEWPORT_
