#include "defines.h"
#include "Settings.h"

const SettingsResolution resolutions[] = {
  {1, 1152, 768},
  {2, 1281, 854},
  {3, 1350, 900},
  {4, 1440, 960},
  {5, 1575, 1050},
  {6, 1800, 1200},
  {7, 2160, 1440},
  {8, 2304, 1536},
  {9, 2400, 1600},
  {10, 2561, 1707},
  {11, 2880, 1920}
};

Settings::Settings() {
  _debug = false;
  _resX = resolutions[0].width;
  _resY = resolutions[0].height;
}

Settings::~Settings() {
}

void	Settings::set(int entry, int value) {
  switch (entry) {
  case RESOLUTION:
	_resX = resolutions[value].width;
	_resY = resolutions[value].height;
  }
}
