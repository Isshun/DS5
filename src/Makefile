SRC	=	main.cpp	\
		Game.cpp	\
		Log.cpp		\
		MapSearchNode.cpp	\
		UserInterface.cpp	\
		BaseItem.cpp	\
		SpriteManager.cpp	\
		ResourceManager.cpp	\
		WorldMap.cpp	\
		Cursor.cpp	\
		Character.cpp

NAME	=	top_scene

CC	=	g++

OBJ	=	$(SRC:.cpp=.o)

INC	=	-I/usr/include	\
		-I. -I./class

CFLAGS	=	-W $(INC) -g3

LIB	=	-L/usr/lib -lsfml-graphics -lsfml-window -lsfml-system

$(NAME)	:	$(OBJ)
		$(CC) $(OBJ) -o $(NAME) $(LIB) $(CFLAGS)

.cpp.o:
		$(CC) -c $(CFLAGS) $< -o $@

all	:	$(NAME)

clean	:
		rm -f *~
		rm -f \#*\#
		rm -f *.o

fclean	:	clean
		rm -f $(NAME)

re	:	fclean all

install	:	all clean
