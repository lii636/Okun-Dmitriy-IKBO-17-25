#!/usr/bin/env bash

text="$*"
len=${#text}
border=$(printf '+%*s+' "$((len + 2))" ' ' | tr ' ' '-')

echo "$border"
echo "| $text |"
echo "$border"