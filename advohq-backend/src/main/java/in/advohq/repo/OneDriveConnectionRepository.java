package in.advohq.repo;

import in.advohq.domain.OneDriveConnection;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface OneDriveConnectionRepository extends JpaRepository<OneDriveConnection, UUID> {

    Optional<OneDriveConnection> findByUserId(UUID userId);

    /**
     * Row-locked read used while refreshing tokens, so two concurrent requests
     * don't both spend the same (rotating) refresh token.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from OneDriveConnection c where c.userId = :userId")
    Optional<OneDriveConnection> findByUserIdForUpdate(@Param("userId") UUID userId);

    @Modifying
    @Query("delete from OneDriveConnection c where c.userId = :userId")
    int deleteByUserId(@Param("userId") UUID userId);
}
