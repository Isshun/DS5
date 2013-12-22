#include "defines.h"
#include "MapSearchNode.h"
#include "WorldMap.h"

// map helper functions
int GetMap( int x, int y ) {
  // std::cout << "GetMap: " << x << " x " << y << std::endl;

  if( x < 0 || x >= WorldMap::getInstance()->getWidth() ||
	  y < 0 || y >= WorldMap::getInstance()->getHeight()) {
	return 9;
  }

  WorldMap::getInstance()->debugAstar(x, y);
  BaseItem* item = WorldMap::getInstance()->getItem(x, y);

  // Space
  if (item == NULL || item->isType(BaseItem::NONE)) {
	  return 2;
  }

  // WALL
  if (item != NULL && item->isSolid && item->isComplete()) {
	return 9;
  }

  // DEFAULT
  return 1;
}

bool MapSearchNode::IsSameState( MapSearchNode &rhs ) {
  // same state in a maze search is simply when (x,y) are the same
  if(x == rhs.x && y == rhs.y) {
	return true;
  } else {
	return false;
  }
}

void MapSearchNode::PrintNodeInfo() {
  // char str[100];
  // sprintf( str, "Node position : (%d,%d)\n", x,y );
  // cout << str;
}

// Here's the heuristic function that estimates the distance from a Node
// to the Goal.

float MapSearchNode::GoalDistanceEstimate( MapSearchNode &nodeGoal ) {
  float xd = float( ( (float)x - (float)nodeGoal.x ) );
  float yd = float( ( (float)y - (float)nodeGoal.y) );

  return xd + yd;
}

bool MapSearchNode::IsGoal( MapSearchNode &nodeGoal ) {
  if( (x == nodeGoal.x) && (y == nodeGoal.y) ) {
	return true;
  }

  return false;
}

// This generates the successors to the given Node. It uses a helper function called
// AddSuccessor to give the successors to the AStar class. The A* specific initialisation
// is done for each node internally, so here you just set the state information that
// is specific to the application
bool MapSearchNode::GetSuccessors( AStarSearch<MapSearchNode> *astarsearch, MapSearchNode *parent_node ) {
  int parent_x = -1;
  int parent_y = -1;

  if( parent_node ) {
	parent_x = parent_node->x;
	parent_y = parent_node->y;
  }

  MapSearchNode NewNode;

  // push each possible move except allowing the search to go backwards

  // left
  if ( (GetMap( x-1, y ) < 9) && !((parent_x == x-1) && (parent_y == y)) ) {
	NewNode = MapSearchNode( x-1, y );
	astarsearch->AddSuccessor( NewNode );
  }

  // bottom
  if ( (GetMap( x, y-1 ) < 9) && !((parent_x == x) && (parent_y == y-1)) ) {
	NewNode = MapSearchNode( x, y-1 );
	astarsearch->AddSuccessor( NewNode );
  }

  // right
  if ( (GetMap( x+1, y ) < 9) && !((parent_x == x+1) && (parent_y == y)) ) {
	NewNode = MapSearchNode( x+1, y );
	astarsearch->AddSuccessor( NewNode );
  }

  // top
  if ( (GetMap( x, y+1 ) < 9) && !((parent_x == x) && (parent_y == y+1)) ) {
	NewNode = MapSearchNode( x, y+1 );
	astarsearch->AddSuccessor( NewNode );
  }

  // top left
  if ( (GetMap( x-1, y-1 ) < 9) && !((parent_x == x-1) && (parent_y == y-1)) ) {
	NewNode = MapSearchNode( x-1, y-1 );
	astarsearch->AddSuccessor( NewNode );
  }

  // top right
  if ( (GetMap( x+1, y-1 ) < 9) && !((parent_x == x+1) && (parent_y == y-1)) ) {
	NewNode = MapSearchNode( x+1, y-1 );
	astarsearch->AddSuccessor( NewNode );
  }

  // bottom left
  if ( (GetMap( x-1, y+1 ) < 9) && !((parent_x == x-1) && (parent_y == y+1)) ) {
	NewNode = MapSearchNode( x-1, y+1 );
	astarsearch->AddSuccessor( NewNode );
  }

  // bottom right
  if ( (GetMap( x+1, y+1 ) < 9) && !((parent_x == x+1) && (parent_y == y+1)) ) {
	NewNode = MapSearchNode( x+1, y+1 );
	astarsearch->AddSuccessor( NewNode );
  }

  return true;
}

// given this node, what does it cost to move to successor. In the case
// of our map the answer is the map terrain value at this node since that is
// conceptually where we're moving
float MapSearchNode::GetCost( MapSearchNode &successor ) {
  float cost = 1.0f;

  // Diagonal
  if (successor.x != x && successor.y != y)
	cost += 0.4f;

  // Space
  if (GetMap(x, y) == 2)
	cost += 0.8f;

  return cost;
}
