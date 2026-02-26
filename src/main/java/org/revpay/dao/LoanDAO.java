package org.revpay.dao;

import org.revpay.model.Loan;

import java.math.BigDecimal;
import java.util.Optional;

public interface LoanDAO {

    Long save(Loan loan);

    Optional<Loan> findByIdAndUser(Long loanId, Long userId);

    void updateLoanAmounts(Long loanId,
                           BigDecimal paidAmount,
                           BigDecimal remainingAmount,
                           String status);
}