package com.example.goragallery;

import com.example.goragallery.services.StorageService;
import com.example.goragallery.sql.ImageModel;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class GalleryController {
    @Autowired
    StorageService storageService;

    @RequestMapping("/upload")
    @ResponseBody
    public String upload(@RequestParam("file") MultipartFile file, @RequestParam String name) {
        try {
            return storageService.save(file.getBytes(), name, file.getContentType()).toJSONString();
        } catch (Throwable th) {
            th.printStackTrace();
            return wrapThrowable(th).toJSONString();
        }
    }

    @RequestMapping(value = "/image/{imageId}", method = RequestMethod.GET)
    public ResponseEntity<Resource> getImage(@PathVariable("imageId") int imageId) {
        ImageModel img;
        byte[] data;
        try {
            img = storageService.getImageModel(imageId);
            data = storageService.getImageData(imageId);
            if (img == null || data == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Throwable th) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", img.getContentType());
        return new ResponseEntity<>(new ByteArrayResource(data), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public String getAllImagesMetadata() {
        try {
            return storageService.getAllImagesDesc().toJSONString();
        } catch (Throwable th) {
            return wrapThrowable(th).toJSONString();
        }
    }

    @RequestMapping(value = "/delete/{imageId}", method = RequestMethod.DELETE)
    @ResponseBody
    public String removeImage(@PathVariable("imageId") int imageId) {
        try {
            JSONObject result = new JSONObject();
            result.put("num_removed", storageService.removeImage(imageId));
            return result.toJSONString();
        } catch (Throwable th) {
            return wrapThrowable(th).toJSONString();
        }
    }

    private JSONObject wrapThrowable(Throwable th) {
        JSONObject result = new JSONObject();
        result.put("error", th.getMessage());
        return result;
    }
}
