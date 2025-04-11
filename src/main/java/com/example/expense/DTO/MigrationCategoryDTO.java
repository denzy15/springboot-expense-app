package com.example.expense.DTO;

import com.example.expense.enums.OperationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class MigrationCategoryDTO {
    private Long categoryId;
    private String name;
    private OperationType type;
}
