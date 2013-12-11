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
  void  setScale(float delta) { _scale = min(max(_scale + 0.1f * delta, 0.5f), 1.5f); }
  float getScale() { return _scale; }
  void  update(int x, int y);
  sf::Transform  getViewTransform(sf::Transform transform);
  sf::Transform  getViewTransformBackground(sf::Transform transform);

 private:
  int   _posX;
  int   _posY;
  int   _width;
  int   _height;
  float _scale;
};

#endif //_C_VIEWPORT_
