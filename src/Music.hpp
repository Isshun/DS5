#ifndef _MUSIC_H_
#define _MUSIC_H_

#include <SDL/SDL_mixer.h>

class Music
{
public:
		Music();
		~Music();
  void		load(const char *path);
  void		unload();
  void		loop();
  bool		is_playing(){return this->music;}

private:
  bool		playing;
  Mix_Music	*music;
  std::string	path;
};

#endif
