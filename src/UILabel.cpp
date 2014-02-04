#include "UILabel.h"


bool	UILabel::isHover(int x, int y) {
  sf::Rect<float> local = _text.getLocalBounds();
  sf::Rect<int> rect = sf::Rect<int>(_posX, _posY, local.width, local.height + 4);

  return rect.contains(x, y);

}
