package com.nonk.spring_auth_api.user;

import com.nonk.spring_auth_api.api.RegistrationRequest;
import com.nonk.spring_auth_api.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.regex.Pattern;

@Service
public class UserService {

    private final Pattern validPasswordPattern = Pattern.compile("[a-zA-Z0-9_]{8,32}");
    private final Pattern validPhoneNumberPattern = Pattern.compile("(\\+7|8)[0-9]{10}");

    private final PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestorationCodeRepository restorationCodeRepository;

    @Autowired
    private MailSender mailSender;

    public UserService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public boolean isPasswordValid(String password) {
        return validPasswordPattern.matcher(password).matches();
    }

    public boolean isPhoneNumberValid(String phoneNumber) {
        return validPhoneNumberPattern.matcher(phoneNumber).matches();
    }

    /**
     * Match a user's password against a plaintext input.
     *
     * @return {@code true} if the hashed and plaintext passwords match; {@code false} otherwise.
     */
    public boolean matchPassword(User user, String plaintextPassword) {
        return isPasswordValid(plaintextPassword) &&
                passwordEncoder.matches(plaintextPassword, user.getPasswordHash());
    }

    /**
     * Encode a plaintext password.
     *
     * @param plaintextPassword the password to be encoded.
     * @return a 60-character encoded string.
     */
    public String encodePassword(String plaintextPassword) {
        return passwordEncoder.encode(plaintextPassword);
    }

    /**
     * Attempt to retrieve a user from the database.
     *
     * @param email the user's email.
     */
    public User retrieveUser(String email) throws UserNotFoundException {
        return userRepository.findById(email).orElseThrow(() -> new UserNotFoundException(email));
    }

    /**
     * Attempt to retrieve the restoration code associated with a user account.
     *
     * @param email the user's email.
     * @throws RestorationCodeExpiredException if the user has no restoration code assigned, or if
     *                                         the code is too old to be secure (a restoration code
     *                                         expires after 30 minutes).
     */
    public RestorationCode retrieveRestorationCode(String email) throws
            RestorationCodeExpiredException {
        RestorationCode restorationCode = restorationCodeRepository.findById(email)
                .orElseThrow(() -> new RestorationCodeExpiredException(email));

        Date now = new Date();

        if (now.after(restorationCode.getExpirationDate())) {
            throw new RestorationCodeExpiredException(email);
        }

        return restorationCode;
    }

    /**
     * Register a user account with given credentials.
     *
     * @param credentials the object storing the credentials.
     */
    public void register(RegistrationRequest credentials) throws InvalidCredentialsException,
            EmailInUseException, InvalidPasswordException {
        // Prevent overwriting an existing user.
        if (userRepository.findById(credentials.getEmail()).isPresent()) {
            throw new EmailInUseException(credentials.getEmail());
        }

        if (credentials.getFirstName().isBlank()) {
            throw new InvalidCredentialsException("Не заполнено поле имени");
        }

        if (credentials.getLastName().isBlank()) {
            throw new InvalidCredentialsException("Не заполнено поле фамилии");
        }

        if (!isPhoneNumberValid(credentials.getPhoneNumber())) {
            throw new InvalidCredentialsException("Указан недопустимый номер телефона");
        }

        if (!credentials.getEmail().contains("@")) {
            throw new InvalidCredentialsException("Указан недопустимый адрес электронной почты");
        }

        if (!isPasswordValid(credentials.getPassword())) {
            throw new InvalidPasswordException();
        }

        User user = new User();

        user.setFirstName(credentials.getFirstName());
        user.setLastName(credentials.getLastName());
        user.setPhoneNumber(credentials.getPhoneNumber());
        user.setEmail(credentials.getEmail());
        user.setPasswordHash(encodePassword(credentials.getPassword()));

        userRepository.save(user);
    }

    /**
     * Restore a user's password given their email.
     * <p>
     * If such user exists, they will be assigned a temporary restoration code
     * lasting 30 minutes, which is then sent to the specified email address.
     * The code can be redeemed with {@link #submitRestorationCode} unless it's
     * expired.
     *
     * @param email the email of the user whose password is to be reset.
     */
    public void restorePassword(String email) throws UserNotFoundException {
        User user = retrieveUser(email);

        Calendar expirationDate = Calendar.getInstance();
        expirationDate.setTime(new Date());
        expirationDate.add(Calendar.MINUTE, 30);

        RestorationCode restorationCode = new RestorationCode();
        restorationCode.setEmail(email);
        restorationCode.setCode(generateRestorationCode());
        restorationCode.setExpirationDate(expirationDate.getTime());

        restorationCodeRepository.save(restorationCode);
        sendRestorationCode(restorationCode.getCode(), user);
    }

    /**
     * @return a six-digit code padded with zeroes.
     */
    private String generateRestorationCode() {
        int code = new Random().nextInt(1000000);
        return String.format("%06d", code);
    }

    private void sendRestorationCode(String code, User destination) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("Восстановление пароля");
        message.setText("Ваш код восстановления пароля: " + code);
        message.setTo(destination.getEmail());
        mailSender.send(message);
    }

    /**
     * Update a user's password using a generated restoration code.
     *
     * @param email       the email by which the user is identified.
     * @param code        the restoration code to submit.
     * @param newPassword the new and updated password.
     */
    public void submitRestorationCode(String email, String code, String newPassword) throws
            UserNotFoundException, InvalidPasswordException, RestorationCodeExpiredException,
            RestorationCodeMismatchException {
        User user = retrieveUser(email);

        if (!isPasswordValid(newPassword)) {
            throw new InvalidPasswordException();
        }

        RestorationCode restorationCode = retrieveRestorationCode(email);

        if (!restorationCode.getCode().equals(code)) {
            throw new RestorationCodeMismatchException();
        }

        user.setPasswordHash(encodePassword(newPassword));
        userRepository.save(user);

        restorationCodeRepository.delete(restorationCode);
    }
}
