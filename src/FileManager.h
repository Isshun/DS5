#ifndef _C_FILEMANAGER_
#define _C_FILEMANAGER_

#include <sstream>
#include <vector>

class	Serializable
{
  virtual void	load(const char* filePath) = 0;
  virtual void	save(const char* filePath) = 0;
};

class FileManager {

 public:

  static const char* SAVE_DIRECTORY;

  static void split(const std::string &s, char delim, std::vector<std::string> &elems) {
    std::stringstream ss(s);
    std::string item;
    while (std::getline(ss, item, delim)) {
	  elems.push_back(item);
    }
  }

};

#endif
