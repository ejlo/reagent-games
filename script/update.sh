#!/bin/sh

current_dir=$(dirname "$0")
cd $current_dir

for dir in ../*/; do
    if [[ $dir != "../script/" ]] && [[ $dir != "../games/" ]]; then
        cd $dir
        game=$(basename "$PWD")
        echo -e "\n\nWorking with $game..."
        lein do clean, prod
        rm -rf ../games/$game
        cp -r resources/public ../games/$game
        cd ../$current_dir
    fi
done
