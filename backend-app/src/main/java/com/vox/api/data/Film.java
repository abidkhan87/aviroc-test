package com.vox.api.data;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


/**
 * @author abidkhan
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document
@ToString
public class Film {
	
	@Id
	private String id;
	
	private String name;
	
	private String description;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm", iso = ISO.DATE_TIME)
	private LocalDateTime releaseDate;
	private Integer rating;
	
	private Float price;
	
	private String country;
	
	private List<String> genre;
	
	private String slugLine;

	private List<Comment> comments;

}
