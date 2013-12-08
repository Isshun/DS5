#include <iostream>

#include "tosng.h"
#include "Character.hpp"
#include "Scene.hpp"
#include "stlastar.h"

extern Scene	*scene;
#include <SFML/Graphics.hpp>

extern sf::RenderWindow	*app;

#define Character_W	32
#define Character_H	64

// Frequence de modification du sprite par
// rapport a l'update de la position du Character
#define FRAME_JUMP 3
#define STEP_Character	6
#define FIX_PATH_SIZE 8


Character::Character(int x, int y)
{
  _posY = y;
  _posX = x;
  _astarsearch = 0;
}

// FIXME: taille du sprite en dur
Character::~Character()
{
}

void		Character::go(int toX, int toY) {
  cout << "FindPath: " << toX << " x " << toY << endl;

  if (_astarsearch != 0) {
	_astarsearch->FreeSolutionNodes();
	_astarsearch->EnsureMemoryFreed();
	delete _astarsearch;
	_astarsearch = 0;
  }

  _steps = 0;
  _astarsearch = new AStarSearch<MapSearchNode>();

  unsigned int SearchCount = 0;

  const unsigned int NumSearches = 1;

   while(SearchCount < NumSearches) {

	 // Create a start state
	 MapSearchNode nodeStart;
	 nodeStart.x = _posX;
	 nodeStart.y = _posY;

	 // Define the goal state
	 MapSearchNode nodeEnd;
	 nodeEnd.x = toX;
	 nodeEnd.y = toY;

	 // Set Start and goal states

	 _astarsearch->SetStartAndGoalStates( nodeStart, nodeEnd );

	 unsigned int SearchState;
	 unsigned int SearchSteps = 0;

	 do {
	   SearchState = _astarsearch->SearchStep();
	   SearchSteps++;
	 }
	 while( SearchState == AStarSearch<MapSearchNode>::SEARCH_STATE_SEARCHING );

	 if( SearchState == AStarSearch<MapSearchNode>::SEARCH_STATE_SUCCEEDED ) {
	   cout << "Search found goal state: " << SearchSteps << endl;
	 }
	 else if( SearchState == AStarSearch<MapSearchNode>::SEARCH_STATE_FAILED ) {
	   cout << "Search terminated. Did not find goal state\n";
	   // _astarsearch->FreeSolutionNodes();
	   // _astarsearch->EnsureMemoryFreed();
	   delete _astarsearch;
	   _astarsearch = 0;
	 }

	 SearchCount ++;
   }
}

void		Character::move()
{

  if (_astarsearch != 0) {

	// Next node
	MapSearchNode *node = 0;
	if (_steps == 0) {
	  node = _astarsearch->GetSolutionStart();
	} else {
	  node = _astarsearch->GetSolutionNext();
	}

	// Goto node
	if (node) {
	  node->PrintNodeInfo();
	  _posX = node->x;
	  _posY = node->y;
	  _steps++;
	}

	// clear path
	else {
	  // _astarsearch->FreeSolutionNodes();
	  // _astarsearch->EnsureMemoryFreed();
	  // delete _astarsearch;
	  // _astarsearch = 0;
	}
  }
}
