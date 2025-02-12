package com.example.expense.DTO;

import com.example.expense.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BudgetMemberDTO {
    private Long id;
    private String email;
    private String username;
    private Role role;
}
