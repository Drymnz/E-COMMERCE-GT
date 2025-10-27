#docker 
# sudo pacman -S docker
#docker corre una imagen con el nombre "commerce" y usa el puerto "mi computadora: contenedor" -d en segundo plano la imagen de postgres
docker run -d --name commerce -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=123456 -e POSTGRES_DB=commerce -p 8880:5432 postgres

#ver los docker que tengo
docker ps -a

#inicia el docker
docker start commerce

#docker ejecuta ya el contendor name "commerce" en terminal
docker exec -it commerce bash

#iniciar usuario
psql -U postgres


