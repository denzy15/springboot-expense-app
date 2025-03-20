package com.example.expense.DTO;

import com.example.expense.enums.OperationType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CategorySummaryResponseDTO {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    private OperationType type;

    @JsonProperty("amount")
    private BigDecimal amount;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategorySummaryResponseDTO that = (CategorySummaryResponseDTO) o;
        return id.equals(that.id) && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
