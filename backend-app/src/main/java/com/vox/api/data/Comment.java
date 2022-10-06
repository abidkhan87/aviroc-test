package com.vox.api.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author abidkhan
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document
public class Comment {
	@Id
	private String comId;
	private String userId;
	private String fullName;
	private String text;
	private String filmId;
}
