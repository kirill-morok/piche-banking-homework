package com.pichebanking.dao.entity;

import com.pichebanking.util.enums.TransactionType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "piche_transaction")
@Accessors(chain = true)
public class PicheTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BaseGenerator")
    @SequenceGenerator(name = "BaseGenerator", sequenceName = "base_sequence", allocationSize = 1)
    @Column
    private Long id;

    @Column
    private BigDecimal funds;

    @Enumerated(EnumType.STRING)
    @Column
    private TransactionType transactionType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_account_id", referencedColumnName = "id", updatable = false, nullable = false)
    private Account sourceAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_account_id", referencedColumnName = "id", updatable = false)
    private Account targetAccount;

}
