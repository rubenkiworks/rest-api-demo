package com.example.utilities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;

import com.example.entities.Producto;
import com.example.services.ProductoService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FileDownloadUtil {

    private Path foundFile;
    private final ProductoService productoService;

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

    public Resource getFileAsResourceByIdProducto(int idProducto) throws IOException {
        
        Producto producto = productoService.findById(idProducto);

        String imagenProducto = producto.getImagenProducto();

        String fileCode = imagenProducto.split("-")[0];

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
