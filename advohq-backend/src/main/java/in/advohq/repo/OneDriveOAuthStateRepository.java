package in.advohq.repo;

import in.advohq.domain.OneDriveOAuthState;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface OneDriveOAuthStateRepository extends JpaRepository<OneDriveOAuthState, UUID> {

    /** Locked so a ticket can't be spent twice by two racing requests. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from OneDriveOAuthState s where s.ticketHash = :hash")
    Optional<OneDriveOAuthState> findByTicketHashForUpdate(@Param("hash") String hash);

    /** Locked so a state can't be redeemed twice by two racing callbacks. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from OneDriveOAuthState s where s.stateHash = :hash")
    Optional<OneDriveOAuthState> findByStateHashForUpdate(@Param("hash") String hash);

    @Modifying
    @Query("delete from OneDriveOAuthState s where s.expiresAt < :now")
    int deleteExpired(@Param("now") Instant now);
}
