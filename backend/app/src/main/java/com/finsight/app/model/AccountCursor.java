package com.finsight.app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "account_cursors")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountCursor {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String userId;
    private String itemId;
    private String accountId;
    private String lastFour;
    private String institutionId;
    private String cursor;
}

