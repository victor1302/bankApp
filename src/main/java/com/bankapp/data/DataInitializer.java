package com.bankapp.data;

import com.bankapp.entity.Role;
import com.bankapp.entity.User;
import com.bankapp.repository.RoleRepository;
import com.bankapp.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public DataInitializer(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
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
                    user.setPhoneNumber("-");
                    user.setAddress("-");
                    user.setAge(99);
                    user.setUserRole(Set.of(adminRole));
                    userRepository.save(user);

                }
        );


    }
}
