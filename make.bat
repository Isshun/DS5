g++ -c JNIBridge.cpp Render.cpp TestJNI.cpp -I C:\SFML\include -I C:\java\include -I C:\java\include\win32
rem g++ TestJNI.o -o app -L C:\SFML\lib -lsfml-graphics -lsfml-window -lsfml-system
g++ -shared -o TestJNI.dll JNIBridge.o Render.o TestJNI.o TestJNI.def -L C:\SFML\lib -lsfml-graphics -lsfml-window -lsfml-system
pause