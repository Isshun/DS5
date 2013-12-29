/*
 * MapSearchNode.h
 *
 *  Created on: 4 déc. 2013
 *      Author: alex
 */

#ifndef MAPSEARCHNODE_H_
#define MAPSEARCHNODE_H_

#include <iostream>
#include <stdio.h>
#include "stlastar.h" // See header for copyright and usage information

#define DEBUG_LISTS 0
#define DEBUG_LIST_LENGTHS_ONLY 0

using namespace std;

class MapSearchNode
{
 public:
  int x;
  int y;

  MapSearchNode() { x = y = 0; }
  MapSearchNode( unsigned int px, unsigned int py ) { x=px; y=py; }

  float GoalDistanceEstimate( MapSearchNode &nodeGoal );
  bool IsGoal( MapSearchNode &nodeGoal );
  bool GetSuccessors( AStarSearch<MapSearchNode> *astarsearch, MapSearchNode *parent_node );
  float GetCost( MapSearchNode &successor );
  bool IsSameState( MapSearchNode &rhs );

  void PrintNodeInfo();
};

typedef AStarSearch<MapSearchNode>		Path;

#endif /* MAPSEARCHNODE_H_ */
