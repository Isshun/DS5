#ifndef USERINTERRACTION_H_
#define USERINTERRACTION_H_

#include <SFML/System.hpp>
#include <SFML/Window.hpp>
#include <SFML/Graphics.hpp>
#include "Cursor.h"
#include "Viewport.h"

class UserInteraction {

 public:

  enum {
	MODE_NONE,
	MODE_BUILD,
	MODE_EREASE,
	MODE_SELECT
  };

  UserInteraction(Viewport* viewport);
  int getMode() { return _mode; }
  void setMode(int mode) { _mode = mode; }
  void selectBuildItem(int type) { _mode = MODE_BUILD; _itemType = type; }
  void cancel() { _mode = MODE_NONE; _itemType = -1; }
  int getBuildItem() { return _itemType; }

  void							refreshCursor();
  Cursor*						getCursor() { return _cursor; }
  void							mouseMove(int x, int y);
  void							mousePress(int button, int x, int y);
  bool							mouseRelease(int button, int x, int y);
  void							build(int startX, int startY, int toX, int toY);
  void							erease(int startX, int startY, int toX, int toY);

 private:
  void							drawCursor(int startX, int startY, int toX, int toY);

  sf::Texture					_cursorTexture;
  Viewport*						_viewport;
  Cursor*						_cursor;
  int							_mode;
  int							_itemType;
  int							_startPressX;
  int							_startPressY;
  int							_mouseMoveX;
  int							_mouseMoveY;
  int							_button;
};

#endif
