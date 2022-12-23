package ru.kudinov.model;

import ru.kudinov.model.enums.entityEnums.DetailType;
import ru.kudinov.model.enums.entityEnums.ProductKind;
import ru.kudinov.model.interfaces.Producible;
import ru.kudinov.model.interfaces.ProducibleType;

import javax.persistence.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Detail implements Producible {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private Integer quantity;

    private Double price;

    private String description;

    @Enumerated(EnumType.STRING)
    private DetailType detailType;

    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "detail_images", joinColumns = @JoinColumn(name = "detail_id"))
    private Set<String> images = new HashSet<>();

    private boolean isActive;

    @Transient
    private final ProductKind PRODUCT_KIND = ProductKind.DETAIL;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DetailType getDetailType() {
        return detailType;
    }

    public void setDetailType(DetailType detailType) {
        this.detailType = detailType;
    }

    public ProductKind getPRODUCT_KIND() {
        return PRODUCT_KIND;
    }

    @Override
    public ProducibleType getProductType() {
        return getDetailType();
    }

    public Set<String> getImages() {
        if (this.images.size() == 0) return Collections.singleton("static/detailDefaultImage.png");
        return this.images;
    }

    public void setImages(Set<String> images) {
        this.images = images;
    }

    public void addImages(Set<String> imagesToAdd) {
        this.images.addAll(imagesToAdd);
    }

    public void deleteImage(String imageToDelete) {
        this.images.remove(imageToDelete);
    }

    public boolean isDefaultImage() {
        return this.images.size() == 0;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive() {
        this.isActive = true;
    }

    public void setNonActive() {
        this.isActive = false;
    }
}
