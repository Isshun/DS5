#ifndef _C_GAME_
#define _C_GAME_

#include <SFML/Window.hpp>
#include <utime.h>

#include "Character.h"
#include "defines.h"
#include "WorldMap.h"
#include "WorldRenderer.h"
#include "SpriteManager.h"
#include "Cursor.h"
#include "UserInterface.h"
#include "Viewport.h"
#include "CharacterManager.h"

class	Game : public Serializable {
public:
	Game(sf::RenderWindow* app);
	~Game();

	void	loop();
	virtual void	create();
	virtual void	load(const char* filePath);
	virtual void	save(const char* filePath);
	void	update();
	void	refresh(double animProgress);
	void	draw_surface();
	void	checkQuit();

private:
	sf::RenderWindow*	_app;
	sf::Event			event;
	sf::Sprite*			_background;
	sf::Texture*		_backgroundTexture;
	sf::Time			_last_refresh;
	sf::Time			_last_update;

	WorldRenderer*		_worldRenderer;
	SpriteManager*		_spriteManager;
	UserInterface*		_ui;

	bool                _run;
	CharacterManager*	_characterManager;
	Viewport*           _viewport;
	unsigned int		_update;

	unsigned int		_seed;
	unsigned int		_frame;
	unsigned int		_lastInput;
	long				_renderTime;
};

#endif
