#include "Viewport.h"

Viewport::Viewport(sf::RenderWindow* app) {
  _posX = 0;
  _posY = 0;
  _width = WINDOW_WIDTH - UI_WIDTH;
  _height = WINDOW_HEIGHT - UI_HEIGHT;
  _scaleIndex = 0;
}

Viewport::~Viewport() {
}

void    Viewport::update(int x, int y) {
  _posX -= x;
  _posY -= y;
}

sf::Transform  Viewport::getViewTransform(sf::Transform transform) {
  float scale = getScale();
  transform.translate(UI_WIDTH + _posX, UI_HEIGHT + _posY);
  transform.scale(scale, scale);
  return transform;
}

sf::Transform  Viewport::getViewTransformBackground(sf::Transform transform) {
  float scale = getScale();
  transform.translate(_posX / 10 - 250, _posY / 10 - 50);
  transform.scale(1+(scale/20), 1+(scale/20));
  return transform;
}

float  Viewport::getScale() {
  switch (_scaleIndex) {
  case -4: return 0.5f;
  case -3: return 0.625f;
  case -2: return 0.75f;
  case -1: return 0.875f;
  default: return 1.0f;
  case 1: return 1.125f;
  case 2: return 1.25f;
  case 3: return 1.375f;
  case 4: return 1.5f;
  }
}
