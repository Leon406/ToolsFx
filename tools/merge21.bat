set LIB=%~dp0toMerged
cd %LIB%
for %%X in ("%LIB%"\*.jar) do (
    echo %%X
	%JAVA_HOME_21%/bin/jar -xvf %%X
)
del /F *.jar
%JAVA_HOME_21%/bin/jar -cvfM bigJar.jar .
rem  %JAVA_HOME_17%/bin/jar -cvfM bigJar.jar .
pause
