package com.bankapp.data;

import com.bankapp.entity.Account;
import com.bankapp.entity.Role;
import com.bankapp.entity.User;
import com.bankapp.entity.enums.AccountType;
import com.bankapp.entity.enums.UserType;
import com.bankapp.repository.AccountRepository;
import com.bankapp.repository.RoleRepository;
import com.bankapp.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;


    public DataInitializer(RoleRepository roleRepository, UserRepository userRepository, AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args)throws Exception{
        System.out.println("Inicializando data");

        //Inicializar as Roles
        if(roleRepository.count() == 0){
            Role basicRole = new Role();
            basicRole.setName(Role.Values.BASIC.name());
            basicRole.setName("BASIC");

            Role adminRole = new Role();
            adminRole.setName(Role.Values.ADMIN.name());
            adminRole.setName("ADMIN");
            roleRepository.saveAll(List.of(basicRole, adminRole));
            System.out.println("Roles Inicializadas com sucesso!");
        }


        var adminRole = roleRepository.findByName(Role.Values.ADMIN.name());
        var userAdmin = userRepository.findByUsername("admin");


        userAdmin.ifPresentOrElse(
                user -> {
                    System.out.println("Usuario já existe");
                },
                ()->{
                    var user = new User();
                    user.setUsername("admin");
                    user.setPassword(passwordEncoder.encode("123"));
                    user.setEmail("admin@admin.com");
                    user.setPhoneNumber("-");
                    user.setAddress("-");
                    user.setActive(true);
                    user.setAge(99);
                    user.setUserRole(Set.of(adminRole));

                    var account = new Account(user, 0);
                    account.setUserAccount(user);
                    account.setAccountType(AccountType.PERSONAL);
                    account.setCachedBalance(BigDecimal.valueOf(999999999));
                    account.setAccountNumber(0);
                    account.setActive(true);
                    user.setUserAccount(account);
                    userRepository.save(user);
                    accountRepository.save(account);

                }
        );
        createUsers();
    }
    protected void createUsers(){
        var basicRole = roleRepository.findByName(Role.Values.BASIC.name());
        //Creating two users
        var firstUser = new User();
        firstUser.setUsername("Victor");
        firstUser.setPassword(passwordEncoder.encode("123"));
        firstUser.setEmail("firstuser@gmail.com");
        firstUser.setPhoneNumber("");
        firstUser.setAddress("");
        firstUser.setActive(true);
        firstUser.setAge(20);
        firstUser.setUserRole(Set.of(basicRole));

        var account = new Account(firstUser, 1);
        account.setUserAccount(firstUser);
        account.setAccountType(AccountType.PERSONAL);
        account.setCachedBalance(BigDecimal.valueOf(99999999));
        account.setAccountNumber(1);
        account.setActive(true);
        firstUser.setUserAccount(account);
        userRepository.save(firstUser);
        accountRepository.save(account);

        var secondUser = new User();
        secondUser.setUsername("Victor");
        secondUser.setPassword(passwordEncoder.encode("123"));
        secondUser.setUserType(UserType.SELLER);
        secondUser.setEmail("seconduser@gmail.com");
        secondUser.setPhoneNumber("");
        secondUser.setAddress("");
        secondUser.setActive(true);
        secondUser.setAge(20);
        secondUser.setUserRole(Set.of(basicRole));

        var secondAccount = new Account(secondUser, 2);
        secondAccount.setUserAccount(secondUser);
        secondAccount.setAccountType(AccountType.MERCHANT);
        secondAccount.setCachedBalance(BigDecimal.valueOf(99999999));
        secondAccount.setAccountNumber(2);
        secondAccount.setActive(true);
        secondUser.setUserAccount(secondAccount);
        userRepository.save(secondUser);
        accountRepository.save(secondAccount);

    }

}
