package kitchenpos.product.domain;



import kitchenpos.common.domain.Price;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

import static java.util.Objects.isNull;

@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Embedded
    private Price price;

    protected Product() {
    }

    public Product(Long id, String name, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.price = new Price(price);
    }

    public Product(String name, BigDecimal price) {
        nameValidCheck(name);
        this.name = name;
        this.price = new Price(price);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price.value();
    }

    private void nameValidCheck(String name) {
        if (isNull(name)) {
            throw new IllegalArgumentException("상품 이름은 필수로 입력되어야 합니다.");
        }
    }

    public BigDecimal totalPrice(long quantity) {
        return BigDecimal.valueOf(quantity).multiply(getPrice());
    }
}