#!/bin/bash

# check if node processes are running and kill
if [ps -ef | grep | node | wc -l > 1]
then 
for line in $(ps -ef|grep node|awk -F ' ' '{print $2}')
do
  kill -9 $line
done
fi

# check if npm processes are running and kill
if [ps -ef | grep | npm | wc -l > 1]
then 
for line in $(ps -ef|grep npm|awk -F ' ' '{print $2}')
do
  kill -9 $line
done
fi

# Remove the directory if pre-existing
cd ~
if [ -d weather-app ]
then rm -rf weather-app
fi

# Clone and start node processes
cd ~
git clone https://github.com/rohitgabriel/weather-app.git
cd weather-app/
cd server/
npm install
cd ../client/
npm install
cd ../server
node server.js &
cd ../client/
npm start &