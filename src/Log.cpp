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

void Log::bind(ostream *o) const
{
  os = o;
  header();
}

void Log::header() const
{
  switch (_type)
    {
    case LOG_WARNING:
      *os << "[" << now << "] " << "\33[33mWARNING\33[0m ";
      break;
    case LOG_ERROR:
      *os << "[" << now << "] " << "\33[31mERROR\33[0m ";
      break;
    case LOG_INFO:
      *os << "[" << now << "] " << "\33[32mINFO\33[0m ";
      break;
    default:
      *os << "[" << now << "] " << "\33[34mDEBUG\33[0m ";
      break;
    }
}

void Log::footer() const
{
}

ostream& operator<<(ostream &os, const Log &l)
{
  l.bind(&os);
  return os;
}
