set LIB=%~dp0lib
cd %LIB%
for %%X in ("%LIB%"\*.jar) do (
    echo %%X
	jdeps %%X
)
pause