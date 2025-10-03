#docker corre una imagen con el nombre "commerce" y usa el puerto "mi computadora: contenedor" -d en segundo plano la imagen de postgres
docker run --name commerce -e POSTGRES_PASSWORD=123456 -p 8880:3245 -d postgres

#ver los docker que tengo
docker ps -a

#inicia el docker
docker start commerce

#docker ejecuta ya el contendor name "commerce" en terminal
docker exec -it commerce bash

#iniciar postgres
psql -U postgres
