#!/bin/bash

"$PWD/oc.exe" login https://console.ocp.corp.myservices.digital:8443 -u $oc_login -p $oc_password
"$PWD/oc.exe" project portal-dev

while true; do
    
    "$PWD/oc.exe" port-forward service/mongodb4 9019:27017

    echo "connection is broken, start reconnecting"
done