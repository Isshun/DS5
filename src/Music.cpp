#include <iostream>
#include "Music.hpp"

Music::Music(): music(NULL)
{
  Uint16 audio_format = AUDIO_S16; /* 16-bit stereo */
  int	audio_rate = 22050;
  int	audio_channels = 2;
  int	audio_buffers = 4096;


  if (Mix_OpenAudio(audio_rate, audio_format, audio_channels, audio_buffers))
    {
      printf("Unable to open audio!\n");
      exit(1);
    }

  Mix_QuerySpec(&audio_rate, &audio_format, &audio_channels);
}

Music::~Music()
{
    Mix_HaltMusic();
    Mix_FreeMusic(music);
    music = NULL;
    std::cout << "Music deleted (" << this->path << ")" << std::endl;
}

void	Music::load(const char *path)
{
  std::cout << "Music load " << "(" << path << ")" << std::endl;

  // Reload de la meme musique, pas d'action
  if (this->music && strcmp(path, this->path.c_str()) == 0)
    return;

  // Unload de la music precedente
  if (this->music)
    this->unload();

  // Load de la music
  this->path = path;
  music = Mix_LoadMUS(this->path.c_str());
  Mix_PlayMusic(music, -1); // pointer, loop number
  //Mix_HookMusicFinished( (*f)() );
}

void	Music::unload()
{
    Mix_HaltMusic();
    Mix_FreeMusic(music);
    music = NULL;

    std::cout << "Music unload (" << this->path << ")" << std::endl;
}
