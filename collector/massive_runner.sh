#!/usr/bin/env bash

NUMBER_OF_COLLECTORS=${1}
shift
COLLECTOR_ARGUMENTS=$@

trap 'kill $(jobs -p)' EXIT

I=1
while (( ${I} <= ${NUMBER_OF_COLLECTORS} )); do
    ./collector.py ${COLLECTOR_ARGUMENTS} &
    I=$((${I} + 1))
done

wait