rm -rf node_modules
rm -f package-lock.json

npm cache clean --force

npm install

ng build --configuration production

## vas a mover el dist mejor, solo mover el "browser"

git checkout deploy 2>/dev/null || git checkout -b deploy

git add .

git commit -m "otra ves"

git push origin deploy

git checkout main

##nota para el ngrok

#es el token para permiso con mi cuenta de la pagina
##ngrok config add-authtoken 28IPmpMCGImATLjOCSmCnMWmGOq_qY6SiDDDkpME24KZUMnf

## que puerto se va abrier
##ngrok http 8080

##screen

