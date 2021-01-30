package kitchenpos.application;

import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.dao.TableGroupDao;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.TableGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TableGroupServiceTest {
    @Mock
    private OrderDao orderDao;
    @Mock
    private OrderTableDao orderTableDao;
    @Mock
    private TableGroupDao tableGroupDao;

    @InjectMocks
    private TableGroupService tableGroupService;

    private OrderTable firstOrderTable;
    private OrderTable secondOrderTable;

    @BeforeEach
    void setUp() {
        firstOrderTable = new OrderTable(10, true);
        secondOrderTable = new OrderTable(20, true);
    }

    @DisplayName("단체 지정을 생성")
    @Test
    void createTableGroup() {
        // given
        TableGroup tableGroup = new TableGroup(Arrays.asList(firstOrderTable, secondOrderTable));

        when(orderTableDao.findAllByIdIn(Arrays.asList(firstOrderTable.getId(), secondOrderTable.getId())))
                .thenReturn(Arrays.asList(firstOrderTable, secondOrderTable));
        when(tableGroupDao.save(tableGroup)).thenReturn(tableGroup);

        // when
        TableGroup createdTableGroup = tableGroupService.create(tableGroup);

        // then
        assertThat(createdTableGroup.getId()).isEqualTo(tableGroup.getId());
        assertThat(createdTableGroup.getOrderTables()).isEqualTo(tableGroup.getOrderTables());
        assertThat(createdTableGroup.getCreatedDate()).isNotNull();
    }

    @DisplayName("주문 테이블이 2개 이상 있어야 함")
    @Test
    void twoOrderTable() {
        // given
        TableGroup tableGroup = new TableGroup(Collections.singletonList(firstOrderTable));

        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            tableGroupService.create(tableGroup);
        });
    }

    @DisplayName("주문 테이블 상태가 비어있음이 아니면 생성할 수 없음")
    @Test
    void notEmptyOrderTable() {
        // given
        firstOrderTable.setEmpty(false);
        TableGroup tableGroup = new TableGroup(Arrays.asList(firstOrderTable, secondOrderTable));

        when(orderTableDao.findAllByIdIn(Arrays.asList(firstOrderTable.getId(), secondOrderTable.getId())))
                .thenReturn(Arrays.asList(firstOrderTable, secondOrderTable));

        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            tableGroupService.create(tableGroup);
        });
    }

    @DisplayName("단체 지정이 되어 있으면 생성할 수 없다.")
    @Test
    void alreadyExistTableGroupId() {
        // given
        firstOrderTable.setTableGroupId(1L);
        TableGroup tableGroup = new TableGroup(Arrays.asList(firstOrderTable, secondOrderTable));

        when(orderTableDao.findAllByIdIn(Arrays.asList(firstOrderTable.getId(), secondOrderTable.getId())))
                .thenReturn(Arrays.asList(firstOrderTable, secondOrderTable));

        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            tableGroupService.create(tableGroup);
        });
    }

    @DisplayName("단체 지정을 삭제할 수 있다.")
    @Test
    void ungroup() {
        // given
        TableGroup tableGroup = new TableGroup(Arrays.asList(firstOrderTable, secondOrderTable));
        firstOrderTable.setTableGroupId(1L);
        secondOrderTable.setTableGroupId(1L);

        when(orderTableDao.findAllByTableGroupId(tableGroup.getId()))
                .thenReturn(Arrays.asList(firstOrderTable, secondOrderTable));
        when(orderDao.existsByOrderTableIdInAndOrderStatusIn(Arrays.asList(firstOrderTable.getId(), secondOrderTable.getId())
                , Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name())))
                .thenReturn(false);

        // when
        tableGroupService.ungroup(tableGroup.getId());

        // then
        assertThat(firstOrderTable.getTableGroupId()).isNull();
        assertThat(secondOrderTable.getTableGroupId()).isNull();
    }

    @DisplayName("조리중 이거나 식사일때는 단체 지정을 해제할 수 없음")
    @Test
    void ungroupFailWhenIsCookingOrMeal() {
        // given
        TableGroup tableGroup = new TableGroup(Arrays.asList(firstOrderTable, secondOrderTable));
        firstOrderTable.setTableGroupId(1L);
        secondOrderTable.setTableGroupId(1L);

        when(orderTableDao.findAllByTableGroupId(tableGroup.getId()))
                .thenReturn(Arrays.asList(firstOrderTable, secondOrderTable));
        when(orderDao.existsByOrderTableIdInAndOrderStatusIn(Arrays.asList(firstOrderTable.getId(), secondOrderTable.getId())
                , Arrays.asList(OrderStatus.COOKING.name(), OrderStatus.MEAL.name())))
                .thenReturn(true);

        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            tableGroupService.ungroup(tableGroup.getId());
        });
    }
}