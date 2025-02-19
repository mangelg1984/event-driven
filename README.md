Pasos para ejecutar el proyecto

1.- Descargar Axon Server de la siguiente liga: https://www.axoniq.io/download

2.- Descomprimir en algun directorio que tenga permisos de ejecucion (AxonServer-2024.2.2)

3.- posicionarse dentro del directorio AxonServer-2024.2.2 y crear el directorio config
    **AxonServer-2024.2.2/config/**
    
4.- Dentro del directorio **AxonServer-2024.2.2/config/** colocar el archivo axonserver.properties 
    que se encuentra en la carpeta config en la raiz de este repositorio
    
5.- Ejecutar el siguiente comando dentro del directorio AxonServer-2024.2.2 sobre una terminal 

    java -jar axonserver.jar
    
6.- Una vez se este ejecutando Axon Server, ahora iniciar el microservicio de DiscoveryServer con el 
    siguiente comando:
    
    mvn spring-boot:run

7.- Iniciar el microservicio de ApiGateway con el siguiente comando:

    mvn spring-boot:run

8.- Iniciar el microservicio de ProductService con el siguiente comando:

    mvn spring-boot:run
