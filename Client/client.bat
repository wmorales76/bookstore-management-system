@echo off

rem Compile Java files
javac  edu/suagm/tftpExmpleClient/*.java

rem Run the compiled Java program
java edu.suagm.tftpExmpleClient.tftpClient 127.0.0.1 8888