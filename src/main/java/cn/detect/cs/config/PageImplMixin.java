package cn.detect.cs.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class PageImplMixin<T> {

    @JsonCreator
    public PageImplMixin(
            @JsonProperty("content") List<T> content,
            @JsonProperty("pageable") PageRequest pageable,
            @JsonProperty("totalElements") long total) {
    }

    @JsonProperty("pageable")
    abstract PageRequest getPageable();

    @JsonProperty("totalElements")
    abstract long getTotalElements();
}