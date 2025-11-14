package com.example.libreria.service;

import com.example.libreria.dto.ReservationRequestDTO;
import com.example.libreria.dto.ReservationResponseDTO;
import com.example.libreria.dto.ReturnBookRequestDTO;
import com.example.libreria.dto.UserResponseDTO;
import com.example.libreria.model.Book;
import com.example.libreria.model.Reservation;
import com.example.libreria.model.User;
import com.example.libreria.repository.BookRepository;
import com.example.libreria.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {
    
    @Mock
    private ReservationRepository reservationRepository;
    
    @Mock
    private BookRepository bookRepository;
    
    @Mock
    private BookService bookService;
    
    @Mock
    private UserService userService;
    
    @InjectMocks
    private ReservationService reservationService;
    
    private User testUser;
    private Book testBook;
    private Reservation testReservation;
    
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Juan Pérez");
        testUser.setEmail("juan@example.com");
        
        testBook = new Book();
        testBook.setExternalId(258027L);
        testBook.setTitle("The Lord of the Rings");
        testBook.setPrice(new BigDecimal("15.99"));
        testBook.setStockQuantity(10);
        testBook.setAvailableQuantity(5);
        
        testReservation = new Reservation();
        testReservation.setId(1L);
        testReservation.setUser(testUser);
        testReservation.setBook(testBook);
        testReservation.setRentalDays(7);
        testReservation.setStartDate(LocalDate.now());
        testReservation.setExpectedReturnDate(LocalDate.now().plusDays(7));
        testReservation.setDailyRate(new BigDecimal("15.99"));
        testReservation.setTotalFee(new BigDecimal("111.93"));
        testReservation.setStatus(Reservation.ReservationStatus.ACTIVE);
        testReservation.setCreatedAt(LocalDateTime.now());
    }
    
    @Test
    void testCreateReservation_Success() {
        // Teniendo
        ReservationRequestDTO reservationRequestDTO = new ReservationRequestDTO();
        reservationRequestDTO.setUserId(1L);
        reservationRequestDTO.setBookExternalId(258027L);
        reservationRequestDTO.setRentalDays(5);
        reservationRequestDTO.setStartDate(LocalDate.now());

        UserResponseDTO user = new UserResponseDTO();
        user.setEmail("juan@example.com");
        user.setId(1L);
        user.setName("pepe");

        // Cuando
        when(userService.getUserById(1L)).thenReturn(user);
        when(userService.getUserEntity(1L)).thenReturn(testUser);
        when(bookRepository.findByExternalId(258027L)).thenReturn(Optional.of(testBook));
        when(bookRepository.existsByExternalId(258027L)).thenReturn(true);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);

        ReservationResponseDTO response = reservationService.createReservation(reservationRequestDTO);

        assertNotNull(response);
        assertEquals(testReservation.getId(), response.getId());
    }
    
    @Test
    void testCreateReservation_BookNotAvailable() {
        // Teniendo
        ReservationRequestDTO reservationRequestDTO = new ReservationRequestDTO();
        reservationRequestDTO.setUserId(1L);
        reservationRequestDTO.setBookExternalId(258027L);
        reservationRequestDTO.setRentalDays(5);
        reservationRequestDTO.setStartDate(LocalDate.now());

        UserResponseDTO user = new UserResponseDTO();
        user.setEmail("juan@example.com");
        user.setId(1L);
        user.setName("pepe");

        Book unavailableBook = new Book();
        unavailableBook.setExternalId(258027L);
        unavailableBook.setTitle("The Lord of the Rings");
        unavailableBook.setPrice(new BigDecimal("15.99"));
        unavailableBook.setStockQuantity(10);
        unavailableBook.setAvailableQuantity(0);

        // Cuando
        when(userService.getUserById(1L)).thenReturn(user);
        when(bookRepository.existsByExternalId(258027L)).thenReturn(true);
        when(bookRepository.findByExternalId(258027L)).thenReturn(Optional.of(unavailableBook));

        // Entonces
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reservationService.createReservation(reservationRequestDTO);
        });
        assertEquals("No hay libros disponibles para reservar", exception.getMessage());
        verify(bookService, never()).decreaseAvailableQuantity(anyLong());
        verify(reservationRepository, never()).save(any(Reservation.class));
    }
    
    @Test
    void testReturnBook_OnTime() {
        // Teniendo
        ReturnBookRequestDTO returnRequest = new ReturnBookRequestDTO();
        returnRequest.setReturnDate(testReservation.getExpectedReturnDate());

        // Cuando
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);
        ReservationResponseDTO result = reservationService.returnBook(1L, returnRequest);

        // Entonces
        assertNotNull(result);
        assertEquals(Reservation.ReservationStatus.RETURNED, result.getStatus());
        assertEquals(BigDecimal.ZERO, result.getLateFee());
        assertEquals(returnRequest.getReturnDate(), result.getActualReturnDate());
        verify(bookService, times(1)).increaseAvailableQuantity(testBook.getExternalId());
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }
    
    @Test
    void testReturnBook_Overdue() {
        // Teniendo
        ReturnBookRequestDTO returnRequest = new ReturnBookRequestDTO();
        returnRequest.setReturnDate(testReservation.getExpectedReturnDate().plusDays(3)); // 3 días tarde

        // Cuando
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> {
            Reservation saved = invocation.getArgument(0);
            return saved;
        });
        ReservationResponseDTO result = reservationService.returnBook(1L, returnRequest);

        // Entonces
        assertNotNull(result);
        assertEquals(Reservation.ReservationStatus.RETURNED, result.getStatus());
        assertEquals(returnRequest.getReturnDate(), result.getActualReturnDate());
        BigDecimal expectedLateFee = new BigDecimal("7.1955");
        assertEquals(expectedLateFee, result.getLateFee());
        verify(bookService, times(1)).increaseAvailableQuantity(testBook.getExternalId());
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }
    
    @Test
    void testGetReservationById_Success() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));
        
        ReservationResponseDTO result = reservationService.getReservationById(1L);
        
        assertNotNull(result);
        assertEquals(testReservation.getId(), result.getId());
    }
    
    /*@Test
    void testGetAllReservations() {
        Reservation reservation2 = new Reservation();
        reservation2.setId(2L);
        
        when(reservationRepository.findAll()).thenReturn(Arrays.asList(testReservation, reservation2));
        
        List<ReservationResponseDTO> result = reservationService.getAllReservations();
        
        assertNotNull(result);
        assertEquals(2, result.size());
    }*/
    
    @Test
    void testGetReservationsByUserId() {
        when(reservationRepository.findByUserId(1L)).thenReturn(Arrays.asList(testReservation));
        
        List<ReservationResponseDTO> result = reservationService.getReservationsByUserId(1L);
        
        assertNotNull(result);
        assertEquals(1, result.size());
    }
    
    @Test
    void testGetActiveReservations() {
        when(reservationRepository.findByStatus(Reservation.ReservationStatus.ACTIVE))
                .thenReturn(Arrays.asList(testReservation));
        
        List<ReservationResponseDTO> result = reservationService.getActiveReservations();
        
        assertNotNull(result);
        assertEquals(1, result.size());
    }
}

