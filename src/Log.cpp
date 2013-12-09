/** Implementation Log
 *! @see Log.hpp
 */

#include "Log.hpp"

// Manipulateur - affiche date/heure courante
ostream &now(ostream &stream)
{
  char		buffer[42];
  time_t	t = time(NULL);
  struct tm	*localt = localtime(&t);

  strftime (buffer, 42, "%c", localt);
  return stream << buffer;
}

Log::Log(int type):os(0), _type(type){
}

Log::~Log(){
  footer();
}

void Log::bind(ostream *o) const {
  os = o;
  header();
}

void Log::header() const {
  switch (_type)
    {
    case LOG_WARNING:
      // *os << "[" << now << "] " << "\33[33mWARNING\33[0m ";
      *os << "\33[0;33mWARNING\33[0m ";
      break;
    case LOG_ERROR:
      *os << "\33[1;31mERROR\33[0m ";
      break;
    case LOG_INFO:
      *os << "\33[0;32mINFO\33[0m ";
      break;
    case LOG_VERBOSE:
      *os << "\33[0;34mVERBOSE\33[0m ";
      break;
    default:
      *os << "\33[0;35mDEBUG\33[0m ";
      break;
    }
}

void Log::footer() const {
}

ostream& operator<<(ostream &os, const Log &l) {
  // if (l._type < LOG_LEVEL) {
  // 	return os;
  // }
  l.bind(&os);
  return os;
}
