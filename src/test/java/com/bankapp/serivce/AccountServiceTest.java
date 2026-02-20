package com.bankapp.serivce;

import com.bankapp.dto.Account.CreateAccountResponseDto;
import com.bankapp.entity.Account;
import com.bankapp.entity.User;
import com.bankapp.exception.AlreadyDisabledOrNotPresent;
import com.bankapp.exception.AlreadyExistsException;
import com.bankapp.repository.AccountRepository;
import com.bankapp.repository.UserRepository;
import com.bankapp.service.AccountService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AccountService accountService;

    // -------------------------------------------------------
    // Helpers
    // -------------------------------------------------------

    private User buildUser(String email) {
        User user = new User();
        user.setEmail(email);
        user.setUsername("testuser");
        user.setActive(true);
        return user;
    }

    private MockedStatic<SecurityContextHolder> mockSecurityContext(User principal) {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class);
        mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(principal);
        return mockedStatic;
    }

    // =========================================================
    // createAccount()
    // =========================================================

    @Test
    @DisplayName("createAccount: deve criar conta com número 1 quando não existem contas cadastradas")
    void createAccount_whenNoAccountsExist_shouldCreateAccountWithNumberOne() {
        // Arrange
        User principal = buildUser("user@test.com");
        User userFromDb = buildUser("user@test.com");

        try (MockedStatic<SecurityContextHolder> ignored = mockSecurityContext(principal)) {
            when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(userFromDb));
            when(accountRepository.findMaxAccountNumber()).thenReturn(null);

            // Act
            CreateAccountResponseDto response = accountService.createAccount();

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.accountNumber()).isEqualTo(1);
            assertThat(response.balance()).isEqualByComparingTo(BigDecimal.ZERO);
            verify(userRepository).findByEmail("user@test.com");
            verify(accountRepository).findMaxAccountNumber();
        }
    }

    @Test
    @DisplayName("createAccount: deve criar conta com número incrementado quando já existem contas cadastradas")
    void createAccount_whenAccountsExist_shouldCreateAccountWithIncrementedNumber() {
        // Arrange
        User principal = buildUser("user@test.com");
        User userFromDb = buildUser("user@test.com");

        try (MockedStatic<SecurityContextHolder> ignored = mockSecurityContext(principal)) {
            when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(userFromDb));
            when(accountRepository.findMaxAccountNumber()).thenReturn(42);

            // Act
            CreateAccountResponseDto response = accountService.createAccount();

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.accountNumber()).isEqualTo(43);
            assertThat(response.balance()).isEqualByComparingTo(BigDecimal.ZERO);
            verify(accountRepository).findMaxAccountNumber();
        }
    }

    @Test
    @DisplayName("createAccount: deve associar a nova conta ao usuário autenticado")
    void createAccount_shouldAssociateNewAccountToAuthenticatedUser() {
        // Arrange
        User principal = buildUser("user@test.com");
        User userFromDb = buildUser("user@test.com");

        try (MockedStatic<SecurityContextHolder> ignored = mockSecurityContext(principal)) {
            when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(userFromDb));
            when(accountRepository.findMaxAccountNumber()).thenReturn(10);

            // Act
            accountService.createAccount();

            // Assert
            assertThat(userFromDb.getUserAccount()).isNotNull();
            assertThat(userFromDb.getUserAccount().getAccountNumber()).isEqualTo(11);
            assertThat(userFromDb.getUserAccount().isActive()).isTrue();
        }
    }

    @Test
    @DisplayName("createAccount: deve lançar NoSuchElementException quando usuário autenticado não for encontrado no banco")
    void createAccount_whenAuthenticatedUserNotFoundInDb_shouldThrowException() {
        // Arrange
        User principal = buildUser("user@test.com");

        try (MockedStatic<SecurityContextHolder> ignored = mockSecurityContext(principal)) {
            when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> accountService.createAccount())
                    .isInstanceOf(java.util.NoSuchElementException.class);

            verify(userRepository).findByEmail("user@test.com");
            verify(accountRepository, never()).findMaxAccountNumber();
        }
    }

    @Test
    @DisplayName("createAccount: saldo inicial da nova conta deve ser zero")
    void createAccount_shouldInitializeBalanceAsZero() {
        // Arrange
        User principal = buildUser("user@test.com");
        User userFromDb = buildUser("user@test.com");

        try (MockedStatic<SecurityContextHolder> ignored = mockSecurityContext(principal)) {
            when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(userFromDb));
            when(accountRepository.findMaxAccountNumber()).thenReturn(5);

            // Act
            CreateAccountResponseDto response = accountService.createAccount();

            // Assert
            assertThat(response.balance()).isEqualByComparingTo(BigDecimal.ZERO);
        }
    }

    // =========================================================
    // disableAccount()
    // =========================================================

    @Test
    @DisplayName("disableAccount: deve desativar conta ativa com sucesso")
    void disableAccount_whenAccountIsActive_shouldDisableSuccessfully() {
        // Arrange
        Long accountId = 1L;
        User owner = buildUser("owner@test.com");
        Account activeAccount = new Account(owner, 1);
        activeAccount.setAccountId(accountId);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(activeAccount));
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Account result = accountService.disableAccount(accountId);

        // Assert
        assertThat(result.isActive()).isFalse();
        verify(accountRepository).findById(accountId);
        verify(accountRepository).save(activeAccount);
    }

    @Test
    @DisplayName("disableAccount: deve lançar AlreadyDisabledOrNotPresent quando conta já está desativada")
    void disableAccount_whenAccountIsAlreadyDisabled_shouldThrowAlreadyDisabledOrNotPresent() {
        // Arrange
        Long accountId = 1L;
        User owner = buildUser("owner@test.com");
        Account disabledAccount = new Account(owner, 1);
        disabledAccount.setAccountId(accountId);
        disabledAccount.setActive(false);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(disabledAccount));

        // Act & Assert
        assertThatThrownBy(() -> accountService.disableAccount(accountId))
                .isInstanceOf(AlreadyDisabledOrNotPresent.class)
                .hasMessageContaining("Account already disabled or not present");

        verify(accountRepository).findById(accountId);
        verify(accountRepository, never()).save(any());
    }

    @Test
    @DisplayName("disableAccount: deve lançar AlreadyExistsException quando conta não for encontrada")
    void disableAccount_whenAccountNotFound_shouldThrowAlreadyExistsException() {
        // Arrange
        Long accountId = 99L;
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> accountService.disableAccount(accountId))
                .isInstanceOf(AlreadyExistsException.class)
                .hasMessageContaining("Account not present or already exists");

        verify(accountRepository).findById(accountId);
        verify(accountRepository, never()).save(any());
    }

    @Test
    @DisplayName("disableAccount: deve persistir a conta com isActive=false no repositório")
    void disableAccount_shouldPersistDisabledStateInRepository() {
        // Arrange
        Long accountId = 1L;
        User owner = buildUser("owner@test.com");
        Account activeAccount = new Account(owner, 1);
        activeAccount.setAccountId(accountId);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(activeAccount));
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        accountService.disableAccount(accountId);

        // Assert
        verify(accountRepository).save(argThat(account -> !account.isActive()));
    }

    @Test
    @DisplayName("disableAccount: deve retornar a conta atualizada com isActive=false")
    void disableAccount_shouldReturnUpdatedAccountWithActiveFalse() {
        // Arrange
        Long accountId = 2L;
        User owner = buildUser("owner@test.com");
        Account activeAccount = new Account(owner, 2);
        activeAccount.setAccountId(accountId);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(activeAccount));
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Account result = accountService.disableAccount(accountId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.isActive()).isFalse();
        assertThat(result.getAccountNumber()).isEqualTo(2);
    }
}

