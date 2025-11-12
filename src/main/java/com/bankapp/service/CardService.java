package com.bankapp.service;

import com.bankapp.data.LuhnAlgorithm;
import com.bankapp.dto.Card.CardCreateResponseDto;
import com.bankapp.entity.Account;
import com.bankapp.entity.Card;
import com.bankapp.entity.User;
import com.bankapp.repository.AccountRepository;
import com.bankapp.repository.CardRepository;
import com.bankapp.repository.UserRepository;
import com.github.javafaker.Faker;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.Random;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;


    public CardService(CardRepository cardRepository, AccountRepository accountRepository, UserRepository userRepository) {
        this.cardRepository = cardRepository;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public CardCreateResponseDto createCard(){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user = userRepository.findByEmail(user.getEmail()).orElseThrow(() -> new RuntimeException("User not found"));

        Account account = accountRepository.findById(user.getUserAccount().getAccountId()).orElseThrow();

        Faker faker = new Faker();

        String rawPan = faker.finance().creditCard();
        String pan = rawPan.replaceAll("\\D","");

        YearMonth expiryYM = YearMonth.now().plusMonths(new Random().nextInt(60) + 1);
        String expiry = String.format("%02d/%02d", expiryYM.getMonthValue(), expiryYM.getYear() % 100);

        if(!LuhnAlgorithm.validateLuhn(pan)){
            throw new IllegalStateException("Fail to generate credit card(Luhn)");
        }
        String cvv = faker.number().digits(3);
        Card newCard = new Card();
        newCard.setPan(pan);
        newCard.setCvv(cvv);
        newCard.setExpiry(expiry);
        newCard.setCardAccount(account);
        account.setCardAccount(newCard);
        cardRepository.save(newCard);
        return new CardCreateResponseDto(newCard.getPan(), newCard.getCvv(), newCard.getExpiry());

    }
}
