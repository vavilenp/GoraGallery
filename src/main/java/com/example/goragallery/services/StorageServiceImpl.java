package com.example.goragallery.services;

import com.example.goragallery.sql.Image;
import org.springframework.core.env.Environment;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.*;
import java.util.Optional;

@Service
@Transactional
public class StorageServiceImpl implements StorageService {
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private Environment environment;
    private Path savePath;

    @Override
    public JSONObject save(byte[] data, String name, String contentType) throws IOException {
        return save(data, name, contentType, true);
    }

    private JSONObject save(byte[] data, String name, String contentType, boolean withPreview) throws IOException {
        if (withPreview) {
            // TODO
        }

        Image image = new Image();
        image.setName(name);
        image.setContentType(contentType);
        entityManager.persist(image); // auto rollback on IOException
        Files.write(getSavePath().resolve(image.getImageId().toString()), data, StandardOpenOption.CREATE_NEW);
        return toJSON(image);
    }

    @Override
    public Image getImageModel(int id) {
        return entityManager.find(Image.class, id);
    }

    @Override
    public byte[] getImageData(int id) {
        try {
            return Files.readAllBytes(getSavePath().resolve(Integer.toString(id)));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    private Path getSavePath() {
        if (savePath == null) {
            savePath = Paths.get(environment.getProperty("images.path"));
        }
        return savePath;
    }

    private JSONObject toJSON(Image image) {
        JSONObject result = new JSONObject();
        result.put("imageId", image.getImageId());
        result.put("name", image.getName());
        result.put("contentType", image.getContentType());
        result.put("previewId", Optional.ofNullable(image.getPreview()).map(Image::getImageId).orElse(-1));
        return result;
    }

    private JSONObject error(String msg) {
        JSONObject result = new JSONObject();
        result.put("error", msg);
        return result;
    }
}
