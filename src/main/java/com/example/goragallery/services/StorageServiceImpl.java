package com.example.goragallery.services;

import com.example.goragallery.sql.Image;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class StorageServiceImpl implements StorageService {
    @Autowired
    private
    EntityManager entityManager;

    @Override
    public JSONObject save(byte[] data, String name, String contentType) {
        Image image = new Image();
        image.setData(data);
        image.setName(name);
        image.setContentType(contentType);
        // TODO: preview
        entityManager.persist(image);
        return toJSON(image);
    }

    public JSONObject toJSON(Image image) {
        JSONObject result = new JSONObject();
        result.put("imageId", image.getImageId());
        result.put("name", image.getName());
        result.put("contentType", image.getContentType());
        result.put("previewId", Optional.ofNullable(image.getPreview()).map(Image::getImageId).orElse(-1));
        return result;
    }
}
