package com.myntra.clone.order.Service;
import com.myntra.clone.cart.entity.CartItems;
import com.myntra.clone.cart.repository.CartRepository;
import com.myntra.clone.catalog.entity.ProductImage;
import com.myntra.clone.catalog.repository.ProductImageRepository;
import com.myntra.clone.order.dto.*;
import com.myntra.clone.order.entity.Order;
import com.myntra.clone.order.entity.OrderItem;
import com.myntra.clone.order.entity.OrderShippingAddress;
import com.myntra.clone.order.repository.OrderRepository;
import com.myntra.clone.order.repository.OrderShippingAddressRepository;
import com.myntra.clone.payment.dto.RazorpayOrderResult;
import com.myntra.clone.user.entity.Address;
import com.myntra.clone.user.entity.Users;
import com.myntra.clone.user.repository.AddressRepository;
import com.myntra.clone.user.repository.AuthRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.myntra.clone.payment.service.RazorpayService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private OrderRepository orderRepository;
    private OrderShippingAddressRepository shippingAddressRepository;
    private AuthRepository authRepository;
    private final CartRepository cartItemsRepository;
    private final AddressRepository addressRepository;
    private final RazorpayService razorpayService;
    private final ProductImageRepository productImageRepository;

    @Autowired
    public OrderService(
            OrderRepository orderRepository,
            OrderShippingAddressRepository shippingAddressRepository,
            AddressRepository addressRepository,
            AuthRepository authRepository,
            CartRepository cartItemsRepository,
            RazorpayService razorpayService,
            ProductImageRepository productImageRepository) {

        this.orderRepository = orderRepository;
        this.shippingAddressRepository = shippingAddressRepository;
        this.addressRepository = addressRepository;
        this.authRepository = authRepository;
        this.cartItemsRepository = cartItemsRepository;
        this.razorpayService = razorpayService;
        this.productImageRepository = productImageRepository;
    }

    @Transactional
    public PlaceOrderResponse placeOrder(
            String email,
            PlaceOrderRequest request) {

        Users user = authRepository.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Long userId = user.getId();

        if (request.paymentMethod() == null) {
            throw new RuntimeException("Payment method is required");
        }

        List<CartItems> cartItems =
                cartItemsRepository.findCartItemsWithProducts(userId);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Your cart is empty");
        }

        Address address = addressRepository
                .findByIdAndUserId(request.addressId(), userId)
                .orElseThrow(() ->
                        new RuntimeException("Address not found"));

        OrderShippingAddress shippingAddress =
                new OrderShippingAddress();

        shippingAddress.setRecipientName(address.getRecipientName());
        shippingAddress.setPhone(address.getPhone());
        shippingAddress.setStreetAddress(address.getStreetAddress());
        shippingAddress.setCity(address.getCity());
        shippingAddress.setState(address.getState());
        shippingAddress.setPincode(address.getPincode());

        shippingAddress =
                shippingAddressRepository.save(shippingAddress);

        BigDecimal subtotal = BigDecimal.ZERO;

        Order order = new Order();

        for (CartItems cartItem : cartItems) {

            int quantity = cartItem.getQuantity();

            var sku = cartItem.getSku();
            var product = sku.getProduct();

            if (sku.getStockQuantity() < quantity) {
                throw new RuntimeException(
                        "Insufficient stock for " + product.getTitle()
                );
            }

            if (quantity <= 0) {
                throw new RuntimeException("Invalid cart quantity");
            }

            BigDecimal unitPrice = product.getPrice();

            BigDecimal itemTotal = unitPrice.multiply(
                    BigDecimal.valueOf(quantity));

            subtotal = subtotal.add(itemTotal);

            if (request.paymentMethod() == PaymentMethod.COD) {
                sku.setStockQuantity(
                        sku.getStockQuantity() - quantity
                );
            }

            OrderItem orderItem = new OrderItem();

            orderItem.setProduct(product);
            orderItem.setProductName(product.getTitle());
            orderItem.setSku(String.valueOf(sku.getId()));
            orderItem.setUnitPrice(unitPrice);
            orderItem.setQuantity(quantity);
            orderItem.setTotalPrice(itemTotal);
            order.addItem(orderItem);
        }

        // 7. Calculate final amount
        BigDecimal discount = BigDecimal.ZERO;
        BigDecimal shippingFee = BigDecimal.ZERO;
        BigDecimal tax = BigDecimal.ZERO;

        BigDecimal total = subtotal
                .subtract(discount)
                .add(shippingFee)
                .add(tax);

        // 8. Set order details
        order.setOrderNumber("ORD-" +
                UUID.randomUUID().toString()
                        .substring(0, 8).toUpperCase());

        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentMethod(request.paymentMethod());
        order.setPaymentStatus(PaymentStatus.UNPAID);

        order.setSubtotalAmount(subtotal);
        order.setDiscountAmount(discount);
        order.setShippingFee(shippingFee);
        order.setTaxAmount(tax);
        order.setTotalAmount(total);

        order.setShippingAddress(shippingAddress);

        // 9. Save order and its items
        orderRepository.save(order);

        if (request.paymentMethod() == PaymentMethod.COD) {

            cartItemsRepository.deleteAllByUserId(userId);

            return new PlaceOrderResponse(
                    order.getOrderNumber(),
                    order.getStatus().name(),
                    order.getTotalAmount(),
                    "Order placed successfully with COD",
                    null,
                    null
            );
        }

        RazorpayOrderResult razorpayOrder = razorpayService.createOrder(
                        order.getTotalAmount(),
                        order.getOrderNumber()
                );

        order.setGatewayOrderId(razorpayOrder.orderId());

        orderRepository.save(order);


        return new PlaceOrderResponse(
                order.getOrderNumber(),
                order.getStatus().name(),
                order.getTotalAmount(),
                "Proceed to online payment",
                razorpayOrder.orderId(),
                razorpayOrder.keyId()
        );
    }


    public List<OrderResponse> getMyOrders(String email) {

        Users user = authRepository.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        List<Order> orders =
                orderRepository.findAllByUserIdOrderByCreatedAtDesc(
                        user.getId()
                );

        List<OrderResponse> response = new ArrayList<>();

        for (Order order : orders) {
            response.add(mapToResponse(order));
        }

        return response;
    }

    public OrderResponse getOrderDetails(String email, Long orderId) {

        Users user = authRepository.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Order order = orderRepository
                .findByIdAndUserId(orderId, user.getId())
                .orElseThrow(() ->
                        new RuntimeException("Order not found"));

        return mapToResponse(order);
    }

    public String cancelOrder(String email, Long orderId) {

        Users user = authRepository.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Order order = orderRepository
                .findByIdAndUserId(orderId, user.getId())
                .orElseThrow(() ->
                        new RuntimeException("Order not found"));

        if (order.getStatus() != OrderStatus.PENDING &&
                order.getStatus() != OrderStatus.CONFIRMED) {

            throw new RuntimeException(
                    "Order cannot be cancelled at this stage"
            );
        }

        order.setStatus(OrderStatus.CANCELLED);

        orderRepository.save(order);

        return "Order cancelled successfully";
    }

    private OrderResponse mapToResponse(Order order) {

        List<OrderItemResponse> items = new ArrayList<>();

        for (OrderItem item : order.getItems()) {

            String imageUrl = productImageRepository
                    .findFirstByProductIdOrderBySortOrderAsc(
                            item.getProduct().getId()
                    )
                    .map(ProductImage::getImageUrl)
                    .orElse(null);

            OrderItemResponse itemResponse = new OrderItemResponse(
                    item.getProduct().getId(),
                    item.getProductName(),
                    item.getSku(),
                    item.getUnitPrice(),
                    item.getQuantity(),
                    item.getTotalPrice(),
                    imageUrl
            );

            items.add(itemResponse);
        }



        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getStatus().name(),
                order.getPaymentMethod().name(),
                order.getPaymentStatus().name(),
                order.getSubtotalAmount(),
                order.getDiscountAmount(),
                order.getShippingFee(),
                order.getTaxAmount(),
                order.getTotalAmount(),
                order.getCreatedAt(),
                items
        );
    }
}
