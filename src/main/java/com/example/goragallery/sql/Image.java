package com.example.goragallery.sql;

import javax.persistence.*;
import java.sql.Blob;

@Entity
@Table(name = "images")
public class Image {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    Integer imageId;

    @Column(name = "name", columnDefinition="VARCHAR(128)")
    private String name;

    @Column(name = "contentType", columnDefinition="VARCHAR(1024)")
    private String contentType;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "preview_id")
    private Image preview;

    public Integer getImageId() {
        return imageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Image getPreview() {
        return preview;
    }

    public void setPreview(Image preview) {
        this.preview = preview;
    }
}
