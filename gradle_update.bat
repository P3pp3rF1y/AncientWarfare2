pushd %~dp0
call gradlew setupDecompWorkspace
call gradlew eclipse
pause