SRC	=	Main.cpp				\
		HomeScreen.cpp			\
		Options.cpp				\
		Game.cpp				\
		FileManager.cpp			\
		MainRenderer.cpp		\
		MapSearchNode.cpp		\
		UIViewGroup.cpp			\
		UILabel.cpp				\
		UserInteraction.cpp		\
		UserInterface.cpp		\
		UserInterfaceBase.cpp	\
		UserInterfaceMenu.cpp	\
		UserInterfaceCrew.cpp	\
		UserInterfaceDebug.cpp	\
		UserInterfaceScience.cpp	\
		UserInterfaceSecurity.cpp	\
		UserInterfaceMenuCharacter.cpp	\
		UserInterfaceMenuInfo.cpp	\
		UserInterfaceEngineering.cpp	\
		UserInterfaceMenuOperation.cpp	\
		BaseItem.cpp	\
		SpriteManager.cpp	\
		JobManager.cpp	\
		Job.cpp	\
		ResourceManager.cpp	\
		Viewport.cpp	\
		WorldMap.cpp	\
		WorldRenderer.cpp	\
		Room.cpp	\
		Settings.cpp	\
		Cursor.cpp	\
		PathManager.cpp	\
		CharacterManager.cpp	\
		Character.cpp

NAME	=	ds5

CC	=	g++

OBJ	=	$(SRC:.cpp=.o)

INC	=	-I/usr/include	\
		-I. -I./class

CFLAGS	=	-W $(INC) -g3 -std=c++11

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
