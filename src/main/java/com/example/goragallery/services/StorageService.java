package com.example.goragallery.services;

import com.example.goragallery.sql.Image;
import org.json.simple.JSONObject;

import java.io.IOException;

public interface StorageService {
    JSONObject save(byte[] data, String name, String contentType) throws IOException;
    Image getImageModel(int id);
    byte[] getImageData(int id);
}
