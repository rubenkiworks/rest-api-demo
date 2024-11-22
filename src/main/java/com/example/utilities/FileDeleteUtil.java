package com.example.utilities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import org.springframework.stereotype.Component;

@Component
public class FileDeleteUtil {

    private Path foundFile;

    public String deleteFile(String fileCode) throws IOException {
        
        Path dirPath = Paths.get("Files-Upload");

        var responseAsMap = new HashMap<String, Object>();

        try {
            Files.list(dirPath).forEach(file -> {
                if (file.getFileName().toString().startsWith(fileCode)) {
                    foundFile = file;

                    return;
                }
            });
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
