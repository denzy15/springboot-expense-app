package com.example.expense.DTO;

import com.example.expense.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BudgetMemberDTO {
    private Long userId;
    private String email;
    private String username;
    private Role role;
}
