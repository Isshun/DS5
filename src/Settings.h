#ifndef _C_SETTINGS_
#define _C_SETTINGS_

#include "defines.h"

class	Settings
{
 public:
  Settings();
  ~Settings();
  
  void				setDebug(bool debug) { _debug = debug; }
  
  bool				isDebug() { return _debug; }
  
  static Settings*	getInstance() { return _self; }

 private:
  bool				_debug;
  static Settings*	_self;
};

#endif
