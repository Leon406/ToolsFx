@echo off
echo # %2  >> changelog.md
echo ## feature:  >> changelog.md
git log %1..%2 --pretty="- %%s" |findstr feat>> changelog.md
echo ## bug fix:  >>changelog.md
git log %1..%2 --pretty="- %%s" |findstr  fix>> changelog.md
echo generate complete