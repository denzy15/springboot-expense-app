package com.example.expense.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "categories")
@NoArgsConstructor
@Data
public class Category {
    @Id
    @GeneratedValue
    private long id;

    private String name;

    @ManyToOne
    private User createdBy;
}
