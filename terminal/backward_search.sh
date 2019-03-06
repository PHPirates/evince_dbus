#!/usr/bin/env bash

# By running this script, you can see all the SyncSource signals emitted. and the parameters
# Instructions: run this script, open a pdf in Evince which was compiled with synctex and ctrl+click somewhere. You should see a message appearing in stdout with the SyncSource signal which was emitted

main()
{
    # Create a temporary file which will hold the output of dbus-monitor
    output=$(mktemp "${TMPDIR:-/tmp/}$(basename 0).XXX")
    echo "Output file: "
    echo ${output}

    # Start the monitor process (defined below) in the background, redirect output to the temporary file
    monitor &> ${output} &

    echo "Waiting:"

    while [[ true ]]; do
        # grep the output for the SyncSource signal
        until grep -q SyncSource ${output}
        do
            : # no-op
        done
        echo
        echo "SyncSource signal received, content:"

        # Print the next lines
        grep --text -A 6 SyncSource ${output}

        # Empty temp file
        truncate -s 0 ${output}
    done
}

# Copy the output of dbus-monitor
# Note that it is possible to monitor signals of only one object path, but then you cannot view the content of the signal
monitor()
{
    dbus-monitor |
    while read -r line; do
        echo ${line}
    done
}

main