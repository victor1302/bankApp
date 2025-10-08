package com.bankapp.data;

import com.bankapp.entity.Role;
import com.bankapp.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;


    public DataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args)throws Exception{
        System.out.println("Inicializando data");

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
    }
}
