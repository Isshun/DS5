
#ifndef OPTIONS_H_
#define OPTIONS_H_

#include <SFML/System.hpp>
#include <SFML/Window.hpp>
#include <SFML/Graphics.hpp>
#include "defines.h"
#include "SpriteManager.h"

#define NB_LINES 5;

struct {
  int							value;
  const char*					label;
} typedef						OptionsEntryValue;

struct {
  int							id;
  const char*					label;
  OptionsEntryValue*			values;
} typedef						OptionsEntry;

class Options {

public:

  Options(sf::RenderWindow *app);
  void	draw(sf::RenderWindow* app);
  void	checkKey(sf::Keyboard::Key code);

  bool	isOpen;

 private:
  sf::RenderWindow* _app;
  int	_line;
  int	_buf[32];
  int	_nbEntries;
};

#endif
