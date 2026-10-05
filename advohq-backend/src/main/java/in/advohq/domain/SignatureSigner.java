package in.advohq.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "signature_signers")
@Getter
@Setter
@NoArgsConstructor
public class SignatureSigner {

    public static final String PENDING = "PENDING";
    public static final String SIGNED = "SIGNED";

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "request_id", nullable = false)
    private SignatureRequestEntity request;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 200)
    private String email;

    // Named "company" (not "isCompany") so Lombok's generated accessors are
    // unambiguous: isCompany() / setCompany(boolean) either way.
    @Column(name = "is_company", nullable = false)
    private boolean company;

    @Column(name = "sign_order", nullable = false)
    private int signOrder;

    @Column(nullable = false, unique = true, length = 64)
    private String token;

    @Column(nullable = false, length = 20)
    private String status = PENDING;

    @Column(name = "signed_at")
    private Instant signedAt;

    @Column(name = "signed_name", length = 200)
    private String signedName;

    @Column(name = "signed_ip", length = 64)
    private String signedIp;

    @OneToMany(mappedBy = "signer", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt ASC")
    private List<SignatureField> fields = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
