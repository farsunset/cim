#! /bin/bash  

java -Dcom.sun.akuma.Daemon=daemonized -Dspring.profiles.active=pro -jar ./cim-boot-server-4.0.0.jar &