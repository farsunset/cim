#! /bin/bash  

java -Dcom.sun.akuma.Daemon=daemonized -Dspring.profiles.active=dev -Dserver.port=9090 -jar ./cim-boot-server-4.2.0.jar &