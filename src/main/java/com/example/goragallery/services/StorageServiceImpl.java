package com.example.goragallery.services;

import com.example.goragallery.sql.ImageModel;
import org.json.simple.JSONArray;
import org.springframework.core.env.Environment;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
    private Integer previewWidth;

    @Override
    public JSONObject save(byte[] data, String name, String contentType) throws IOException {
        return toJSON(save(data, name, contentType, true));
    }

    private ImageModel save(byte[] data, String name, String contentType, boolean withPreview) throws IOException {
        ImageModel preview = null;
        if (withPreview) {
            preview = save(createPreview(data), name + "_preview", "image/png", false);
        }

        ImageModel imageModel = new ImageModel();
        imageModel.setName(name);
        imageModel.setContentType(contentType);
        if (preview != null) {
            imageModel.setPreview(preview);
        }
        entityManager.persist(imageModel); // auto rollback on IOException
        if (preview != null) {
            preview.setParent(imageModel);
            entityManager.persist(preview);
        }
        Files.write(getSavePath().resolve(imageModel.getImageId().toString()), data, StandardOpenOption.CREATE);
        return imageModel;
    }

    private byte[] createPreview(byte[] source) throws IOException {
        BufferedImage img = ImageIO.read(new ByteArrayInputStream(source));
        int destWidth = getPreviewWidth();
        int destHeight = getPreviewWidth() * img.getHeight(null) / img.getWidth(null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(resize(img, destHeight, destWidth), "png", baos);
        return baos.toByteArray();
    }

    private BufferedImage resize(BufferedImage img, int height, int width) {
        Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return resized;
    }

    @Override
    public ImageModel getImageModel(int id) {
        return entityManager.find(ImageModel.class, id);
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

    @Override
    public JSONArray getAllImagesDesc() {
        JSONArray result = new JSONArray();
        entityManager.createQuery("from ImageModel as im where im.parent is null")
                .getResultStream().map(obj -> toJSON((ImageModel) obj)).forEach(result::add);
        return result;
    }

    @Override
    public int removeImage(int id) {
        ImageModel img = entityManager.find(ImageModel.class, id);
        if (img == null) {
            return 0;
        }

        if (img.getParent() != null) { // in case of deleting preview retain original
            img.getParent().setPreview(null);
            entityManager.persist(img.getParent());
        }

        Optional<Integer> previewId = Optional.ofNullable(img.getPreview()).map(ImageModel::getImageId);
        if (previewId.isPresent()) { // in case of deleting original remove preview as well
            entityManager.remove(img.getPreview());
        }
        entityManager.remove(img);
        removeFromFS(id); // only when entityManager::remove succeeded
        previewId.ifPresent(this::removeFromFS);
        return previewId.isPresent() ? 2 : 1;
    }

    private void removeFromFS(int id) {
        try {
            Files.delete(getSavePath().resolve(Integer.toString(id)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Path getSavePath() {
        if (savePath == null) {
            savePath = Paths.get(environment.getProperty("images.path"));
        }
        return savePath;
    }

    private Integer getPreviewWidth() {
        if (previewWidth == null) {
            previewWidth = Integer.valueOf(environment.getProperty("preview.width"));
        }
        return previewWidth;
    }

    private JSONObject toJSON(ImageModel imageModel) {
        JSONObject result = new JSONObject();
        result.put("imageId", imageModel.getImageId());
        result.put("name", imageModel.getName());
        result.put("contentType", imageModel.getContentType());
        ImageModel preview = imageModel.getPreview();
        if (preview != null) {
            result.put("preview", toJSON(preview));
        }
        return result;
    }
}
