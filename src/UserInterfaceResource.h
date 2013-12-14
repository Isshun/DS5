/*
 * UserInterfaceMenu.h
 *
 *  Created on: 4 déc. 2013
 *      Author: alex
 */

#ifndef USERINTERFACERESOURCE_H_
#define USERINTERFACERESOURCE_H_

#include <SFML/System.hpp>
#include <SFML/Window.hpp>
#include <SFML/Graphics.hpp>
#include "defines.h"

class UserInterfaceResource {
 public:

  UserInterfaceResource(sf::RenderWindow* app);
  ~UserInterfaceResource();
  void refreshResources(int frame);

 private:
  sf::RenderWindow* _app;
};

#endif /* USERINTERFACERESOURCE_H_ */
