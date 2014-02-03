#ifndef UIVIEWGROUP_H
#define UIVIEWGROUP_H

#include <list>
#include <SFML/System.hpp>
#include <SFML/Window.hpp>
#include <SFML/Graphics.hpp>

#include "UIFrame.h"

class UIViewGroup {

 public:

  UIViewGroup();
  ~UIViewGroup();

  void	addView(UIFrame* view);
  void	click(sf::Mouse::Button button, int x, int y);
  void	mouseMove(int x, int y);
  void	draw(sf::RenderWindow* app);

 private:

  std::list<UIFrame*>*	_views;

};

#endif
