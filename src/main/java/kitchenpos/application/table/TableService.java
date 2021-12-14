package kitchenpos.application.table;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kitchenpos.application.order.OrderService;
import kitchenpos.domain.table.OrderTable;
import kitchenpos.domain.table.OrderTableRepository;
import kitchenpos.dto.table.OrderTableDto;
import kitchenpos.exception.order.HasNotCompletionOrderException;
import kitchenpos.exception.table.EmptyOrderTableException;
import kitchenpos.exception.table.HasOtherTableGroupException;
import kitchenpos.exception.table.NegativeOfNumberOfGuestsException;
import kitchenpos.exception.table.NotFoundOrderTableException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TableService {
    private final OrderService orderService;
    private final OrderTableRepository orderTableRepository;

    public TableService(
        final OrderService orderService,
        final OrderTableRepository orderTableRepository
    ) {
        this.orderService = orderService;
        this.orderTableRepository = orderTableRepository;
    }

    @Transactional
    public OrderTableDto create(final OrderTableDto orderTable) {
        return OrderTableDto.of(orderTableRepository.save(OrderTable.of(orderTable.getNumberOfGuests(), orderTable.getEmpty())));
    }

    @Transactional(readOnly = true)
    public List<OrderTableDto> list() {
        return orderTableRepository.findAll().stream()
                                    .map(OrderTableDto::of)
                                    .collect(Collectors.toList());
    }

    @Transactional
    public OrderTableDto changeEmpty(final Long orderTableId, final OrderTableDto orderTable) {
        final OrderTable savedOrderTable = orderTableRepository.findById(orderTableId)
                                                                .orElseThrow(NotFoundOrderTableException::new);

        validationOfChangeEmpty(orderTableId, savedOrderTable);

        savedOrderTable.changeEmpty(orderTable.isEmpty());

        return OrderTableDto.of(savedOrderTable);
    }

    private void validationOfChangeEmpty(final Long orderTableId, final OrderTable savedOrderTable) {
        checkHasTableGroup(savedOrderTable);
        checkOrderStatusOfOrderTable(orderTableId);
    }

    private void checkOrderStatusOfOrderTable(final Long orderTableId) {
        if (orderService.isNotCompletionOrder(orderTableId)) {
            throw new HasNotCompletionOrderException();
        }
    }

    private void checkHasTableGroup(final OrderTable savedOrderTable) {
        if (savedOrderTable.hasTableGroup()) {
            throw new HasOtherTableGroupException();
        }
    }

    @Transactional
    public OrderTable changeNumberOfGuests(final Long orderTableId, final OrderTableDto orderTable) {
        final int numberOfGuests = orderTable.getNumberOfGuests();

        final OrderTable savedOrderTable = orderTableRepository.findById(orderTableId)
                                                                .orElseThrow(NotFoundOrderTableException::new);

        validationOfChangeNumberOfGuests(numberOfGuests, savedOrderTable);

        savedOrderTable.changeNumberOfGuests(numberOfGuests);

        return orderTableRepository.save(savedOrderTable);
    }

    private void validationOfChangeNumberOfGuests(final int numberOfGuests, final OrderTable orderTable) {
        checkPositiveOfNumberOfGuests(numberOfGuests);
        checkEmptyTable(orderTable);
    }

    private void checkEmptyTable(final OrderTable orderTable) {
        if (orderTable.isEmpty()) {
            throw new EmptyOrderTableException();
        }
    }

    private void checkPositiveOfNumberOfGuests(final int numberOfGuests) {
        if (numberOfGuests < 0) {
            throw new NegativeOfNumberOfGuestsException();
        }
    }
}