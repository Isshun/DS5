cd C:\MinGW\bin\
g++ ..\DS5\DS5\src\*.cpp --std=c++11 -I..\SFML-2.0\include -I..\DS5\DS5\src -L..\SFML-2.0\lib -lsfml-graphics -lsfml-window -lsfml-system -o ..\DS5\DS5\bin\ds5.exe