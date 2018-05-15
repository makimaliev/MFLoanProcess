package kg.gov.mf.loan.process.repository;

import kg.gov.mf.loan.process.model.JobItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobItemRepository extends JpaRepository<JobItem, Long>{
}
