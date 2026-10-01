#!/usr/bin/env bash

echo "$(grep -o '[A-Za-z_][A-Za-z0-9_]*' "$1" | sort -u | tr '\n' ' ')"