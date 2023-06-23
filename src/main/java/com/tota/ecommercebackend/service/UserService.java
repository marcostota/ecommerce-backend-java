package com.tota.ecommercebackend.service;

import com.tota.ecommercebackend.api.model.LoginBody;
import com.tota.ecommercebackend.api.model.PasswordResetBody;
import com.tota.ecommercebackend.api.model.RegistrationBody;
import com.tota.ecommercebackend.exception.EmailFailureException;
import com.tota.ecommercebackend.exception.EmailNotFoundException;
import com.tota.ecommercebackend.exception.UserAlreadyExistsException;
import com.tota.ecommercebackend.exception.UserNotVerifiedException;
import com.tota.ecommercebackend.model.LocalUser;
import com.tota.ecommercebackend.model.VerificationToken;
import com.tota.ecommercebackend.model.dao.LocalUserDAO;
import com.tota.ecommercebackend.model.dao.VerificationTokenDAO;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private LocalUserDAO localUserDAO;
    private EncryptionService encryptionService;
    private JWTService jwtService;
    private EmailService emailService;
    private VerificationTokenDAO verificationTokenDAO;

    public UserService(LocalUserDAO localUserDAO, EncryptionService encryptionService, JWTService jwtService, EmailService emailService, VerificationTokenDAO verificationTokenDAO) {
        this.localUserDAO = localUserDAO;
        this.encryptionService = encryptionService;
        this.jwtService = jwtService;
        this.emailService=emailService;
        this.verificationTokenDAO=verificationTokenDAO;
    }

    public LocalUser registerUser(RegistrationBody registrationBody) throws UserAlreadyExistsException, EmailFailureException {
        if (localUserDAO.findByEmailIgnoreCase(registrationBody.getEmail()).isPresent()
                || localUserDAO.findByUsernameIgnoreCase(registrationBody.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException();
        }
        LocalUser user = new LocalUser();
        user.setEmail(registrationBody.getEmail());
        user.setUsername(registrationBody.getUsername());
        user.setFirstName(registrationBody.getFirstName());
        user.setLastName(registrationBody.getLastName());
        user.setPassword(encryptionService.encryptPassword(registrationBody.getPassword()));
        VerificationToken verificationToken = createVerificationToken(user);
        emailService.sendVerificationEmail(verificationToken);
        return localUserDAO.save(user);
    }

    private VerificationToken createVerificationToken(LocalUser user){
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(jwtService.generateVerificationJWT(user));
        verificationToken.setCreatedTimeStamp(new Timestamp(System.currentTimeMillis()));
        verificationToken.setUser(user);
        user.getVerificationTokens().add(verificationToken);
        return verificationToken;
    }

    public String loginUser(LoginBody loginBody) throws UserNotVerifiedException, EmailFailureException {
        Optional<LocalUser> userOpt = localUserDAO.findByUsernameIgnoreCase(loginBody.getUsername());
        if(userOpt.isPresent()){
            LocalUser user = userOpt.get();
            if(encryptionService.verifyPassword(loginBody.getPassword(), user.getPassword())){
                if(user.isEmailVerified()){
                    return jwtService.generateJWT(user);
                }else{
                    List<VerificationToken> verificationTokens = user.getVerificationTokens();
                    boolean resend = verificationTokens.size() == 0 || verificationTokens.get(0).getCreatedTimeStamp().before(new Timestamp(System.currentTimeMillis() - (60*60+ 1000)));
                    if(resend){
                        VerificationToken verificationToken = createVerificationToken(user);
                        verificationTokenDAO.save(verificationToken);
                        emailService.sendVerificationEmail(verificationToken);
                    }
                    throw new UserNotVerifiedException(resend);
                }
            }
        }
        return null;
    }

    @Transactional
    public boolean verifyUser(String token){
        Optional<VerificationToken> opToken =  verificationTokenDAO.findByToken(token);
        if(opToken.isPresent()){
            VerificationToken verificationToken = opToken.get();
            LocalUser user = verificationToken.getUser();
            if(!user.isEmailVerified()){
                user.setEmailVerified(true);
                localUserDAO.save(user);
                verificationTokenDAO.deleteByUser(user);
                return true;
            }
        }
        return false;
    }

    public void forgotPassword(String email) throws EmailNotFoundException, EmailFailureException{
        Optional<LocalUser> opUser = localUserDAO.findByEmailIgnoreCase(email);
        if(opUser.isPresent()){
            LocalUser user = opUser.get();
            String token = jwtService.generatePasswordResetJWT(user);
            emailService.sendPasswordResetEmail(user, token);
        }else{
            throw new EmailNotFoundException();
        }
    }

    public void resetPassword(PasswordResetBody body){
        String email = jwtService.getResetPasswordEmail(body.getToken());
        Optional<LocalUser> opUser = localUserDAO.findByEmailIgnoreCase(email);
        if(opUser.isPresent()){
            LocalUser user = opUser.get();
            user.setPassword(encryptionService.encryptPassword(body.getPassword()));
            localUserDAO.save(user);
        }
    }


}
