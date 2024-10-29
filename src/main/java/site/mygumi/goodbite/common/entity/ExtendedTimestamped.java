package site.mygumi.goodbite.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;

/**
 * 엔티티의 삭제 시간을 기록하는 필드를 추가한 추상 클래스입니다.
 * <p>
 * 이 클래스는 {@link Timestamped} 클래스를 상속하여 기본 생성 및 수정 시간 외에 삭제 시간을 저장할 수 있습니다.
 * {@code @MappedSuperclass} 어노테이션을 통해 JPA 엔티티에서 상속받아 사용할 수 있습니다.
 * </p>
 * <p>
 * soft-delete 데이터에 활용할 수 있습니다.
 * </p>
 *
 * @author a-white-bit
 */
@Getter
@MappedSuperclass
public abstract class ExtendedTimestamped extends Timestamped {

    /**
     * 엔티티의 삭제 시간을 기록하는 필드입니다.
     */
    @Column(name = "delete_date")
    protected LocalDateTime deletedAt;

}