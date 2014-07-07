#include <stdio.h> 
#include <sstream>
#include <string>
#include <iostream>
#include "Render.hpp"

Render::Render(JNIBridge* bridge) {
  this->bridge = bridge;
  _map = std::map<int, sf::Sprite*>();
}

std::vector<std::string> &split(const std::string &s, char delim, std::vector<std::string> &elems) {
    std::stringstream ss(s);
    std::string item;
    while (std::getline(ss, item, delim)) {
        elems.push_back(item);
    }
    return elems;
}

void Render::addResource(int id, const char* name) {
  sf::Texture *texture = new sf::Texture();
  std::vector<std::string> vector;
  split(name, '.', vector);
  
  texture->loadFromFile("data/items/"+vector[1]+".png");
  _map[id] = new sf::Sprite();
  _map[id]->setTexture(*texture);
  _map[id]->setTextureRect(sf::IntRect(0, 0, 32, 32));
  std::cout << "addResource: " << "data/items/"+vector[1]+".png" << std::endl;
}

void Render::init()
{
    sf::RenderWindow window(sf::VideoMode(1500, 1000), "SFML works!");
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
  
    long** map = new long*[250];
    for (int i = 0; i < 250; i++) {
      map[i] = new long[250];
      for (int j = 0; j < 250; j++) {
        map[i][j] = 0;
      }
    }
  
    int count = 0;
  
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
        
        std::stringstream ss;
        ss << count;

        sf::RectangleShape rectangle;
        rectangle.setSize(sf::Vector2f(1, 1));
        for (int x = 0; x < 250; x++) {
          for (int y = 0; y < 250; y++) {
            int id = map[x][y];
            if (id != 0 && id < 500) {
              printf("id: %d\n", map[x][y]);
              _map[id]->setPosition(x*4, y*4);
              window.draw(*_map[id]);
            }
/*            rectangle.setPosition(x, y);
            rectangle.setFillColor(sf::Color(map[x][y] % 255, 0, 0));
            window.draw(rectangle);
            */
          }
            /*printf("\n");*/
        }
        
        sf::Text text;
        text.setFont(font);
        text.setString(ss.str());
        text.setCharacterSize(12);
        text.setColor(sf::Color::Red);
        text.setStyle(sf::Text::Bold | sf::Text::Underlined);
        window.draw(text);

        window.display();
        
        count = bridge->update(map);
    }
}

void Render::display(long* data) {

}
