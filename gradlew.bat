@echo off
set DIRNAME=%~dp0
if "%DIRNAME%"=="" set DIRNAME=.
set APP_HOME=%DIRNAME%
set DEFAULT_JVM_OPTS="-Xmx64m" "-Xms64m"
if not defined JAVA_HOME goto findJavaExe
set JAVA_EXE=%JAVA_HOME%/bin/java.exe
if exist "%JAVA_EXE%" goto init
echo ERROR: JAVA_HOME set to invalid directory
goto fail
:findJavaExe
set JAVA_EXE=java.exe
"%JAVA_EXE%" -version >nul 2>&1
if not errorlevel 1 goto init
echo ERROR: JAVA_HOME not set and no 'java' found
goto fail
:init
set CLASSPATH=%APP_HOME%\gradle\wrapper\gradle-wrapper.jar
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %GRADLE_OPTS% "-Dorg.gradle.appname=%~n0" -cp "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*
:end
if %ERRORLEVEL% equ 0 goto mainEnd
:fail
exit /b 1
:mainEnd
