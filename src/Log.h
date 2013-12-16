#ifndef __LOG_H__
#define __LOG_H__

#include <fstream>
#include <iostream>
#include <ostream>
#include <ctime>
#include <string>
#undef Log

using namespace std;

#define LOG_LEVEL		LOG_ERROR

#define LOG_DEBUG	1
#define LOG_VERBOSE	2
#define LOG_INFO	3
#define LOG_WARNING	4
#define LOG_ERROR	5

class Log
{
  int	_count;
  int	_type;

   public:
      Log(int type) // the ofstream needs a path
      {
		_count = 0;
		_type = type;
      }

	  void header() const {
		switch (_type)
		  {
		  case LOG_WARNING:
			// *os << "[" << now << "] " << "\33[33mWARNING\33[0m ";
			std::cout << "\33[0;33mWARNING\33[0m ";
			break;
		  case LOG_ERROR:
			std::cout << "\33[1;31mERROR\33[0m ";
			break;
		  case LOG_INFO:
			std::cout << "\33[0;32mINFO\33[0m ";
			break;
		  case LOG_VERBOSE:
			std::cout << "\33[0;34mVERBOSE\33[0m ";
			break;
		  default:
			std::cout << "\33[0;35mDEBUG\33[0m ";
			break;
		  }
	  }

      template <typename T>
      Log& operator<<(T&& t) // provide a generic operator<<
      {
		if (_type >= LOG_LEVEL) {
		  if (_count++ == 0) {
			std::cout << std::endl;
			header();
		  }
		
		  std::cout << t;
		}

		return *this;
      }
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

#endif //__LOG_H__
