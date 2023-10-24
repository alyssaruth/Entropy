@Echo off
REM :loop

java -server -Xmx1024m -Xms1024m -Xmn512m -XX:SurvivorRatio=2 -XX:+UseConcMarkSweepGC -verbose:gc -XX:-UseGCOverheadLimit -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+HeapDumpOnOutOfMemoryError -jar EntropyServer.jar

REM set date=%date:~-4,4%%date:~-7,2%%date:~0,2%
REM set time=%time:~0,2%%time:~3,2%%time:~6,2%

REM move C:\Users\Administrator\Desktop\gc.log C:\Users\Administrator\Desktop\Old\gc.log
REM rename C:\Users\Administrator\Desktop\Old\gc.log gc%date%.log
REM goto loop
pause