package com.example.expense.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Calendar;

@Entity
@Table(name = "expenses")
@Data
@NoArgsConstructor
public class Expense {
    @Id
    @GeneratedValue
    private long id;

    private String title;

    private String description;

    private Category category;

    @ManyToOne
    private User createdBy;

    private Calendar date;
}
