package fr.eni.bookhub.loan.dao;

import fr.eni.bookhub.loan.entity.Loan;

import java.util.List;

public interface ILoanDao {

    List<Loan> findAll();

    Loan findById(long id);
}
