#!/usr/bin/env bash

# By running this script, you can see all the SyncSource signals emitted. and the parameters

main()
{
    output=$(mktemp "${TMPDIR:-/tmp/}$(basename 0).XXX")
    echo "Output file: "
    echo ${output}
    server &> ${output} &
    echo "Waiting:"
    while [[ true ]]; do
        until grep -q SyncSource ${output}
        do
            :
        done
        echo
        echo "SyncSource signal received, content:"
        grep --text -A 6 SyncSource ${output}
        # Empty output
        truncate -s 0 ${output}
    done
}

server()
{
    dbus-monitor |
    while read -r line; do
        echo ${line}
    done
}

main "$@"