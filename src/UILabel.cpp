#include "UILabel.h"


bool	UILabel::isHover(int x, int y) {
  sf::Rect<float> local = _text.getLocalBounds();
  sf::Rect<float> global = _text.getGlobalBounds();
  sf::Rect<int> rect = sf::Rect<int>(global.left, global.top - 12, local.width, local.height + 4);

  // std::cout << "size: "
  // 			<< rect.width << " "
  // 			<< rect.height << std::endl;

  // std::cout << "pos: "
  // 			<< rect.left << " "
  // 			<< rect.top << std::endl;

  /* std::cout << "rect: " */
  /* 		  << rect.left << " " */
  /* 		  << rect.top << " " */
  /* 		  << rect.width << " " */
  /* 		  << rect.height << std::endl; */

  /* rect = getLocalBounds(); */
  /* std::cout << "rect: " */
  /* 		  << rect.left << " " */
  /* 		  << rect.top << " " */
  /* 		  << rect.width << " " */
  /* 		  << rect.height << std::endl; */

  /* std::cout << "pos: " */
  /* 		  << getPosition().x << " " */
  /* 		  << getPosition().y << std::endl; */

  return rect.contains(x, y);

}
