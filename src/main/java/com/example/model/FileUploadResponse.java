package com.example.model;

import lombok.Builder;

// URL (Uniform Resource Locator)
// URI (Uniform Resource Identifier)
@Builder
public record FileUploadResponse(String fileName, String downloadURI, long fileSize) {

}
