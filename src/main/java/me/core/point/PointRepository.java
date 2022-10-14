package me.core.point;

import me.core.point.wallet.PointWallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointRepository extends JpaRepository<Point, Long>,PointCustomRepository {

    List<Point> findByPointWallet(PointWallet pointWallet);

}
