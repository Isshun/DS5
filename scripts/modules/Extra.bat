@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  modules/Extra startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

@rem Add default JVM options here. You can also use JAVA_OPTS and MODULES_EXTRA_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto init

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:init
@rem Get command-line arguments, handling Windowz variants

if not "%OS%" == "Windows_NT" goto win9xME_args
if "%@eval[2+2]" == "4" goto 4NT_args

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1" == "x" goto execute

set CMD_LINE_ARGS=%*
goto execute

:4NT_args
@rem Get arguments from the 4NT Shell from JP Software
set CMD_LINE_ARGS=%$

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\Extra-1.0.jar;%APP_HOME%\lib\core-1.0.jar;%APP_HOME%\lib\gdx-box2d-1.5.2.jar;%APP_HOME%\lib\box2dlights-1.3.jar;%APP_HOME%\lib\gdx-freetype-1.5.2.jar;%APP_HOME%\lib\gdx-ai-1.5.0.jar;%APP_HOME%\lib\gdx-smart-font-1.0.jar;%APP_HOME%\lib\PathFinding-1.0.jar;%APP_HOME%\lib\vtd-xml-2.11.jar;%APP_HOME%\lib\commons-math3-3.5.jar;%APP_HOME%\lib\luaj-jse-3.0.1.jar;%APP_HOME%\lib\xstream-1.4.8.jar;%APP_HOME%\lib\xmlpull-1.1.3.1.jar;%APP_HOME%\lib\xpp3-1.1.6.jar;%APP_HOME%\lib\snakeyaml-1.15.jar;%APP_HOME%\lib\reflections-0.9.10.jar;%APP_HOME%\lib\json-20150729.jar;%APP_HOME%\lib\xpp3_min-1.1.4c.jar;%APP_HOME%\lib\junit-4.7.jar;%APP_HOME%\lib\jakarta-regexp-1.4.jar;%APP_HOME%\lib\guava-18.0.jar;%APP_HOME%\lib\javassist-3.18.2-GA.jar;%APP_HOME%\lib\annotations-2.0.1.jar;%APP_HOME%\lib\gdx-1.5.3.jar

@rem Execute modules/Extra
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %MODULES_EXTRA_OPTS%  -classpath "%CLASSPATH%"  %CMD_LINE_ARGS%

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable MODULES_EXTRA_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%MODULES_EXTRA_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
