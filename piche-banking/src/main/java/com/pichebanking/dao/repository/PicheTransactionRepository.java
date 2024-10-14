package com.pichebanking.dao.repository;

import com.pichebanking.dao.entity.PicheTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PicheTransactionRepository extends JpaRepository<PicheTransaction, Long> {
}
