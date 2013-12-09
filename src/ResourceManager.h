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

 private:
  static ResourceManager _self;

  int _matter;
};

#endif
