package com.example.utilities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;

@Component
public class FileDownloadUtil {

    private Path foundFile;

    @SuppressWarnings("UnnecessaryReturnStatement")
    public Resource getFileAsResource(String fileCode) throws IOException {
        
        Path dirPath = Paths.get("Files-Upload");

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
            return new UrlResource(foundFile.toUri());
        } 

        return null;
    }
}
