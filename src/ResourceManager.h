#ifndef _C_RESOURCE_MANAGER_
#define _C_RESOURCE_MANAGER_

#include "BaseItem.h"
#include "defines.h"

class	ResourceManager {
 public:

  enum {NONE, NO_MATTER, BUILD_COMPLETE, BUILD_PROGRESS};

  ResourceManager();
  ~ResourceManager();
  static ResourceManager& getInstance();

  int build(BaseItem* item);

  int getMatter() { return _matter; }
  int getPower() { return _power; }
  int getO2() { return _o2Use == 0 ? 100 : _o2Supply >= _o2Use ? 100 : _o2Supply * 100.0f / _o2Use; }

 private:
  static ResourceManager _self;

  int	_o2Use;
  int	_o2Supply;
  int	_matter;
  int	_power;
};

#endif
