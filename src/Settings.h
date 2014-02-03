#ifndef _C_SETTINGS_
#define _C_SETTINGS_

#include "defines.h"

struct {
  int id;
  int width;
  int height;
} typedef SettingsResolution;

class	Settings
{
 public:
  Settings();
  ~Settings();

  enum {
	RESOLUTION,
	FULLSCREEN,
	RATIO
  };
  
  void				setDebug(bool debug) { _debug = debug; }
  void				set(int entry, int value);
  
  bool				isDebug() { return _debug; }
  
  static Settings*	getInstance() { return _self; }
  int				getResX() { return _resX; }
  int				getResY() { return _resY; }

 private:
  bool				_debug;
  static Settings*	_self;
  int				_resX;
  int				_resY;
};

#endif
