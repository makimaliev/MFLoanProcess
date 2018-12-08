package kg.gov.mf.loan.process.repository;

import kg.gov.mf.loan.process.model.FixedDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FixedDateRepository extends JpaRepository<FixedDate, Long> {
    List<FixedDate> getFixedDateByStatusEquals(int status);
}
