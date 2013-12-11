#include "Viewport.h"

Viewport::Viewport(sf::RenderWindow* app) {
  _posX = 0;
  _posY = 0;
  _width = WINDOW_WIDTH - UI_WIDTH;
  _height = WINDOW_HEIGHT - UI_HEIGHT;
  _scale = 1;
}

Viewport::~Viewport() {
}

void    Viewport::update(int x, int y) {
  _posX -= x;
  _posY -= y;
}

sf::Transform  Viewport::getViewTransform(sf::Transform transform) {
  transform.translate(UI_WIDTH + _posX, UI_HEIGHT + _posY);
  transform.scale(_scale, _scale);
  return transform;
}

