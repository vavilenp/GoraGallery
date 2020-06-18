package com.example.goragallery.services;

import com.example.goragallery.sql.ImageModel;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;

public interface StorageService {

    JSONObject save(byte[] data, String name, String contentType) throws IOException;

    ImageModel getImageModel(int id);
    byte[] getImageData(int id);

    JSONArray getAllImagesDesc();

    /**
     * @return number of images removed (0 to 2)
     */
    int removeImage(int id);
}
