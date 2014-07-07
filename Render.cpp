#include <stdio.h> 
#include "Render.hpp"

Render::Render(JNIBridge* bridge) {
  this->bridge = bridge;
}

void Render::init()
{
    sf::RenderWindow window(sf::VideoMode(200, 200), "SFML works!");
    sf::CircleShape shape(100.f);
    shape.setFillColor(sf::Color::Green);

    sf::Font font;
    if (!font.loadFromFile("arial.ttf"))
    {
        // erreur...
    }
  /*
    sf::Text text;
    text.setFont(font); // font est un sf::Font
    text.setString(s_text);
    text.setCharacterSize(24); // exprimée en pixels, pas en points !
    text.setColor(sf::Color::Red);
    text.setStyle(sf::Text::Bold | sf::Text::Underlined);
  */
  
    while (window.isOpen())
    {
        sf::Event event;
        while (window.pollEvent(event))
        {
            if (event.type == sf::Event::Closed)
                window.close();
        }

        window.clear();
        window.draw(shape);
        
        sf::Text text;
        text.setFont(font); // font est un sf::Font
        text.setString("gg");
        text.setCharacterSize(24); // exprimée en pixels, pas en points !
        text.setColor(sf::Color::Red);
        text.setStyle(sf::Text::Bold | sf::Text::Underlined);

        window.draw(text);
        window.display();
        
        bridge->update();
    }
}

void Render::display(long* data) {

}
