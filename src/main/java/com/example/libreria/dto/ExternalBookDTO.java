package com.example.libreria.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExternalBookDTO {
    
    private Long id;
    
    @JsonProperty("has_fulltext")
    private Boolean hasFulltext;
    
    @JsonProperty("edition_count")
    private Integer editionCount;
    
    private String title;
    
    @JsonProperty("author_name")
    private List<String> authorName;
    
    @JsonProperty("first_publish_year")
    private Integer firstPublishYear;
    
    private BigDecimal price;
    

    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Boolean getHasFulltext() {
        return hasFulltext;
    }
    
    public void setHasFulltext(Boolean hasFulltext) {
        this.hasFulltext = hasFulltext;
    }
    
    public Integer getEditionCount() {
        return editionCount;
    }
    
    public void setEditionCount(Integer editionCount) {
        this.editionCount = editionCount;
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
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}

