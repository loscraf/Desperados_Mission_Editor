# Para linux o mac, no lo puedo probar, tengo windows, pero debería funcionar, es el mismo código que el gradlew.bat pero para sistemas unix
#!/usr/bin/env sh
DIR="$( cd "$( dirname "$0" )" && pwd )"
java -classpath "$DIR/gradle/wrapper/gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain "$@"