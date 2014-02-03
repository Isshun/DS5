#ifndef CLICK_LISTENER_H
#define CLICK_LISTENER_H

#include <iostream>
#include <string>
#include <stdio.h>
#include <condition_variable>
#include <future>
#include <functional>
#include <stdexcept>

class UILabel;

typedef std::function<void (void)> OnClickListener;
typedef std::function<void (UILabel*)> OnMouseEnterListener;
typedef std::function<void (UILabel*)> OnMouseExitListener;

class UIClickListener {
 public:

  UIClickListener() {
	_isFocused = false;
  }

  virtual void			onMouseEnter(OnMouseEnterListener listener) = 0;
  virtual void			onMouseExit(OnMouseExitListener listener) = 0;

  void					setOnClickListener(OnClickListener listener) {
	_clickListener = listener;
  }
  void					setOnMouseEnterListener(OnMouseEnterListener listener) {
	_mouseEnterListener = listener;
  }
  void					setOnMouseExitListener(OnMouseExitListener listener) {
	_mouseExitListener = listener;
  }

  void					click() {
	_clickListener();
  }

  void					mouseEnter() {
	if (_isFocused == false) {
	  _isFocused = true;
	  onMouseEnter(_mouseEnterListener);
	}
  }

  void					mouseExit() {
	if (_isFocused == true) {
	  _isFocused = false;
	  onMouseExit(_mouseExitListener);
	}
  }

  bool					isFocused() { return _isFocused; }

 private:
  OnClickListener		_clickListener;
  OnMouseEnterListener	_mouseEnterListener;
  OnMouseExitListener	_mouseExitListener;
  bool					_isFocused;
};

#endif
