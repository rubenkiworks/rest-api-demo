package com.example.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileUploadUtil {
    public String saveFile(String fileName, MultipartFile multipartFile) throws IOException{
        String  fileCode;

        // Definimos donde se va a guardar el archivo recibido, es decir, la carpeta
        Path uploadPath = Paths.get("Files-Upload");

        // Preguntamos si existe la carpeta y de lo contrario la creamos
        if(!Files.exists(uploadPath)) 
            Files.createDirectories(uploadPath);

        // Ahora generamos el codigo alfanumerico de 8 caracteres, aleatorio, que vamos a utilizar
        // como prefijo del nombre del archivo recibido, para lo cual necesitamos agregar una dependencia
        // al pom.xml, que buscaremos en Maven Central (mvnrepository.com), y es de apache commons lang3
        // que nos proporcionara el metodo que necesitamos.
        fileCode = RandomStringUtils.randomAlphanumeric(8);

        // Ya podemos guardar el fichero en la carpeta especificada
        try (InputStream inputStream = multipartFile.getInputStream()) {

            Path destino = uploadPath.resolve(fileCode + "-" + fileName);
            Files.copy(inputStream, destino, StandardCopyOption.REPLACE_EXISTING);
            
        } catch (IOException ioe) {
            throw new IOException("Error guardando la imagen " + fileName, ioe);
        }

        return fileCode;
    }
}
