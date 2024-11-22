package com.example.utilities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Component;

@Component
public class FileDeleteUtil {

    private Path foundFile;

    public String deleteFile(String fileCode) throws IOException {
        
        Path dirPath = Paths.get("Files-Upload");

        try {
            foundFile = Files.list(dirPath)
            .filter(file -> file.getFileName().toString().startsWith(fileCode))
            .findFirst().get();
        } catch (IOException e) {
            throw new IOException("Error fatal buscando el fichero", e);
        }

        if (foundFile != null) {
            if(foundFile.toFile().delete()){
                return "La imagen del producto se ha eliminado correctamente";
            } else{
                return "No se ha podido eliminar la imagen del producto";
            }
        } 

        return null;
    }
}
