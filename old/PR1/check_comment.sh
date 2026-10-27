#!/usr/bin/env bash

file="$1"
ext="${file##*.}"
firstline=$(head -n 1 "$file" )

if [ "$ext" = 'c' ] || [ "$ext" = 'js' ]; then
    if [ "${firstline:0:2}" = '//' ] || [ "${firstline:0:2}" = '/*' ]; then
        echo "В первой строке файла $file есть комментарий"
    else
        echo "В первой строке файла $file нет комментария"
    fi
fi

if [ "$ext" = 'py' ]; then
    if [ "${firstline:0:1}" = '#' ] || [ "${firstline:0:3}" = '"""' ] || [ "${firstline:0:3}" = "'''" ]; then
        echo "В первой строке файла $file есть комментарий"
    else
        echo "В первой строке файла $file нет комментария"
    fi
fi