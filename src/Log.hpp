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

#define LOG_LEVEL		LOG_INFO

#define LOG_DEBUG	1
#define LOG_VERBOSE	2
#define LOG_INFO	3
#define LOG_WARNING	4
#define LOG_ERROR	5

// Manipulateur - affiche date/heure courante
ostream &now(ostream &stream);

class Log
{
public:
  Log(int type = LOG_DEBUG);
  ~Log();

  inline void bind(ostream *o) const;
  void	header() const;
  void	footer() const;
  ostream	*get_stream() const {return _type ? os : oo;}
  int		_type;

private:
  mutable ostream *os;
  mutable ostream *oo;
};

class Error : public Log {
public:
  Error():Log(LOG_ERROR) {}
};

class Info : public Log {
public:
  Info():Log(LOG_INFO) {}
};

class Warning : public Log {
public:
  Warning():Log(LOG_WARNING) {}
};

class Debug : public Log {
public:
  Debug():Log(LOG_DEBUG) {}
};

class Verbose : public Log {
public:
  Verbose():Log(LOG_VERBOSE) {}
};

ostream &operator<<(ostream &os, const Log &l);

#endif /* __LOG_HPP__ */
