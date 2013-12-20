#ifndef _CPP_WORLDRENDERER_
#define _CPP_WORLDRENDERER_

#include <SFML/Graphics.hpp>
#include "defines.h"
#include "SpriteManager.h"

class WorldRenderer {
  public:
	WorldRenderer(sf::RenderWindow* app, SpriteManager* spriteManager);
	~WorldRenderer() {}

	void	draw(sf::RenderStates render);
	void	drawStructure(sf::RenderStates render, int fromX, int fromY, int toX, int toY);
	void	drawItems(sf::RenderStates render, int fromX, int fromY, int toX, int toY);
	void	drawFloor(sf::RenderStates render, int fromX, int fromY, int toX, int toY);
	void	drawDebug(sf::RenderStates render, int fromX, int fromY, int toX, int toY);

  private:
	sf::RenderWindow*	_app;
	SpriteManager*		_spriteManager;
	sf::Font			_font;
};

#endif
