#ifndef RENDER_HPP
#define RENDER_HPP

#include <SFML/Graphics.hpp>
#include "JNIBridge.hpp"

class Render
{
private:
  JNIBridge* bridge;
  std::map<int, sf::Sprite*>  _map;

public:
    Render(JNIBridge* bridge);
    void display(long* data);
    void init();
    void addResource(int id, const char* name);
};

#endif
