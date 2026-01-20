package com.bankapp.data;

import com.bankapp.entity.Account;
import com.bankapp.entity.Role;
import com.bankapp.entity.User;
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

                    var account = new Account();
                    account.setUserAccount(user);
                    account.setCachedBalance(BigDecimal.valueOf(999999999));
                    account.setAccountNumber(0);
                    account.setActive(true);
                    user.setUserAccount(account);
                    userRepository.save(user);
                    accountRepository.save(account);

                }
        );


    }
}
