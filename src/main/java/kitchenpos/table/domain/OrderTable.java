package kitchenpos.table.domain;

import kitchenpos.order.domain.*;
import kitchenpos.table.dto.OrderTableRequest;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
public class OrderTable {

    private static final String EXCEPTION_MESSAGE_MIN_NUMBER_OF_GUESTS = "손님의 수는 %s보다 작을수 없습니다.";
    private static final int MIN_NUMBER_OF_GUESTS = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "table_group_id")
    private Long tableGroupId;

    @Column(name = "number_of_guests")
    private int numberOfGuests;

    @Column(name = "empty")
    private boolean empty;

    @Embedded
    private Orders orders;

    public OrderTable() {
    }

    public OrderTable(int numberOfGuests, boolean empty) {
        this.numberOfGuests = numberOfGuests;
        this.empty = empty;
    }

    public OrderTable(Long tableGroupId, int numberOfGuests, boolean empty) {
        this.tableGroupId = tableGroupId;
        this.numberOfGuests = numberOfGuests;
        this.empty = empty;
    }

    public Long getId() {
        return id;
    }

    public Long getTableGroupId() {
        return tableGroupId;
    }

    public void groupBy(final Long tableGroupId) {
        this.tableGroupId = tableGroupId;
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void changeNumberOfGuests(int numberOfGuests) {
        if (numberOfGuests < MIN_NUMBER_OF_GUESTS) {
            throw new IllegalArgumentException(String.format(EXCEPTION_MESSAGE_MIN_NUMBER_OF_GUESTS, MIN_NUMBER_OF_GUESTS));
        }

        if (isEmpty()) {
            throw new IllegalArgumentException("빈테이블은 손님의 수를 변경할수 없습니다.");
        }

        this.numberOfGuests = numberOfGuests;
    }

    public void changeEmpty(OrderTableRequest orderTableRequest) {
        if (Objects.nonNull(getTableGroupId())) {
            throw new IllegalArgumentException("단체테이블인 경우 테이블을 비울수 없습니다.");
        }

        this.empty = orderTableRequest.isEmpty();
    }

    public boolean isUnableTableGroup() {
        if (isEmpty() || Objects.nonNull(getTableGroupId())) {
            return true;
        }
        return false;
    }

    public void ungroup() {
        orders.ungroup();
        groupBy(null);
    }

    public Order newOrder(LocalDateTime orderedTime, List<OrderLineItem> newOrderLineItems) {
        if (isEmpty()) {
            throw new IllegalArgumentException("빈테이블은 주문을 할수 없습니다.");
        }
        Order newOrder = Order.newOrder(this, orderedTime, newOrderLineItems);
        newOrder.reception();
        this.orders.newOrder(newOrder);
        return newOrder;
    }
}
