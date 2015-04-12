pushd %~dp0
gradlew --refresh-dependencies setupDecompWorkspace
gradlew eclipse
pause