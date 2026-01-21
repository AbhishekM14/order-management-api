package com.tp.order.service;

import com.tp.order.dto.CreateOrderRequest;
import com.tp.order.dto.OrderDTO;
import com.tp.order.dto.OrderItemRequest;
import com.tp.order.entity.*;
import com.tp.order.exception.InsufficientStockException;
import com.tp.order.exception.ResourceNotFoundException;
import com.tp.order.repository.OrderRepository;
import com.tp.order.repository.ProductRepository;
import com.tp.order.repository.UserRepository;
import com.tp.order.strategy.DiscountCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DiscountCalculator discountCalculator;

    @InjectMocks
    private OrderService orderService;

    private User user;
    private Product product;

    @BeforeEach
    void setupSecurityContext() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "testuser",
                        "password",
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                )
        );

        user = User.builder()
                .id(1L)
                .username("testuser")
                .role(UserRole.USER)
                .build();

        product = Product.builder()
                .id(10L)
                .name("Test Product")
                .price(BigDecimal.valueOf(100))
                .quantity(10)
                .deleted(false)
                .build();
    }

    @Test
    void createOrder_success() {
        CreateOrderRequest request = new CreateOrderRequest(
                List.of(new OrderItemRequest(10L, 2))
        );

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(productRepository.findAllById(List.of(10L))).thenReturn(List.of(product));
        when(discountCalculator.calculateDiscount(eq(UserRole.USER), any()))
                .thenReturn(BigDecimal.valueOf(20));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        OrderDTO result = orderService.createOrder(request);

        assertNotNull(result);
        assertEquals(1, result.items().size());
        assertEquals(BigDecimal.valueOf(180), result.orderTotal());
        verify(productRepository).save(product);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void createOrder_insufficientStock_shouldThrowException() {
        product.setQuantity(1);

        CreateOrderRequest request = new CreateOrderRequest(
                List.of(new OrderItemRequest(10L, 5))
        );

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(productRepository.findAllById(List.of(10L))).thenReturn(List.of(product));

        assertThrows(
                InsufficientStockException.class,
                () -> orderService.createOrder(request)
        );
    }

    @Test
    void createOrder_userNotFound_shouldThrowException() {
        CreateOrderRequest request = new CreateOrderRequest(
                List.of(new OrderItemRequest(10L, 1))
        );

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> orderService.createOrder(request)
        );
    }

    @Test
    void getOrderById_success_forOwner() {
        Order order = Order.builder()
                .id(1L)
                .user(user)
                .status(OrderStatus.PENDING)
                .orderTotal(BigDecimal.valueOf(100))
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderDTO dto = orderService.getOrderById(1L);

        assertEquals(1L, dto.id());
        assertEquals("testuser", dto.username());
    }

    @Test
    void getOrderById_unauthorizedUser_shouldThrowException() {
        User anotherUser = User.builder()
                .id(2L)
                .username("other")
                .build();

        Order order = Order.builder()
                .id(1L)
                .user(anotherUser)
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(
                ResourceNotFoundException.class,
                () -> orderService.getOrderById(1L)
        );
    }

    @Test
    void getMyOrders_success() {
        Order order = Order.builder()
                .id(1L)
                .user(user)
                .orderTotal(BigDecimal.valueOf(100))
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(orderRepository.findByUser(eq(user), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(order)));

        Page<OrderDTO> result = orderService.getMyOrders(Pageable.unpaged());

        assertEquals(1, result.getTotalElements());
    }
}