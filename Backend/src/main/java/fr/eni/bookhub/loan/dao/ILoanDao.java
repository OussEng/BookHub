package fr.eni.bookhub.loan.dao;

import fr.eni.bookhub.loan.entity.Loan;

import java.util.List;

public interface ILoanDao {

    public List<Loan> findAll();

    public Loan findById(long id);
}
