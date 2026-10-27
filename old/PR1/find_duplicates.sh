#!/usr/bin/env bash
echo "Пары файлов с одинаковым содержанием:"

find "$1" -type f | sort | while read -r f1; do
  find "$1" -type f | sort | while read -r f2; do
    [ "$f1" \< "$f2" ] || continue  
    cmp -s "$f1" "$f2" && echo "$f1" && echo "$f2" && echo
  done
done
