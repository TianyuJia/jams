@echo off

SET VM=java
SET OPTIONS=-Xms128M -Xmx512M -Dsun.java2d.d3d=false -Djava.library.path=bin

@echo on
%VM% %OPTIONS% -jar lib/jams-starter.jar %1 %2 %3 %4
