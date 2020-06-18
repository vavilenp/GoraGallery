package com.example.goragallery.sql;

import javax.persistence.*;

@Entity
@Table(name = "images")
public class ImageModel {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    Integer imageId;

    @Column(name = "name", columnDefinition="VARCHAR(128)")
    private String name;

    @Column(name = "contentType", columnDefinition="VARCHAR(1024)")
    private String contentType;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "preview_id")
    private ImageModel preview;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private ImageModel parent; // reverse to preview
    // TODO constraint: either parent or preview should be null

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

    public ImageModel getPreview() {
        return preview;
    }

    public void setPreview(ImageModel preview) {
        this.preview = preview;
    }

    public ImageModel getParent() {
        return parent;
    }

    public void setParent(ImageModel parent) {
        this.parent = parent;
    }
}
