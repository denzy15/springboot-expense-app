package com.example.expense.model;

import com.example.expense.DTO.BudgetMemberDTO;
import com.example.expense.DTO.MigrationBudgetAccessDTO;
import com.example.expense.DTO.MigrationCategoryDTO;
import com.example.expense.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "BUDGET_MEMBERS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "budget_id", nullable = false)
    @JsonIgnore
    private Budget budget;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserReference user;

    @Enumerated(EnumType.STRING)
    private Role role;

    public BudgetMemberDTO toDTO() {
        return new BudgetMemberDTO(
                this.user.getId(),
                this.user.getUsername(),
                this.user.getEmail(),
                this.role
        );
    }

    public MigrationBudgetAccessDTO convertToMigrationDTO() {
        return new MigrationBudgetAccessDTO(
                this.budget.getId(),
                this.user.getId()
        );
    }

}
