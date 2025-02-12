package com.example.expense.model;

import com.example.expense.enums.OperationType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Table(name = "TRANSACTIONS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account; // Счет, к которому относится транзакция

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OperationType type; // Тип транзакции (INCOME, EXPENSE)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Category category;

    @Column(nullable = false)
    private BigDecimal amount; // Сумма транзакции

    private String description; // Описание транзакции

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // Дата и время создания
}


