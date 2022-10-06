package com.vox.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author abidkhan
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message implements Serializable {
    private String message;
}