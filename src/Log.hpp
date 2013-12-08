/** Class Log: affichage formate des messages d'info/warning/error
 */

#ifndef __LOG_HPP__
#define __LOG_HPP__

#include <iostream>
#include <fstream>
#include <ostream>
#include <ctime>
#include <string>
#undef Log

using namespace std;

#define LOG_WARNING	1
#define LOG_ERROR	2
#define LOG_INFO	3

// Manipulateur - affiche date/heure courante
ostream &now(ostream &stream);

class Log
{
public:
  Log(int type = LOG_INFO);
  ~Log();

  inline void bind(ostream *o) const;
  void	header() const;
  void	footer() const;
  ostream	*get_stream() const {return _type ? os : oo;}

private:
  mutable ostream *os;
  mutable ostream *oo;
  int		_type;
};

ostream &operator<<(ostream &os, const Log &l);

#endif /* __LOG_HPP__ */
