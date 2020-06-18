package com.example.goragallery.services;

import org.json.simple.JSONObject;

public interface StorageService {
    JSONObject save(byte[] data, String name, String contentType);
}
