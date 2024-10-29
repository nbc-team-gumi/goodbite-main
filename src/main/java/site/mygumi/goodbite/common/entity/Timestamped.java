package site.mygumi.goodbite.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 엔티티의 생성 시간과 수정 시간을 자동으로 관리하는 추상 클래스입니다.
 * <p>
 * 이 클래스는 {@code @CreatedDate}와 {@code @LastModifiedDate} 어노테이션을 사용하여 JPA 엔티티에 생성 시간과 수정 시간을 자동으로
 * 기록합니다. {@code @MappedSuperclass} 어노테이션을 통해 다른 엔티티가 상속받아 사용할 수 있습니다.
 * </p>
 *
 * @author a-white-bit
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Timestamped {

    /**
     * 엔티티가 생성된 시간을 기록하는 필드입니다.
     * <p>엔티티 생성 시 자동으로 설정되며, 이후 변경되지 않습니다.</p>
     */
    @Column(name = "create_date", updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    /**
     * 엔티티가 마지막으로 수정된 시간을 기록하는 필드입니다.
     * <p>엔티티가 업데이트될 때마다 자동으로 갱신됩니다.</p>
     */
    @Column(name = "update_date")
    @LastModifiedDate
    private LocalDateTime updatedAt;
}