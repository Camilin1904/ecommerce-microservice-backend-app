package com.selimhorri.app.service.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import com.selimhorri.app.domain.Payment;
import com.selimhorri.app.domain.PaymentStatus;
import com.selimhorri.app.dto.PaymentDto;
import com.selimhorri.app.dto.OrderDto;
import com.selimhorri.app.exception.wrapper.PaymentNotFoundException;
import com.selimhorri.app.repository.PaymentRepository;
import com.selimhorri.app.resource.impl.PaymentServiceImpl;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Payment testPayment;
    private PaymentDto testPaymentDto;
    private OrderDto testOrderDto;

    @BeforeEach
    void setUp() {
        testOrderDto = OrderDto.builder()
                .orderId(1)
                .orderDate(LocalDateTime.now())
                .orderDesc("Test Order")
                .orderFee(99.99)
                .build();

        testPayment = Payment.builder()
                .paymentId(1)
                .orderId(1)
                .isPayed(true)
                .paymentStatus(PaymentStatus.COMPLETED)
                .build();

        testPaymentDto = PaymentDto.builder()
                .paymentId(1)
                .isPayed(true)
                .paymentStatus(PaymentStatus.COMPLETED)
                .orderDto(testOrderDto)
                .build();
    }

    @Test
    void findAll_ShouldReturnListOfPayments_WhenPaymentsExist() {
        // Given
        Payment payment2 = Payment.builder()
                .paymentId(2)
                .orderId(2)
                .isPayed(false)
                .paymentStatus(PaymentStatus.IN_PROGRESS)
                .build();
        
        List<Payment> payments = Arrays.asList(testPayment, payment2);
        when(paymentRepository.findAll()).thenReturn(payments);
        when(restTemplate.getForObject(anyString(), eq(OrderDto.class))).thenReturn(testOrderDto);

        // When
        List<PaymentDto> result = paymentService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(PaymentStatus.COMPLETED, result.get(0).getPaymentStatus());
        assertEquals(PaymentStatus.IN_PROGRESS, result.get(1).getPaymentStatus());
        
        verify(paymentRepository, times(1)).findAll();
        verify(restTemplate, times(2)).getForObject(anyString(), eq(OrderDto.class));
    }

    @Test
    void findAll_ShouldReturnEmptyList_WhenNoPaymentsExist() {
        // Given
        when(paymentRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<PaymentDto> result = paymentService.findAll();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        verify(paymentRepository, times(1)).findAll();
        verify(restTemplate, never()).getForObject(anyString(), eq(OrderDto.class));
    }

    @Test
    void findById_ShouldReturnPayment_WhenPaymentExists() {
        // Given
        when(paymentRepository.findById(1)).thenReturn(Optional.of(testPayment));
        when(restTemplate.getForObject(anyString(), eq(OrderDto.class))).thenReturn(testOrderDto);

        // When
        PaymentDto result = paymentService.findById(1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getPaymentId());
        assertEquals(PaymentStatus.COMPLETED, result.getPaymentStatus());
        assertTrue(result.getIsPayed());
        assertNotNull(result.getOrderDto());
        assertEquals(1, result.getOrderDto().getOrderId());
        
        verify(paymentRepository, times(1)).findById(1);
        verify(restTemplate, times(1)).getForObject(anyString(), eq(OrderDto.class));
    }

    @Test
    void findById_ShouldThrowPaymentNotFoundException_WhenPaymentDoesNotExist() {
        // Given
        when(paymentRepository.findById(99)).thenReturn(Optional.empty());

        // When & Then
        PaymentNotFoundException exception = assertThrows(
                PaymentNotFoundException.class,
                () -> paymentService.findById(99)
        );
        
        assertEquals("Payment with id: 99 not found", exception.getMessage());
        verify(paymentRepository, times(1)).findById(99);
        verify(restTemplate, never()).getForObject(anyString(), eq(OrderDto.class));
    }

    @Test
    void save_ShouldReturnSavedPayment_WhenValidPaymentProvided() {
        // Given
        when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);

        // When
        PaymentDto result = paymentService.save(testPaymentDto);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getPaymentId());
        assertEquals(PaymentStatus.COMPLETED, result.getPaymentStatus());
        assertTrue(result.getIsPayed());
        
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void update_ShouldReturnUpdatedPayment_WhenValidPaymentProvided() {
        // Given
        testPaymentDto.setPaymentStatus(PaymentStatus.IN_PROGRESS);
        testPayment.setPaymentStatus(PaymentStatus.IN_PROGRESS);
        
        when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);

        // When
        PaymentDto result = paymentService.update(testPaymentDto);

        // Then
        assertNotNull(result);
        assertEquals(PaymentStatus.IN_PROGRESS, result.getPaymentStatus());
        assertEquals(1, result.getPaymentId());
        
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void deleteById_ShouldCallRepositoryDeleteById() {
        // Given
        doNothing().when(paymentRepository).deleteById(1);

        // When
        paymentService.deleteById(1);

        // Then
        verify(paymentRepository, times(1)).deleteById(1);
    }

    @Test
    void save_ShouldReturnPayment_WhenPaymentStatusIsNull() {
        // Given
        PaymentDto paymentWithNullStatus = PaymentDto.builder()
                .paymentId(1)
                .isPayed(false)
                .paymentStatus(null)
                .orderDto(testOrderDto)
                .build();

        Payment savedPayment = Payment.builder()
                .paymentId(1)
                .orderId(1)
                .isPayed(false)
                .paymentStatus(null)
                .build();

        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        // When
        PaymentDto result = paymentService.save(paymentWithNullStatus);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getPaymentId());
        assertNull(result.getPaymentStatus());
        assertFalse(result.getIsPayed());
        
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void update_ShouldReturnPayment_WhenPaymentStatusIsChanged() {
        // Given
        testPaymentDto.setPaymentStatus(PaymentStatus.NOT_STARTED);
        testPayment.setPaymentStatus(PaymentStatus.NOT_STARTED);
        
        when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);

        // When
        PaymentDto result = paymentService.update(testPaymentDto);

        // Then
        assertNotNull(result);
        assertEquals(PaymentStatus.NOT_STARTED, result.getPaymentStatus());
        assertEquals(1, result.getPaymentId());
        
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }
}
