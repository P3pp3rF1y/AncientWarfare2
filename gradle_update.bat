pushd %~dp0
gradlew --refresh-dependencies
gradlew setupDecompWorkspace
gradlew eclipse
pause