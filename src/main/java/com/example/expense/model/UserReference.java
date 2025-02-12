package com.example.expense.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "USER_REFERENCES")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserReference {
    @Id
    private Long id;

    private String email;

    private String username;
}
