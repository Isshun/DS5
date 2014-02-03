#include "UIViewGroup.h"

UIViewGroup::UIViewGroup() {
  _views = new std::list<UIFrame*>();
}

UIViewGroup::~UIViewGroup() {
  delete _views;
}

void	UIViewGroup::addView(UIFrame* view) {
  _views->push_back(view);
}

void	UIViewGroup::click(sf::Mouse::Button button, int x, int y) {
  std::list<UIFrame*>::iterator it;

  for (it = _views->begin(); it != _views->end(); ++it) {
	if ((*it)->isHover(x, y)) {
	  (*it)->click();
	}
  }
}

void	UIViewGroup::mouseMove(int x, int y) {
  std::list<UIFrame*>::iterator it;

  for (it = _views->begin(); it != _views->end(); ++it) {
	if ((*it)->isHover(x, y)) {
	  (*it)->mouseEnter();
	} else {
	  (*it)->mouseExit();
	}
  }
}

void	UIViewGroup::draw(sf::RenderWindow* app) {
  std::list<UIFrame*>::iterator it;

  for (it = _views->begin(); it != _views->end(); ++it) {
	(*it)->draw(app);
  }
}
