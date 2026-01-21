package com.tp.order.service;
import com.tp.order.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tp.order.dto.CreateOrderRequest;
import com.tp.order.dto.OrderDTO;
import com.tp.order.dto.OrderItemDTO;
import com.tp.order.dto.OrderItemRequest;
import com.tp.order.entity.*;
import com.tp.order.exception.InsufficientStockException;
import com.tp.order.exception.ResourceNotFoundException;
import com.tp.order.repository.OrderRepository;
import com.tp.order.repository.ProductRepository;
import com.tp.order.repository.UserRepository;
import com.tp.order.strategy.DiscountCalculator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
    
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final DiscountCalculator discountCalculator;
    public OrderService(OrderRepository orderRepository, ProductRepository productRepository, UserRepository userRepository, DiscountCalculator discountCalculator) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.discountCalculator = discountCalculator;
    }
    
    @Transactional
    public OrderDTO createOrder(CreateOrderRequest request) {
        log.info("inside creating new order service>>>>>>");
        
        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        
        Order order = Order.builder().user(user).status(OrderStatus.PENDING).build();
        
        BigDecimal subtotal = BigDecimal.ZERO;
        List<Long> productIds=request.items().stream().map(OrderItemRequest::productId).toList();
        List<Product> products = productRepository.findAllById(productIds);
        checkProductAvailability(products, request.items());
        
        // Process each order item
        for (var itemRequest : request.items()) {
            
        	Product product = products.stream().filter(p -> p.getId().equals(itemRequest.productId())).findFirst()
					.get();
			BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(itemRequest.quantity()));
			subtotal = subtotal.add(itemTotal);

			OrderItem orderItem = OrderItem.builder().product(product).quantity(itemRequest.quantity())
					.unitPrice(product.getPrice()).discountApplied(BigDecimal.ZERO).totalPrice(itemTotal).build();

			order.addItem(orderItem);
            
            // Decrease product stock
            product.setQuantity(product.getQuantity() - itemRequest.quantity());
            productRepository.save(product);
        }
        
        log.info("Initial Order Total = " + subtotal);
        BigDecimal totalDiscount = discountCalculator.calculateDiscount(user.getRole(), subtotal);
        log.info("totalDiscount = " + totalDiscount);
        BigDecimal orderTotal = subtotal.subtract(totalDiscount);
        log.info("orderTotal  = " + orderTotal);
        
        if (totalDiscount.compareTo(BigDecimal.ZERO) > 0) {
            for (OrderItem item : order.getItems()) {
            	log.info("Order Product id = " + item.getProduct().getId() + " totalDiscount  = " + totalDiscount);

				BigDecimal itemDiscount = totalDiscount.multiply(item.getTotalPrice()).divide(subtotal, 2,
						RoundingMode.HALF_UP);

				log.info("Item Total Amount = " + item.getTotalPrice() + "itemDiscount  = " + itemDiscount);

				item.setDiscountApplied(itemDiscount);
				item.setTotalPrice(item.getTotalPrice().subtract(itemDiscount));
            }
        }
        
        order.setOrderTotal(orderTotal);
        
        Order savedOrder = orderRepository.save(order);
        log.info("Order created successfully with id: {}", savedOrder.getId());
        
        return mapToDTO(savedOrder);
    }
    
    @Transactional(readOnly = true)
    public OrderDTO getOrderById(Long id) {
        log.debug("Fetching the order details with id: {}", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found for input id: " + id));
        
        // Check if user has permission to view this order
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        if (!order.getUser().getUsername().equals(username) && 
            !authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new ResourceNotFoundException("Order not found for input id: " + id);
        }
        
        return mapToDTO(order);
    }
    
    @Transactional(readOnly = true)
    public Page<OrderDTO> getMyOrders(Pageable pageable) {
        log.debug("Fetching order details for current user");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        
        return orderRepository.findByUser(user, pageable)
                .map(this::mapToDTO);
    }
    
    @Transactional(readOnly = true)
    public Page<OrderDTO> getAllOrders(Pageable pageable) {
        log.debug("Fetching all the orders::::::::::");
        return orderRepository.findAll(pageable)
                .map(this::mapToDTO);
    }
    
    private void checkProductAvailability(List<Product> products, List<OrderItemRequest> orderItems) {
		for (OrderItemRequest item : orderItems) {
			Product product = products.stream().filter(p -> p.getId().equals(item.productId())).findFirst()
					.orElseThrow(() -> new InsufficientStockException("Product not found: " + item.productId()));

			if (product.getDeleted()) {
				throw new InsufficientStockException(String.valueOf(product.getId()));
			}

			if (product.getQuantity() < item.quantity() && product.getDeleted()) {
				throw new InsufficientStockException(String.valueOf(item.quantity()));
			}
		}
	}

    
    private OrderDTO mapToDTO(Order order) {
        List<OrderItemDTO> itemDTOs = order.getItems().stream()
                .map(item -> new OrderItemDTO(
                        item.getId(),
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getDiscountApplied(),
                        item.getTotalPrice()
                ))
                .toList();
        
        return new OrderDTO(
                order.getId(),
                order.getUser().getId(),
                order.getUser().getUsername(),
                itemDTOs,
                order.getOrderTotal(),
                order.getStatus().name(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}
