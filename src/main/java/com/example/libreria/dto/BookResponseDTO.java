package com.example.libreria.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookResponseDTO {
    
    private Long externalId;
    private String title;
    private List<String> authorName;
    private Integer firstPublishYear;
    private Integer editionCount;
    private Boolean hasFulltext;
    private BigDecimal price;
    private Integer stockQuantity;
    private Integer availableQuantity;
    

    public Long getExternalId() {
        return externalId;
    }
    
    public void setExternalId(Long externalId) {
        this.externalId = externalId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public List<String> getAuthorName() {
        return authorName;
    }
    
    public void setAuthorName(List<String> authorName) {
        this.authorName = authorName;
    }
    
    public Integer getFirstPublishYear() {
        return firstPublishYear;
    }
    
    public void setFirstPublishYear(Integer firstPublishYear) {
        this.firstPublishYear = firstPublishYear;
    }
    
    public Integer getEditionCount() {
        return editionCount;
    }
    
    public void setEditionCount(Integer editionCount) {
        this.editionCount = editionCount;
    }
    
    public Boolean getHasFulltext() {
        return hasFulltext;
    }
    
    public void setHasFulltext(Boolean hasFulltext) {
        this.hasFulltext = hasFulltext;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public Integer getStockQuantity() {
        return stockQuantity;
    }
    
    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
    
    public Integer getAvailableQuantity() {
        return availableQuantity;
    }
    
    public void setAvailableQuantity(Integer availableQuantity) {
        this.availableQuantity = availableQuantity;
    }
}

