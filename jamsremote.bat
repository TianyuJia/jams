@echo off

SET platform=win32
SET VM=java
SET OPTIONS=-Xms128M -Xmx512M -XX:MaxPermSize=128m -Dsun.java2d.d3d=false -Djava.library.path=bin/%platform% -splash:

@echo on
%VM% %OPTIONS% -jar lib/jamsremote-starter.jar %*
