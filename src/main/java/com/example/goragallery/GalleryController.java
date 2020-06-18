package com.example.goragallery;

import com.example.goragallery.services.StorageService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
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

    private JSONObject wrapThrowable(Throwable th) {
        JSONObject result = new JSONObject();
        result.put("success", 0);
        result.put("msg", th.getMessage());
        return result;
    }
}
