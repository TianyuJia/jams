#!/bin/bash
# 1-deploy.sh is the first step in deploying the client. It is supposed to be
# run on the development machine. The script generates a static website and
# uploads the files to the server. Make sure the Node.js dev server is not
# running. Tested on macOS.

# Go to project root directory
DIR=~/Dev/Projects/JAMSWebClient;
cd "$DIR";

# Build static website
mv ./dist ~/.Trash/;
mkdir ./dist;
npm run build;

# Create archive
tar -c -J --options xz:9 -v ./dist > ./dist.tar.xz

# Upload to server
scp ./dist.tar.xz worf.geogr.uni-jena.de:/home/modis/websites/jams-web-client/

# Delete archive
mv ./dist.tar.xz ~/.Trash

echo "==> SSH into the server and execute '/home/modis/websites/jams-web-client/2-deploy.sh' there."
