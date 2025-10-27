rm -rf node_modules
rm -f package-lock.json

npm cache clean --force

ng build --configuration production

git checkout deploy 2>/dev/null || git checkout -b deploy

git push origin deploy