package gachonproject.mobile.domain.cosmetic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class CosmeticPurchaseLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cosmeticPurchaseLink_id")
    private Long id;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "cosmetic_id")
    private Cosmetic cosmetic;

    private String purchaseSite;

    private String price;

    private String url;

    private String rogoImage;

    public CosmeticPurchaseLink(Cosmetic cosmetic, String purchaseSite, String price, String url) {
        this.cosmetic = cosmetic;
        this.purchaseSite = purchaseSite;
        this.price = price;
        this.url = url;
    }

    public CosmeticPurchaseLink() {
    }
}
