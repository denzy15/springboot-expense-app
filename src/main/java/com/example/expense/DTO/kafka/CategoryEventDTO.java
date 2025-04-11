package com.example.expense.DTO.kafka;

import com.example.expense.enums.OperationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CategoryEventDTO {
    private Long categoryId;
    private String name;
    private OperationType type;
}
