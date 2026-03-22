package lv.emendatus.Destiny_PropMan.repository.interfaces;

import lv.emendatus.Destiny_PropMan.domain.entity.Bill;
import lv.emendatus.Destiny_PropMan.domain.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {

    List<Bill> findByProperty(Property property);

    @Query("SELECT b FROM Bill b WHERE b.property = :property AND b.dueDate >= :start AND b.dueDate < :end")
    List<Bill> findByPropertyAndDueDateRange(@Param("property") Property property,
                                             @Param("start") Timestamp start,
                                             @Param("end") Timestamp end);

    @Query("SELECT b FROM Bill b WHERE b.property = :property AND b.isPaid = :paid")
    List<Bill> findByPropertyAndPaidStatus(@Param("property") Property property, @Param("paid") boolean paid);

    List<Bill> findByPropertyAndExpenseCategory(Property property, String expenseCategory);
}
