package kitchenpos.table.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kitchenpos.common.vo.TableGroupId;

public interface OrderTableRepository extends JpaRepository<OrderTable, Long> {
    List<OrderTable> findAllByIdIn(List<Long> ids);

    List<OrderTable> findAllByTableGroupId(TableGroupId tableGroupId);

    List<OrderTable> findByIdIn(List<Long> orderTableIds);
}