package com.imfine.ngs.game.repository;

import com.imfine.ngs.game.entity.GameNotice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * {@link GameNotice} 저장소 인터페이스.
 *
 * @author chan
 */
@Repository
public interface NoticeRepository extends JpaRepository<GameNotice, Long> {

    List<GameNotice> findByGameId(Long gameId);
}
