package org.unir.books.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "books")
public class BookElastic {

    @Id
    private String isbn;
    @Field(type = FieldType.Text, name = "title")
    private String title;
    private String author;
    private String genre;
    private String editorial;
    private String language;
    private Integer pages;
    private Integer year;
    private String description;
    private Double price = 0.0;
    private String image;
    @Field(type = FieldType.Date, name = "create_at", format = DateFormat.year_month_day)
    private LocalDate createAt;
    private Integer status;
    private Integer score;

}