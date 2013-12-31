/*
 * UserInterfaceMenu.h
 *
 *  Created on: 4 déc. 2013
 *      Author: alex
 */

#ifndef USERINTERFACEMENUBASE_H_
#define USERINTERFACEMENUBASE_H_

#include <SFML/System.hpp>
#include <SFML/Window.hpp>
#include <SFML/Graphics.hpp>
#include "defines.h"
#include "UserInterfaceBase.h"

class UserInterfaceMenuOperation : public UserInterfaceBase {
 public:

  UserInterfaceMenuOperation(sf::RenderWindow* app, int tileIndex);
  ~UserInterfaceMenuOperation();
  void	draw(int frame);
  void	drawTile();
  void	drawPanel(int frame);
  bool	checkKey(sf::Keyboard::Key key);
  void	drawJobs();
  void	toogleJobs() { _isJobsOpen = !_isJobsOpen; }

 private:
  bool				_isJobsOpen;
};

#endif /* USERINTERFACERESOURCE_H_ */
