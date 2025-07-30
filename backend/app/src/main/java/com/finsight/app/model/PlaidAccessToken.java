package com.finsight.app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "plaid")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaidAccessToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String itemId;
    private String accessToken;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public PlaidAccessToken(String itemId, String accessToken, User loggedUser) {
        this.itemId = itemId;
        this.accessToken = accessToken;
        this.user = loggedUser;
    }
}
