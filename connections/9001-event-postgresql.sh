#!/bin/bash

"$PWD/oc.exe" login https://console.ocp.corp.myservices.digital:8443 -u $oc_login -p $oc_password
"$PWD/oc.exe" project portal-dev

while true; do
    
    "$PWD/oc.exe" port-forward service/event-postgresql 9001:5432

    echo "connection is broken, start reconnecting"
done