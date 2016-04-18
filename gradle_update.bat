pushd %~dp0
call gradlew clean
call gradlew --refresh-dependencies setupDecompWorkspace
call gradlew eclipse
pause