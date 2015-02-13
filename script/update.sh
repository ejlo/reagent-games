#!/bin/sh

current_dir=$(dirname "$0")
cd $current_dir
cd ..

git checkout master
git pull

rm -rf target/games/*
mkdir -p target/games

for dir in */; do
    if [[ $dir != "target/" ]]; then
        cd $dir
        game=$(basename "$PWD")
        echo -e "\n\nWorking with $game..."
        lein do clean, prod
        cp -r resources/public ../target/games/$game
        cd ..
    fi
done

git checkout gh-pages

rm -rf ../games/*

cp -r target/games/* games/
