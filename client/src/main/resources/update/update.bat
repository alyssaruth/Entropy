@echo off

REM %1 = noOfBytes
REM %2 = version number
REM %3 = filename
REM %4 = assetId

echo Performing download of %1 bytes (Version %2)

curl -LJO -H "Accept: application/octet-stream" https://api.github.com/repos/alyssaruth/Entropy/releases/assets/%4

ren Entropy.jar Entropy_OLD.jar
ren %3 Entropy.jar
del Entropy_OLD.jar

start javaw -Xms256m -Xmx512m -jar Entropy.jar justUpdated trueLaunch
exit