@REM ----------------------------------------------------------------------------
@REM Maven Wrapper startup script, version 3.3.2
@REM ----------------------------------------------------------------------------
@echo off
setlocal

set BASE_DIR=%~dp0
set WRAPPER_DIR=%BASE_DIR%\.mvn\wrapper
set PROPS_FILE=%WRAPPER_DIR%\maven-wrapper.properties
set JAR_FILE=%WRAPPER_DIR%\maven-wrapper.jar

if not exist "%PROPS_FILE%" (
  echo Missing %PROPS_FILE%
  exit /b 1
)

for /f "usebackq tokens=1,* delims==" %%A in ("%PROPS_FILE%") do (
  if "%%A"=="wrapperUrl" set WRAPPER_URL=%%B
  if "%%A"=="distributionUrl" set DIST_URL=%%B
)

if not exist "%JAR_FILE%" (
  echo Downloading Maven Wrapper jar...
  if exist "%SystemRoot%\System32\WindowsPowerShell\v1.0\powershell.exe" (
    powershell -NoProfile -ExecutionPolicy Bypass -Command "Invoke-WebRequest -UseBasicParsing -Uri '%WRAPPER_URL%' -OutFile '%JAR_FILE%'" || exit /b 1
  ) else (
    echo PowerShell not available to download %WRAPPER_URL%
    exit /b 1
  )
)

java -Dmaven.multiModuleProjectDirectory="%BASE_DIR%" ^
  -classpath "%JAR_FILE%" ^
  org.apache.maven.wrapper.MavenWrapperMain ^
  -Dmaven.wrapper.distributionUrl="%DIST_URL%" ^
  %*

endlocal


