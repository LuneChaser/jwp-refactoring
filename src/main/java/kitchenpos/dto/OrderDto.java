package kitchenpos.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import kitchenpos.domain.order.Orders;
import kitchenpos.domain.order.OrderStatus;

public class OrderDto {
    private Long id;
    private Long orderTableId;
    private String orderStatus;
    private LocalDateTime orderedTime;
    private List<OrderLineItemDto> orderLineItems;

    protected OrderDto() {
    }

    private OrderDto(Long id, Long orderTableId, String orderStatus, LocalDateTime orderedTime, List<OrderLineItemDto> orderLineItems) {
        this.id = id;
        this.orderTableId = orderTableId;
        this.orderStatus = orderStatus;
        this.orderedTime = orderedTime;
        this.orderLineItems = orderLineItems;
    }

    public static OrderDto of(Long id, Long orderTableId, String orderStatus, LocalDateTime orderedTime, List<OrderLineItemDto> orderLineItems) {
        return new OrderDto(id, orderTableId, orderStatus, orderedTime, orderLineItems);
    }

    public static OrderDto of(Long orderTableId, String orderStatus, LocalDateTime orderedTime, List<OrderLineItemDto> orderLineItems) {
        return new OrderDto(null, orderTableId, orderStatus, orderedTime, orderLineItems);
    }

    public static OrderDto of(Orders order) {
        List<OrderLineItemDto> tempOrderLineItems = order.getOrderLineItems().stream()
                                                            .map(OrderLineItemDto::of)
                                                            .collect(Collectors.toList());

        if (order.getOrderStatus() == null) {
            return new OrderDto(order.getId(), order.getOrderTable().getId(), "", order.getOrderedTime(), tempOrderLineItems);
        }

        return new OrderDto(order.getId(), order.getOrderTable().getId(), order.getOrderStatus().name(), order.getOrderedTime(), tempOrderLineItems);
    }

    public Long getId() {
        return this.id;
    }

    public Long getOrderTableId() {
        return this.orderTableId;
    }

    public String getOrderStatus() {
        return this.orderStatus;
    }

    public LocalDateTime getOrderedTime() {
        return this.orderedTime;
    }

    public List<OrderLineItemDto> getOrderLineItems() {
        return this.orderLineItems;
    }

    public void changeOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus.name();
    }
}