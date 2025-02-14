package com.example.expense.repository;

import com.example.expense.model.Transaction;
import com.example.expense.model.UserReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserReferenceRepository extends JpaRepository<UserReference, Long> {

}
