@echo off

rem Compile Java files
javac  edu/suagm/soe/tftpexample/*.java

rem compile the data structures
javac library/*.java

rem Run the compiled Java program
java edu.suagm.soe.tftpexample.tftpServer 8888