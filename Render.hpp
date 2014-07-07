#ifndef RENDER_HPP
#define RENDER_HPP

#include <SFML/Graphics.hpp>
#include "JNIBridge.hpp"

class Render
{
private:
  JNIBridge* bridge;

public:
    Render(JNIBridge* bridge);
    void display(long* data);
    void init();
};

#endif
