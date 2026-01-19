package com.tp.order.service;

import com.tp.order.dto.CreateOrderRequest;
import com.tp.order.dto.OrderDTO;
import com.tp.order.dto.OrderItemRequest;
import com.tp.order.entity.*;
import com.tp.order.entity.Order;
import com.tp.order.exception.InsufficientStockException;
import com.tp.order.exception.ResourceNotFoundException;
import com.tp.order.repository.OrderRepository;
import com.tp.order.repository.ProductRepository;
import com.tp.order.repository.UserRepository;
import com.tp.order.strategy.DiscountCalculator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("john")
                .role(UserRole.USER)
                .build();

        product = Product.builder()
                .id(10L)
                .name("Laptop")
                .price(BigDecimal.valueOf(1000))
                .quantity(10)
                .deleted(false)
                .build();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("john", null, List.of())
        );
    }

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    // ================= CREATE ORDER =================

    @Test
    void createOrder_success() {
        // List of OrderItemRequest
        OrderItemRequest itemRequest = new OrderItemRequest(10L, 2);
        CreateOrderRequest request = new CreateOrderRequest(List.of(itemRequest));

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(discountCalculator.calculateDiscount(eq(UserRole.USER), any()))
                .thenReturn(BigDecimal.ZERO);
        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> {
                    Order o = invocation.getArgument(0);
                    o.setId(1L);
                    return o;
                });

        OrderDTO response = orderService.createOrder(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("john", response.username());
        assertEquals(BigDecimal.valueOf(2000), response.orderTotal());

        // Stock should decrease
        assertEquals(8, product.getQuantity());

        verify(productRepository).save(product);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void createOrder_insufficientStock() {
        OrderItemRequest itemRequest = new OrderItemRequest(10L, 20);
        CreateOrderRequest request = new CreateOrderRequest(List.of(itemRequest));

        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        assertThrows(InsufficientStockException.class,
                () -> orderService.createOrder(request));
    }

    @Test
    void createOrder_productNotFound() {
        OrderItemRequest itemRequest = new OrderItemRequest(99L, 1);
        CreateOrderRequest request = new CreateOrderRequest(List.of(itemRequest));

        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> orderService.createOrder(request));
    }

    // ================= GET ORDER BY ID =================

    @Test
    void getOrderById_success_owner() {
        Order order = Order.builder()
                .id(1L)
                .user(user)
                .status(OrderStatus.PENDING)
                .items(List.of())
                .orderTotal(BigDecimal.TEN)
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderDTO dto = orderService.getOrderById(1L);

        assertEquals(1L, dto.id());
        assertEquals("john", dto.username());
    }

    @Test
    void getOrderById_unauthorizedUser() {
        User otherUser = User.builder()
                .id(2L)
                .username("alice")
                .build();

        Order order = Order.builder()
                .id(1L)
                .user(otherUser)
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(ResourceNotFoundException.class,
                () -> orderService.getOrderById(1L));
    }

    // ================= GET MY ORDERS =================

    @Test
    void getMyOrders_success() {
        Pageable pageable = PageRequest.of(0, 5);

        Order order = Order.builder()
                .id(1L)
                .user(user)
                .items(List.of())
                .orderTotal(BigDecimal.TEN)
                .status(OrderStatus.PENDING)
                .build();

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(orderRepository.findByUser(eq(user), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(order)));

        Page<OrderDTO> page = orderService.getMyOrders(pageable);

        assertEquals(1, page.getTotalElements());
    }

    // ================= GET ALL ORDERS =================

    @Test
    void getAllOrders_success() {
        Pageable pageable = PageRequest.of(0, 5);

        Order order = Order.builder()
                .id(1L)
                .user(user)
                .items(List.of())
                .orderTotal(BigDecimal.TEN)
                .status(OrderStatus.PENDING)
                .build();

        when(orderRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(order)));

        Page<OrderDTO> page = orderService.getAllOrders(pageable);

        assertEquals(1, page.getTotalElements());
    }
}