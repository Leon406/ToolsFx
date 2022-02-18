set LIB=%~dp0toMerged
cd %LIB%
for %%X in ("%LIB%"\*.jar) do (
    echo %%X
	jar -xvf %%X
)
del /F *.jar
jar -cvfM bigJar.jar .
pause
